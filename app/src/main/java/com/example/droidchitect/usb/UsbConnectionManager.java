package com.example.droidchitect.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.*;
import android.util.Log;

import com.example.droidchitect.amp.*;

import java.util.HashMap;

public class UsbConnectionManager {

    private static final String TAG = "USB_MANAGER";

    public static final String ACTION_USB_PERMISSION =
            "com.example.droidchitect.USB_PERMISSION";

    private static final int VENDOR_ID = 0x27D4;
    private static final int PRODUCT_ID = 0x0013;

    private final Context contextRef;
    private final UsbManager usbManager;
    private final AmpState ampState;

    private UsbConnectionContext context;

    private ConnectionState state = ConnectionState.DISCONNECTED;

    private boolean isReading = false;
    private Thread readThread;

    private UsbDevice pendingDevice;

    private ConnectionListener listener;
    // Interface to signal MainActivity (UI) that connection is established
    public interface ConnectionListener {
        void onConnected();
        void onDisconnected();
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.listener = listener;
    }


    public UsbConnectionManager(Context ctx, AmpState state) {
        this.contextRef = ctx;
        this.usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        this.ampState = state;
    }

    // ===== PUBLIC API =====

    public ConnectionState getState() {
        return state;
    }

    public boolean isConnected() {
        return state == ConnectionState.CONNECTED;
    }

    public void detectAndConnect() {

        UsbDevice device = findTargetDevice();

        if (device == null) {
            Log.d(TAG, "Target device not found.");
            return;
        }

        Log.d(TAG, "Device detected: " + device);

        if (usbManager.hasPermission(device)) {
            connect(device);
        } else {
            pendingDevice = device;
            requestPermission(device);
        }
    }

    public void handlePermissionResult() {

        if (pendingDevice == null) {
            Log.d(TAG, "No pending device.");
            return;
        }

        boolean granted = usbManager.hasPermission(pendingDevice);

        Log.d(TAG, "Permission result: " + granted);

        if (granted) {
            connect(pendingDevice);
        } else {
            Log.d(TAG, "Permission denied.");
        }

        pendingDevice = null;
    }

    public void handleDeviceDetached() {
        Log.d(TAG, "Device detached");
        disconnect();
    }

    public void send(byte[] packet) {
        if (!isConnected()) return;

        context.connection.bulkTransfer(
                context.endpointOut,
                packet,
                packet.length,
                1000
        );
    }

    // ===== INTERNAL =====

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

        Log.d(TAG, "Requesting USB permission");

        Intent intent = new Intent(ACTION_USB_PERMISSION);
        intent.setPackage(contextRef.getPackageName());

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                contextRef,
                device.getDeviceId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        usbManager.requestPermission(device, permissionIntent);
    }

    private void connect(UsbDevice device) {

        if (state != ConnectionState.DISCONNECTED) return;

        state = ConnectionState.CONNECTING;

        UsbDeviceConnection connection = usbManager.openDevice(device);
        if (connection == null) {
            state = ConnectionState.DISCONNECTED;
            return;
        }

        UsbInterface targetInterface = null;
        UsbEndpoint endpointIn = null;
        UsbEndpoint endpointOut = null;

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface intf = device.getInterface(i);

            if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_HID) {

                for (int j = 0; j < intf.getEndpointCount(); j++) {
                    UsbEndpoint ep = intf.getEndpoint(j);

                    if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                        endpointIn = ep;
                    } else {
                        endpointOut = ep;
                    }
                }

                if (endpointIn != null && endpointOut != null) {
                    targetInterface = intf;
                    break;
                }
            }
        }

        if (targetInterface == null) {
            connection.close();
            state = ConnectionState.DISCONNECTED;
            return;
        }

        connection.claimInterface(targetInterface, true);

        context = new UsbConnectionContext(
                device,
                connection,
                targetInterface,
                endpointIn,
                endpointOut
        );

        // Mark connection established
        state = ConnectionState.CONNECTED;
        Log.d(TAG, "USB CONNECTED");


        // Here is the real meat.
        // 1 - Flush interface buffer ( so as to remove older junk)
        // 2 - Start the reading thread.
        //      this will continuously read the the buffer for new messages
        //      and keep applying if configs are being changed.
        //      Note: New messages come when:
        //          1   hardware knobs are touched.
        //          2   software request init OR software changes amp settings
        //          3   First order of business, send init and get full amp settings
        //              to update amp state.
        flushInput();

        startReading();
        // INIT (0x81)
        Log.d(TAG, "Sending Init Message to AMP");
        send(BlackstarEncoder.buildOutsiderInit());

        // Now signal MainActivity that connection is up .. and amp can be controlled now
        if (listener != null) {
            listener.onConnected();
        }

    }

    public void disconnect() {

        if (state == ConnectionState.DISCONNECTED) return;

        Log.d(TAG, "USB DISCONNECT");

        state = ConnectionState.DISCONNECTED;

        stopReading();

        if (context != null && context.connection != null) {
            context.connection.close();
        }

        context = null;

        // signal MainActivity that connection is gone now, dont use Amp Controls
        if (listener != null) {
            listener.onDisconnected();
        }
    }

    private void startReading() {

        isReading = true;

        readThread = new Thread(() -> {
            byte[] buffer = new byte[64];
            while (isReading && context != null && context.connection != null) {

                int bytesRead = context.connection.bulkTransfer(
                        context.endpointIn,
                        buffer,
                        buffer.length,
                        1000
                );

                if (bytesRead > 0) {
                    // Decode incoming data
                    BlackstarDecoder.decode(buffer, bytesRead, ampState);
                    // Log structured state
                    Log.d(TAG, "STATE: " + ampState);
                }

                // IMPORTANT:
                // bytesRead <= 0 is NORMAL for HID → do nothing, just keep polling
            }

            Log.d(TAG, "Read thread exiting");
        });
        Log.d(TAG, "Starting Reading Thread");
        readThread.start();
    }

    private void stopReading() {
        isReading = false;

        if (readThread != null) {
            try {
                readThread.join(500);
            } catch (InterruptedException ignored) {}
        }
    }

    private void flushInput() {
        byte[] buffer = new byte[64];

        if (context == null) {
            Log.d(TAG, "1context is NULL");

        }

        if (context.connection == null) {
            Log.d(TAG, "2connection is NULL");

        }

        if (context.endpointIn == null) {
            Log.d(TAG, "3endpointIn is NULL");

        }


        while (context.connection.bulkTransfer(
                context.endpointIn,
                buffer,
                buffer.length,
                10
        ) > 0) {
            // discard
        }
    }
}