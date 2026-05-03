package com.example.droidchitect.mainUI;

import android.content.*;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droidchitect.R;
import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;
import com.example.droidchitect.usb.UsbConnectionManager;
import com.rejowan.rotaryknob.RotaryKnob;

public class MainActivity extends AppCompatActivity
        implements UsbConnectionManager.ConnectionListener {

    private static final String TAG = "DROIDCHITECT_UI";

    private UsbConnectionManager usbConnectionManager;
    private AmpState ampState = new AmpState();

    private AmpController controller;

    @Override
    public void onConnected() {
        Log.d(TAG, "UI notified: USB connected");
        // safe to use controller now

    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "UI notified: USB disconnected");
    }

    // This receiver is our gateway to understand USB events.
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (UsbConnectionManager.ACTION_USB_PERMISSION.equals(action)) {
                usbConnectionManager.handlePermissionResult();
            }

            else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                usbConnectionManager.handleDeviceDetached();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usbConnectionManager = new UsbConnectionManager(this, ampState);
        usbConnectionManager.setConnectionListener(this);
        controller = new AmpController(usbConnectionManager);

        //Button btn = findViewById(R.id.btn_connect);
        //btn.setOnClickListener(v -> usbConnectionManager.detectAndConnect());

        RotaryKnob knob = findViewById(R.id.knob_test);

        knob.setProgressChangeListener(progress -> {
            Log.d("KNOB", "Value: " + progress);
        });

        registerUsbReceiver();
    }

    private void registerUsbReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbConnectionManager.ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        if (Build.VERSION.SDK_INT >= 26) {
            registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }
        else {
            registerReceiver(usbReceiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(usbReceiver);
        } catch (Exception ignored) {}

        usbConnectionManager.disconnect();
    }
}