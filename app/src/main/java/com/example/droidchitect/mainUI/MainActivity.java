package com.example.droidchitect.mainUI;

import android.content.*;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
    private AmpController controller;
    private AmpState ampState = new AmpState();

    private TextView status;

    // ================= CONNECTION =================
    @Override
    public void onConnected() {
        Log.d(TAG, "USB Connected");
        status.setText("Connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "USB Disconnected");
        status.setText("Disconnected");
    }

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

        // 🔥 IMPORTANT
        setContentView(R.layout.amp_page);

        usbConnectionManager = new UsbConnectionManager(this, ampState);
        usbConnectionManager.setConnectionListener(this);
        controller = new AmpController(usbConnectionManager);

        initTopBar();
        initKnobs();
        initVoices();
        initTabs();

        registerUsbReceiver();
    }

    // ================= TOP BAR =================
    private void initTopBar() {
        Button connect = findViewById(R.id.btn_connect);
        status = findViewById(R.id.status);

        connect.setOnClickListener(v -> {
            usbConnectionManager.detectAndConnect();
        });
    }

    // ================= KNOBS =================
    private void initKnobs() {

        setupKnob(R.id.knob_gain, value -> {
            ampState.setGain(value);
            controller.setGain(value);
        });

        setupKnob(R.id.knob_volume, value -> {
            ampState.setVolume(value);
            controller.setVolume(value);
        });

        setupKnob(R.id.knob_bass, value -> {
            ampState.setBass(value);
            //controller.setBass(value); // TODO
        });

        setupKnob(R.id.knob_middle, value -> {
            ampState.setMiddle(value);
            //controller.setMiddle(value); // TODO

        });

        setupKnob(R.id.knob_treble, value -> {
            ampState.setTreble(value);
            //controller.setTre(value); // TODO
        });

        setupKnob(R.id.knob_isf, value -> {
            ampState.setIsf(value);
            controller.setISF(value);
        });

        setupKnob(R.id.knob_presence, value -> {
            ampState.setPresence(value);
            controller.setPresence(value);
        });

        setupKnob(R.id.knob_resonance, value -> {
            ampState.setResonance(value);
            controller.setResonance(value);
        });
    }

    private void setupKnob(int id, KnobCallback callback) {
        RotaryKnob knob = findViewById(id);

        knob.setProgressChangeListener(value -> {
            Log.d(TAG, "Knob " + id + " -> " + value);
            callback.onChange(value);
        });
    }

    interface KnobCallback {
        void onChange(int value);
    }

    // ================= VOICES =================
    private void initVoices() {

        Button cleanWarm = findViewById(R.id.voice_clean_warm);
        Button cleanBright = findViewById(R.id.voice_clean_bright);
        Button crunch = findViewById(R.id.voice_crunch);
        Button superCrunch = findViewById(R.id.voice_super_crunch);
        Button od1 = findViewById(R.id.voice_od1);
        Button od2 = findViewById(R.id.voice_od2);

        Button[] all = {cleanWarm, cleanBright, crunch, superCrunch, od1, od2};

        setVoiceListener(cleanWarm, all, 0);
        setVoiceListener(cleanBright, all, 1);
        setVoiceListener(crunch, all, 2);
        setVoiceListener(superCrunch, all, 3);
        setVoiceListener(od1, all, 4);
        setVoiceListener(od2, all, 5);
    }

    private void setVoiceListener(Button btn, Button[] all, int voiceIndex) {

        btn.setOnClickListener(v -> {

            // UI highlight
            for (Button b : all) {
                b.setBackgroundTintList(
                        getColorStateList(R.color.voice_button_gray)
                );
            }

            btn.setBackgroundTintList(
                    getColorStateList(R.color.orange) // define this
            );

            // Logic
            ampState.setVoice(voiceIndex);
            controller.setVoice(voiceIndex);

            Log.d(TAG, "Voice -> " + voiceIndex);
        });
    }

    // ================= TABS =================
    private void initTabs() {

        TextView amp = findViewById(R.id.tab_amp);
        TextView effects = findViewById(R.id.tab_effects);
        TextView patch = findViewById(R.id.tab_live);

        amp.setOnClickListener(v -> selectTab(amp, effects, patch));
        effects.setOnClickListener(v -> selectTab(effects, amp, patch));
        patch.setOnClickListener(v -> selectTab(patch, amp, effects));
    }

    private void selectTab(TextView selected, TextView t2, TextView t3) {

        setSelected(selected);
        setUnselected(t2);
        setUnselected(t3);

        // later:
        // switchFragment(...)
    }

    private void setSelected(TextView t) {
        t.setTextColor(getColor(R.color.white));
        t.setBackgroundResource(R.drawable.nav_selected_bg);
    }

    private void setUnselected(TextView t) {
        t.setTextColor(getColor(R.color.gray));
        t.setBackgroundColor(getColor(android.R.color.transparent));
    }

    // ================= USB =================
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

        try { unregisterReceiver(usbReceiver); } catch (Exception ignored) {}

        usbConnectionManager.disconnect();
    }
}