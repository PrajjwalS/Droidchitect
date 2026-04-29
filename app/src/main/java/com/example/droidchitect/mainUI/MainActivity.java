package com.example.droidchitect.mainUI;

import android.app.PendingIntent;
import android.content.*;
import android.hardware.usb.*;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droidchitect.R;
import com.example.droidchitect.amp_protocol.BlackstarEncoder;
import com.example.droidchitect.usb.UsbConnectionContext;

import com.example.droidchitect.amp_protocol.AmpState;
import com.example.droidchitect.amp_protocol.BlackstarDecoder;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DROIDCHITECT_USB_DEBUG";
    private static final int VENDOR_ID = 0x27D4;
    private static final int PRODUCT_ID = 0x0013;

    private static final String ACTION_USB_PERMISSION =
            "com.example.droidchitect.USB_PERMISSION";

    private UsbManager usbManager;

    // Holds current session
    private UsbConnectionContext usbContext;

    // Device awaiting permission
    private UsbDevice pendingDevice;

    // The Amp current config state
    private AmpState ampState = new AmpState();
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ACTION_USB_PERMISSION.equals(intent.getAction())) {
                return;
            }

            if (pendingDevice == null) {
                Log.d(TAG, "No pending device. Ignoring broadcast.");
                return;
            }

            boolean granted = usbManager.hasPermission(pendingDevice);
            Log.d(TAG, "Permission check (UsbManager): " + granted);

            if (granted) {
                openConnection(pendingDevice);
                pendingDevice = null;
            } else {
                Log.d(TAG, "Permission not granted.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usbManager = (UsbManager) getSystemService(USB_SERVICE);

        Button btn = findViewById(R.id.btn_connect);
        btn.setOnClickListener(v -> detectAndConnect());

        registerUsbReceiver();
    }

    private void registerUsbReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

        if (Build.VERSION.SDK_INT >= 26) {
            registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(usbReceiver, filter);
        }
    }

    private void detectAndConnect() {
        UsbDevice device = findTargetDevice();

        if (device == null) {
            Log.d(TAG, "Target USB device not found.");
            return;
        }

        Log.d(TAG, "Device detected: " + device);

        if (usbManager.hasPermission(device)) {
            Log.d(TAG, "Permission already granted.");
            openConnection(device);
        } else {
            pendingDevice = device;
            requestPermission(device);
        }
    }

    private UsbDevice findTargetDevice() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        for (UsbDevice device : deviceList.values()) {
            if (device.getVendorId() == VENDOR_ID &&
                    device.getProductId() == PRODUCT_ID) {
                return device;
            }
        }
        return null;
    }

    private void requestPermission(UsbDevice device) {
        Log.d(TAG, "Requesting USB permission.");

        Intent intent = new Intent(ACTION_USB_PERMISSION);
        intent.setPackage(getPackageName());

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                this,
                device.getDeviceId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        usbManager.requestPermission(device, permissionIntent);
    }

    private void openConnection(UsbDevice device) {
        BlackstarDecoder.reset(); // resetting it here to be sure that we dont read garbage after connection was open
        UsbDeviceConnection connection = usbManager.openDevice(device);

        if (connection == null) {
            Log.d(TAG, "Failed to open USB connection.");
            return;
        }

        UsbConnectionContext ctx = buildConnectionContext(device, connection);

        if (ctx == null) {
            Log.d(TAG, "No suitable interface found.");
            return;
        }

        usbContext = ctx;

        // Now we do the real thing.

        // before reading or writing flush the buffers on the interface
        flushInput();

        // start a reading thread to keep updating amp state from messages received from the amp
        //   These messages will be coming when hardware knobs are touched OR when some config is changed via software.
        //   So basically anytime some state is changed, or states is requested.
        Log.d(TAG, "USB connection established.");
        startReading(usbContext);

        // Send some init message so that we are in sync with current state of the amp.
        sendInitSequence();
        // UI may start from here.
    }

    private UsbConnectionContext buildConnectionContext(
            UsbDevice device,
            UsbDeviceConnection connection
    ) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);

            if (usbInterface.getInterfaceClass() != UsbConstants.USB_CLASS_HID) {
                continue;
            }

            UsbEndpoint in = null;
            UsbEndpoint out = null;

            for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                UsbEndpoint ep = usbInterface.getEndpoint(j);

                if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {
                    continue;
                }

                if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                    in = ep;
                } else if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    out = ep;
                }
            }

            if (in != null && out != null) {
                if (!connection.claimInterface(usbInterface, true)) {
                    return null;
                }

                UsbConnectionContext ctx = new UsbConnectionContext();
                ctx.device = device;
                ctx.connection = connection;
                ctx.usbInterface = usbInterface;
                ctx.endpointIn = in;
                ctx.endpointOut = out;

                return ctx;
            }
        }

        return null;
    }

    private void startReading(UsbConnectionContext ctx) {
        new Thread(() -> {
            byte[] buffer = new byte[64];

            while (true) {
                int bytesRead = ctx.connection.bulkTransfer(
                        ctx.endpointIn,
                        buffer,
                        buffer.length,
                        1000
                );

                if (bytesRead > 0) {

                    // Decode into structured state
                    BlackstarDecoder.decode(buffer, bytesRead, ampState);

                    // Log structured state instead of raw bytes
                    Log.d(TAG, "STATE: " + ampState);
                }
            }
        }).start();
    }

    private void flushInput() {
        byte[] buffer = new byte[64];
        while (usbContext.connection.bulkTransfer(
                usbContext.endpointIn, buffer, buffer.length, 10) > 0) {
            // discard
        }
    }

    private void sendPacket(byte[] packet) {
        if (usbContext == null || usbContext.connection == null) return;

        usbContext.connection.bulkTransfer(
                usbContext.endpointOut,
                packet,
                packet.length,
                1000
        );
    }
    private void sendInitSequence() {
        // 1)
        // 1) INIT (triggers device info + state from amp)
        try {Thread.sleep(5000);} catch (InterruptedException ignored) {}
        Log.d(TAG, "Sending Init Message to AMP");
        sendPacket(BlackstarEncoder.buildOutsiderInit());

        // temporary voice packet testing
        try {Thread.sleep(2000);} catch (InterruptedException ignored) {}
        Log.d(TAG, "Changing voice one by one");
        for (int i = 0; i < 6; i++) {

            sendPacket(BlackstarEncoder.buildVoice(i));
            try {Thread.sleep(2000);} catch (InterruptedException ignored) {}
        }
    }
}