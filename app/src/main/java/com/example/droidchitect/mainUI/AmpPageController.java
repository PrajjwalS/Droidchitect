package com.example.droidchitect.mainUI;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.droidchitect.R;
import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;
import com.rejowan.rotaryknob.RotaryKnob;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AmpPageController {

    private static final String TAG = "AMP_PAGE";

    private final View root;
    private final AmpController controller;
    private final AmpState state;

    public AmpPageController(View root, AmpController controller, AmpState state) {
        this.root = root;
        this.controller = controller;
        this.state = state;
    }

    // ================= INIT =================
    public void init() {
        initKnobs(); // these are UI object init
        initVoices(); // these are UI object init
        refresh();  // refresh with the current AMP State
    }

    // ================= REFRESH =================
    public void refresh() {
        applyStateToUI();
        applyVoiceToUI();
    }

    // ================= KNOBS =================
    private void initKnobs() {
        setupKnob(R.id.knob_gain, controller::setGain, state::getGain);
        setupKnob(R.id.knob_volume, controller::setVolume, state::getVolume);
        setupKnob(R.id.knob_bass, controller::setBass, state::getBass);
        setupKnob(R.id.knob_middle, controller::setMiddle, state::getMiddle);
        setupKnob(R.id.knob_treble, controller::setTreble, state::getTreble);
        setupKnob(R.id.knob_isf, controller::setIsf, state::getIsf);
        setupKnob(R.id.knob_presence, controller::setPresence, state::getPresence);
        setupKnob(R.id.knob_resonance, controller::setResonance, state::getResonance);
    }

    private void setupKnob(int id,
                           Consumer<Integer> setter,
                           Supplier<Integer> getter) {

        RotaryKnob knob = root.findViewById(id);

        knob.setProgressChangeListener(value -> {

            int adjusted = value;

            // Edge snapping (UI scale 0–100)
            if (value <= 3) adjusted = 0;
            else if (value >= 97) adjusted = 100;

            if (adjusted != value) {
                knob.setCurrentProgress(adjusted);
                return;
            }

            int ampValue = (int) Math.round(adjusted * 127.0 / 100.0);

            if (getter.get() != ampValue) {
                setter.accept(ampValue);
            }
        });
    }

    private void applyStateToUI() {

        setKnob(R.id.knob_gain, state.getGain());
        setKnob(R.id.knob_volume, state.getVolume());
        setKnob(R.id.knob_bass, state.getBass());
        setKnob(R.id.knob_middle, state.getMiddle());
        setKnob(R.id.knob_treble, state.getTreble());
        setKnob(R.id.knob_isf, state.getIsf());
        setKnob(R.id.knob_presence, state.getPresence());
        setKnob(R.id.knob_resonance, state.getResonance());
    }

    private void setKnob(int id, int ampValue) {
        RotaryKnob knob = root.findViewById(id);

        int uiValue = (int) Math.round(ampValue * 100.0 / 127.0);

        int current = knob.getCurrentProgress();

        // 🔥 prevent flicker
        if (Math.abs(current - uiValue) >= 2) {
            knob.setCurrentProgress(uiValue);
        }
    }

    // ================= VOICES =================
    private void initVoices() {

        Button cleanWarm = root.findViewById(R.id.voice_clean_warm);
        Button cleanBright = root.findViewById(R.id.voice_clean_bright);
        Button crunch = root.findViewById(R.id.voice_crunch);
        Button superCrunch = root.findViewById(R.id.voice_super_crunch);
        Button od1 = root.findViewById(R.id.voice_od1);
        Button od2 = root.findViewById(R.id.voice_od2);

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

            for (Button b : all) {
                b.setBackgroundTintList(
                        root.getContext().getColorStateList(R.color.voice_button_gray)
                );
            }

            btn.setBackgroundTintList(
                    root.getContext().getColorStateList(R.color.orange)
            );

            if (state.getVoice() != voiceIndex) {
                controller.setVoice(voiceIndex);
            }

            Log.d(TAG, "Voice -> " + voiceIndex);
        });
    }

    private void applyVoiceToUI() {

        int voice = state.getVoice();

        Button[] all = {
                root.findViewById(R.id.voice_clean_warm),
                root.findViewById(R.id.voice_clean_bright),
                root.findViewById(R.id.voice_crunch),
                root.findViewById(R.id.voice_super_crunch),
                root.findViewById(R.id.voice_od1),
                root.findViewById(R.id.voice_od2)
        };

        for (Button b : all) {
            b.setBackgroundTintList(
                    root.getContext().getColorStateList(R.color.voice_button_gray)
            );
        }

        if (voice >= 0 && voice < all.length) {
            all[voice].setBackgroundTintList(
                    root.getContext().getColorStateList(R.color.orange)
            );
        }
    }
}