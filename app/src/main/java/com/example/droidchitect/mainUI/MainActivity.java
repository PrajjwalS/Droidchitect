package com.example.droidchitect.mainUI;

import android.content.*;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droidchitect.R;
import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;
import com.example.droidchitect.usb.UsbConnectionManager;
import com.rejowan.rotaryknob.RotaryKnob;

public class MainActivity extends AppCompatActivity
        implements UsbConnectionManager.ConnectionListener,
        ShellController.NavigationListener {

    /* ======================== CLASS VARS =========================== */
    /* DEBUG STUFF */
    private static final String TAG = "DROIDCHITECT_UI";


    /* USB Connection */
    private UsbConnectionManager usbConnectionManager;


    /* AMP Controller obj*/
    private AmpController controller;


    /*AMP state */
    private AmpState ampState = new AmpState();


    /* Stuff that handles UI */
    private TextView status;
    private AmpPageController ampPageController;
    private ShellController shellController;

    enum Page {
        AMP,
        EFFECTS,
        PATCH
    }
    private Page currentPage;

    /* ====================== CLASS VARS END ========================== */





    /* ------------------------- USB CONNECTION LISTENERS ---------------------------- */
    /* These are the events we listen to from USB Connection Manager
     *
     * 1) On Connected        - when USB Connection is Made with all permissions to the Amp.
     * 2) On Disconnected     - when USB Connection is yanked away to the Amp
     * 3) On Amp State Change - when Amp State changes (software/hardware related config change).
     * */
    @Override
    public void onConnected() {
        Log.d(TAG, "USB Connected");
        if (shellController != null) {
            shellController.setConnected(true);
        }
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "USB Disconnected");
        if (shellController != null) {
            shellController.setConnected(false);
        }
    }

    @Override
    public void onAmpStateUpdated() {

        runOnUiThread(() -> {

            // only update if amp page visible
            // I would like to have this isAmpPageVisible coming from UI object actually TODO
            if (currentPage == Page.AMP && ampPageController != null) {
                ampPageController.refresh();
            }

            Log.d(TAG, "UI refreshed from state change read");
        });
    }

    /* -------------------------------------------------------------- */


    /* ------------------------ USB Handlers ------------------------ */
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
    /* -------------------------------------------------------------- */

    /* ----------------------UI HANDLERS-------------------------------- */

    /* Note that this part creates the Page Controllers object all over again
     *  And I believe we are fine with this small app for now.
     *  Maybe we can optimize it ..... but probably i need more UI knowledge of inflataion
     *  Currently, i dont see it as a big problem.
     * TODO look this over again
     *  */
    private void loadPage(int layoutId) {
        FrameLayout container = findViewById(R.id.main_container);

        // Clean up all the page .. no need to keep in memory what isnt needed.
        // This should work and be snappy enough because we have very small ui functionality.

        View pageView = getLayoutInflater().inflate(layoutId, container, false);
        container.removeAllViews();
        container.addView(pageView);

        if (layoutId == R.layout.amp_page) {

            currentPage = Page.AMP;
            ampPageController = new AmpPageController(
                    pageView,
                    controller,
                    ampState
            );
            ampPageController.init();

        } else if (layoutId == R.layout.effects_page) {
            currentPage = Page.EFFECTS;
        } else if (layoutId == R.layout.patch_page) {
            currentPage = Page.PATCH;
        } else {
            // well ... hmmm
        }

    };

    @Override
    public void onAmpSelected() {
        loadPage(R.layout.amp_page);
    }

    @Override
    public void onEffectsSelected() {
        loadPage(R.layout.effects_page);
    }

    @Override
    public void onPatchSelected() {
        loadPage(R.layout.patch_page);
    }

    @Override
    public void onConnectClicked() {
        usbConnectionManager.detectAndConnect();
    }
    /* -------------------------------------------------------------- */




    /* ------------ MAIN PART (Create and Destroy handlers) -------------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Create USB + Controller FIRST
        usbConnectionManager = new UsbConnectionManager(this, ampState);
        usbConnectionManager.setConnectionListener(this);
        controller = new AmpController(usbConnectionManager);

        // Actual UI Start
        shellController = new ShellController(
                findViewById(android.R.id.content),
                this
        );
        shellController.selectAmp();
        loadPage(R.layout.amp_page);

        // register usb receiver ... this ultimately starts the USB manager functionality.
        registerUsbReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try { unregisterReceiver(usbReceiver); } catch (Exception ignored) {}

        usbConnectionManager.disconnect();
    }
    /* -------------------------------------------------------------- */
}