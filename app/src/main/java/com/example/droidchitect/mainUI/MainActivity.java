package com.example.droidchitect.mainUI;

import android.content.*;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droidchitect.R;
import com.example.droidchitect.amp_protocol.AmpState;
import com.example.droidchitect.usb.UsbConnectionManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DROIDCHITECT_UI";

    private UsbConnectionManager usbConnectionManager;
    private AmpState ampState = new AmpState();

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

        Button btn = findViewById(R.id.btn_connect);
        btn.setOnClickListener(v -> usbConnectionManager.detectAndConnect());

        registerUsbReceiver();
    }

    private void registerUsbReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbConnectionManager.ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        if (Build.VERSION.SDK_INT >= 26) {
            registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
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