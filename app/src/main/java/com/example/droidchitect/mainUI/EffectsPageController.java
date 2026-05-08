package com.example.droidchitect.mainUI;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.droidchitect.R;
import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.rejowan.rotaryknob.RotaryKnob;

public class EffectsPageController {

    // =========================================================
    // CORE
    // =========================================================

    private final View root;

    private final AmpState ampState;
    private final AmpController controller;

    // =========================================================
    // EFFECT PAGE TRACKING
    // =========================================================

    private enum EffectPage {
        MODULATION,
        DELAY,
        REVERB,
        NOISE_GATE
    }

    private EffectPage currentEffect = EffectPage.DELAY;

    // =========================================================
    // TOP EFFECT TABS
    // =========================================================

    private final TextView tabMod;
    private final TextView tabDelay;
    private final TextView tabReverb;
    private final TextView tabNoiseGate;

    // =========================================================
    // EFFECT HEADER
    // =========================================================

    private final TextView effectTitle;
    private final MaterialSwitch effectSwitch;

    // =========================================================
    // DELAY UI
    // =========================================================

    private final View delayTypes;
    private final View delayControls;

    private final Button typeLinear;
    private final Button typeAnalogue;
    private final Button typeTape;
    private final Button typeMulti;

    private final RotaryKnob knobDelayLevel;
    private final RotaryKnob knobDelayFeedback;
    private final RotaryKnob knobDelayTime;

    // =========================================================
    // REVERB UI
    // =========================================================

    private final View reverbTypes;
    private final View reverbControls;

    private final Button typeRoom;
    private final Button typeHall;
    private final Button typeSpring;
    private final Button typePlate;

    private final RotaryKnob knobReverbLevel;

    private final RotaryKnob knobReverbSize;

    // =========================================================
    // NOISE GATE UI
    // =========================================================

    private final View noiseGateControls;

    private final RotaryKnob knobGateSensitivity;
    private final RotaryKnob knobGateAmount;

    // =========================================================
    // MODULATION UI
    // =========================================================

    private final View modTypes;
    private final View modControls;

    private final Button typePhaser;
    private final Button typeChorus;
    private final Button typeEnvelope;
    private final Button typeTremolo;

    private final RotaryKnob knobMod1;
    private final RotaryKnob knobMod2;
    private final RotaryKnob knobMod3;
    private final RotaryKnob knobMod4;

    private final MaterialSwitch switchModHarmonic;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EffectsPageController(
            View root,
            AmpState ampState,
            AmpController controller
    ) {

        this.root = root;
        this.ampState = ampState;
        this.controller = controller;

        // ================= TOP TABS =================

        tabMod = root.findViewById(R.id.tab_mod);
        tabDelay = root.findViewById(R.id.tab_delay);
        tabReverb = root.findViewById(R.id.tab_reverb);
        tabNoiseGate = root.findViewById(R.id.tab_gate);

        // ================= HEADER =================

        effectTitle = root.findViewById(R.id.effect_title);
        effectSwitch = root.findViewById(R.id.switch_effect_enabled);

        // ================= DELAY =================

        delayTypes = root.findViewById(R.id.layout_delay_types);
        delayControls = root.findViewById(R.id.layout_delay_controls);

        typeLinear = root.findViewById(R.id.type_linear);
        typeAnalogue = root.findViewById(R.id.type_analogue);
        typeTape = root.findViewById(R.id.type_tape);
        typeMulti = root.findViewById(R.id.type_multi);

        knobDelayLevel = root.findViewById(R.id.knob_delay_level);
        knobDelayFeedback = root.findViewById(R.id.knob_delay_feedback);
        knobDelayTime = root.findViewById(R.id.knob_delay_time);

        // ================= REVERB =================

        reverbTypes = root.findViewById(R.id.layout_reverb_types);
        reverbControls = root.findViewById(R.id.layout_reverb_controls);

        typeRoom = root.findViewById(R.id.type_room);
        typeHall = root.findViewById(R.id.type_hall);
        typeSpring = root.findViewById(R.id.type_spring);
        typePlate = root.findViewById(R.id.type_plate);

        knobReverbLevel = root.findViewById(R.id.knob_reverb_level);
        knobReverbSize = root.findViewById(R.id.knob_reverb_size);

        // ================= NOISE GATE =================

        noiseGateControls = root.findViewById(R.id.layout_noise_gate_controls);

        knobGateSensitivity = root.findViewById(R.id.knob_noise_gate_sensitivity);
        knobGateAmount = root.findViewById(R.id.knob_noise_gate_amount);

        // ================= MODULATION =================

        modTypes = root.findViewById(R.id.layout_mod_types);
        modControls = root.findViewById(R.id.layout_mod_controls);

        typePhaser = root.findViewById(R.id.type_phaser);
        typeChorus = root.findViewById(R.id.type_chorus);
        typeEnvelope = root.findViewById(R.id.type_envelope);
        typeTremolo = root.findViewById(R.id.type_tremolo);

        knobMod1 = root.findViewById(R.id.knob_mod_1);
        knobMod2 = root.findViewById(R.id.knob_mod_2);
        knobMod3 = root.findViewById(R.id.knob_mod_3);
        knobMod4 = root.findViewById(R.id.knob_mod_4);

        switchModHarmonic =
                root.findViewById(R.id.switch_mod_harmonic);

    }

    // =========================================================
    // INIT
    // =========================================================

    private int toKnobProgress(
            int value,
            int min,
            int max
    ) {

        return (int)Math.round(
                ((value - min) * 100.0) / (max - min)
        );
    }

    public void init() {

        setupEffectTabs();
        setupModulationTypeButtons();
        setupModulationHarmonicSwitch();
        setupDelayTypeButtons();
        setupReverbTypeButtons();

        setupEffectSwitch();

        initKnobs();

        showDelay();
    }

    // =========================================================
    // REFRESH
    // =========================================================

    public void refresh() {

        refreshSwitchUI();

        refreshModulationTypeUI();
        refreshModulationLayout();
        refreshModulationUI();

        refreshDelayTypeUI();
        refreshReverbTypeUI();

        refreshDelayUI();
        refreshReverbUI();
        refreshNoiseGateUI();
    }

    private void refreshModulationLayout() {

        int type = ampState.getModulationType();

        // default everything visible
        knobMod1.setVisibility(View.VISIBLE);
        knobMod2.setVisibility(View.VISIBLE);
        knobMod3.setVisibility(View.VISIBLE);
        knobMod4.setVisibility(View.VISIBLE);

        switchModHarmonic.setVisibility(View.GONE);

        // =====================================================
        // PHASER
        // =====================================================

        if (type == 0) {

            knobMod1.setLabelText("MIX");
            knobMod2.setVisibility(View.GONE);
            knobMod3.setLabelText("DEPTH");
            knobMod4.setLabelText("SPEED");
        }

        // =====================================================
        // CHORUS / FLANGER
        // =====================================================

        else if (type == 1) {

            knobMod1.setLabelText("MORPH");
            knobMod2.setLabelText("DEPTH");
            knobMod3.setLabelText("MIX");
            knobMod4.setLabelText("SPEED");
        }

        // =====================================================
        // ENVELOPE
        // =====================================================

        else if (type == 2) {

            knobMod1.setLabelText("SENSE");

            knobMod2.setVisibility(View.GONE);

            knobMod3.setLabelText("DEPTH");

            knobMod4.setVisibility(View.GONE);
        }

        // =====================================================
        // TREMOLO
        // =====================================================

        else if (type == 3) {

            boolean harmonic =
                    ampState.getModulationParam2() == 1;

            switchModHarmonic.setChecked(harmonic);

            if (harmonic) {
                knobMod1.setLabelText("XOVER");
            } else {
                knobMod1.setLabelText("PITCH");
            }

            knobMod2.setVisibility(View.GONE);

            knobMod3.setLabelText("DEPTH");

            knobMod4.setLabelText("SPEED");

            switchModHarmonic.setVisibility(View.VISIBLE);
        }
    }

    private void setupModulationHarmonicSwitch() {

        switchModHarmonic.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    int value = isChecked ? 1 : 0;

                    if (ampState.getModulationParam2() != value) {

                        controller.setModulationParam2(value);
                    }
                });
    }
    private void refreshModulationTypeUI() {

        unselectType(typePhaser);
        unselectType(typeChorus);
        unselectType(typeEnvelope);
        unselectType(typeTremolo);

        switch (ampState.getModulationType()) {

            case 0:
                selectType(typePhaser);
                break;

            case 1:
                selectType(typeChorus);
                break;

            case 2:
                selectType(typeEnvelope);
                break;

            case 3:
                selectType(typeTremolo);
                break;
        }
    }

    private void setupModulationTypeButtons() {

        typePhaser.setOnClickListener(v -> {

            if (ampState.getModulationType() != 0) {
                controller.setModulationType(0);
            }
        });

        typeChorus.setOnClickListener(v -> {

            if (ampState.getModulationType() != 1) {
                controller.setModulationType(1);
            }
        });

        typeEnvelope.setOnClickListener(v -> {

            if (ampState.getModulationType() != 2) {
                controller.setModulationType(2);
            }
        });

        typeTremolo.setOnClickListener(v -> {

            if (ampState.getModulationType() != 3) {
                controller.setModulationType(3);
            }
        });
    }



    // =========================================================
    // EFFECT TABS
    // =========================================================

    private void setupEffectTabs() {
        tabMod.setOnClickListener(v -> showModulation());
        tabDelay.setOnClickListener(v -> showDelay());
        tabReverb.setOnClickListener(v -> showReverb());
        tabNoiseGate.setOnClickListener(v -> showNoiseGate());
    }

    private void showModulation() {

        currentEffect = EffectPage.MODULATION;

        effectTitle.setText("Modulation");

        selectTab(tabMod);

        unselectTab(tabDelay);
        unselectTab(tabReverb);
        unselectTab(tabNoiseGate);

        // show modulation
        modTypes.setVisibility(View.VISIBLE);
        modControls.setVisibility(View.VISIBLE);

        // hide others
        delayTypes.setVisibility(View.GONE);
        delayControls.setVisibility(View.GONE);

        reverbTypes.setVisibility(View.GONE);
        reverbControls.setVisibility(View.GONE);

        noiseGateControls.setVisibility(View.GONE);

        refreshSwitchUI();
    }


    // =========================================================
    // DELAY PAGE
    // =========================================================

    private void showDelay() {

        currentEffect = EffectPage.DELAY;

        effectTitle.setText("Delay");

        selectTab(tabDelay);
        unselectTab(tabMod);
        unselectTab(tabReverb);
        unselectTab(tabNoiseGate);

        modTypes.setVisibility(View.GONE);
        modControls.setVisibility(View.GONE);

        delayTypes.setVisibility(View.VISIBLE);
        delayControls.setVisibility(View.VISIBLE);

        reverbTypes.setVisibility(View.GONE);
        reverbControls.setVisibility(View.GONE);

        noiseGateControls.setVisibility(View.GONE);
        noiseGateControls.setVisibility(View.GONE);

        refreshSwitchUI();
    }

    private void refreshDelayUI() {

        knobDelayLevel.setCurrentProgress(
                toKnobProgress(
                        ampState.getDelayLevel(),
                        0,
                        127
                )
        );

        knobDelayFeedback.setCurrentProgress(
                toKnobProgress(
                        ampState.getDelayFeedback(),
                        0,
                        31
                )
        );

        knobDelayTime.setCurrentProgress(
                toKnobProgress(
                        ampState.getDelayTime(),
                        100,
                        2000
                )
        );
    }

    // =========================================================
    // REVERB PAGE
    // =========================================================

    private void showReverb() {

        currentEffect = EffectPage.REVERB;

        effectTitle.setText("Reverb");

        selectTab(tabReverb);
        unselectTab(tabMod);
        unselectTab(tabDelay);
        unselectTab(tabNoiseGate);

        modTypes.setVisibility(View.GONE);
        modControls.setVisibility(View.GONE);

        delayTypes.setVisibility(View.GONE);
        delayControls.setVisibility(View.GONE);

        reverbTypes.setVisibility(View.VISIBLE);
        reverbControls.setVisibility(View.VISIBLE);

        noiseGateControls.setVisibility(View.GONE);
        noiseGateControls.setVisibility(View.GONE);

        refreshSwitchUI();
    }

    private void refreshReverbUI() {

        knobReverbLevel.setCurrentProgress(
                toKnobProgress(
                        ampState.getReverbLevel(),
                        0,
                        127
                )
        );

        knobReverbSize.setCurrentProgress(
                toKnobProgress(
                        ampState.getReverbSize(),
                        0,
                        31
                )
        );
    }

    // =========================================================
    // EFFECT ENABLE SWITCH
    // =========================================================

    private void setupEffectSwitch() {

        effectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (currentEffect == EffectPage.MODULATION) {
                if (ampState.isModulationEnabled() != isChecked) {
                    controller.toggleMod(isChecked);
                }
            } else if (currentEffect == EffectPage.DELAY) {

                if (ampState.isDelayEnabled() != isChecked) {
                    controller.toggleDelay(isChecked);
                }

            } else if (currentEffect == EffectPage.REVERB) {

                if (ampState.isReverbEnabled() != isChecked) {
                    controller.toggleReverb(isChecked);
                }
            } else if (currentEffect == EffectPage.NOISE_GATE)  {
                if (ampState.isNoiseGateEnabled() != isChecked) {
                    controller.toggleNoiseGate(isChecked);
                }
            }
        });
    }

    private void refreshSwitchUI() {

        boolean enabled = false;

        if (currentEffect == EffectPage.MODULATION) {
            enabled = ampState.isModulationEnabled();
        } else if (currentEffect == EffectPage.DELAY) {
            enabled = ampState.isDelayEnabled();
        } else if (currentEffect == EffectPage.REVERB) {
            enabled = ampState.isReverbEnabled();
        } else if (currentEffect == EffectPage.NOISE_GATE) {
            enabled = ampState.isNoiseGateEnabled();
        }

        if (effectSwitch.isChecked() != enabled) {
            effectSwitch.setChecked(enabled);
        }
    }

    // =========================================================
    // KNOBS
    // =========================================================

    private interface KnobCallback {
        void onChange(int value);
    }

    private void initKnobs() {

        // ================= DELAY =================

        // LEVEL -> 0-127
        setupKnob(knobDelayLevel, 0, 127, value -> {

            if (ampState.getDelayLevel() != value) {
                controller.setDelayLevel(value);
            }
        });

        // FEEDBACK -> 0-31
        setupKnob(knobDelayFeedback, 0, 31, value -> {

            if (ampState.getDelayFeedback() != value) {
                controller.setDelayFeedback(value);
            }
        });

        // TIME -> 100-2000 ms
        setupKnob(knobDelayTime, 100, 2000, value -> {

            if (ampState.getDelayTime() != value) {
                controller.setDelayTime(value);
            }
        });

        // ================= REVERB =================

        // LEVEL -> 0-127
        setupKnob(knobReverbLevel, 0, 127, value -> {

            if (ampState.getReverbLevel() != value) {
                controller.setReverbLevel(value);
            }
        });

        // SIZE -> 0-31
        setupKnob(knobReverbSize, 0, 31, value -> {

            if (ampState.getReverbSize() != value) {
                controller.setReverbSize(value);
            }
        });

        // ================= NOISE GATE =================

        setupKnob(knobGateSensitivity, 0, 127, value -> {

            if (ampState.getNoiseGateSensitivity() != value) {
                controller.setNoiseGateSensitivity(value);
            }
        });

        setupKnob(knobGateAmount, 0, 127, value -> {

            if (ampState.getNoiseGateAmount() != value) {
                controller.setNoiseGateAmount(value);
            }
        });


        // ================= MODULATION =================

        setupKnob(knobMod1, 0, 31, value -> {

            if (ampState.getModulationParam1() != value) {
                controller.setModulationParam1(value);
            }
        });

        setupKnob(knobMod2, 0, 127, value -> {

            if (ampState.getModulationParam2() != value) {
                controller.setModulationParam2(value);
            }
        });

        setupKnob(knobMod3, 0, 127, value -> {

            if (ampState.getModulationParam3() != value) {
                controller.setModulationParam3(value);
            }
        });

        setupKnob(knobMod4, 0, 127, value -> {

            if (ampState.getModulationParam4() != value) {
                controller.setModulationParam4(value);
            }
        });

    }

    private void refreshNoiseGateUI() {

        knobGateSensitivity.setCurrentProgress(
                toKnobProgress(
                        ampState.getNoiseGateSensitivity(),
                        0,
                        127
                )
        );

        knobGateAmount.setCurrentProgress(
                toKnobProgress(
                        ampState.getNoiseGateAmount(),
                        0,
                        127
                )
        );
    }

    private void setupKnob(
            RotaryKnob knob,
            int min,
            int max,
            KnobCallback callback) {

        knob.setProgressChangeListener(value -> {

            int adjusted = value;

            if (value <= 3) adjusted = 0;
            else if (value >= 97) adjusted = 100;

            if (adjusted != value) {
                knob.setCurrentProgress(adjusted);
                return;
            }

            int mappedValue = min + (int)Math.round(
                    (adjusted / 100.0) * (max - min)
            );

            callback.onChange(mappedValue);
        });
    }

    // =========================================================
    // DELAY TYPES
    // =========================================================

    private void setupDelayTypeButtons() {

        typeLinear.setOnClickListener(v -> {

            if (ampState.getDelayType() != 0) {
                controller.setDelayType(0);
            }
        });

        typeAnalogue.setOnClickListener(v -> {

            if (ampState.getDelayType() != 1) {
                controller.setDelayType(1);
            }
        });

        typeTape.setOnClickListener(v -> {

            if (ampState.getDelayType() != 2) {
                controller.setDelayType(2);
            }
        });

        typeMulti.setOnClickListener(v -> {

            if (ampState.getDelayType() != 3) {
                controller.setDelayType(3);
            }
        });
    }

    private void refreshDelayTypeUI() {

        unselectType(typeLinear);
        unselectType(typeAnalogue);
        unselectType(typeTape);
        unselectType(typeMulti);

        switch (ampState.getDelayType()) {

            case 0:
                selectType(typeLinear);
                break;

            case 1:
                selectType(typeAnalogue);
                break;

            case 2:
                selectType(typeTape);
                break;

            case 3:
                selectType(typeMulti);
                break;
        }
    }

    private void refreshReverbTypeUI() {

        unselectType(typeRoom);
        unselectType(typeHall);
        unselectType(typeSpring);
        unselectType(typePlate);

        switch (ampState.getReverbType()) {

            case 0:
                selectType(typeRoom);
                break;

            case 1:
                selectType(typeHall);
                break;

            case 2:
                selectType(typeSpring);
                break;

            case 3:
                selectType(typePlate);
                break;
        }
    }

    private void selectDelayType(Button selected) {

        unselectType(typeLinear);
        unselectType(typeAnalogue);
        unselectType(typeTape);
        unselectType(typeMulti);

        selectType(selected);
    }

    // =========================================================
    // REVERB TYPES
    // =========================================================

    private void setupReverbTypeButtons() {

        typeRoom.setOnClickListener(v -> {

            if (ampState.getReverbType() != 0) {
                controller.setReverbType(0);
            }

            selectReverbType(typeRoom);
        });

        typeHall.setOnClickListener(v -> {

            if (ampState.getReverbType() != 1) {
                controller.setReverbType(1);
            }

            selectReverbType(typeHall);
        });

        typeSpring.setOnClickListener(v -> {

            if (ampState.getReverbType() != 2) {
                controller.setReverbType(2);
            }

            selectReverbType(typeSpring);
        });

        typePlate.setOnClickListener(v -> {

            if (ampState.getReverbType() != 3) {
                controller.setReverbType(3);
            }

            selectReverbType(typePlate);
        });
    }

    private void selectReverbType(Button selected) {

        unselectType(typeRoom);
        unselectType(typeHall);
        unselectType(typeSpring);
        unselectType(typePlate);

        selectType(selected);
    }

    // =========================================================
    // TYPE BUTTON STYLING
    // =========================================================

    private void selectType(Button button) {

        button.setTextColor(
                root.getContext().getColor(R.color.text_primary)
        );

        button.setBackgroundTintList(
                ColorStateList.valueOf(
                        root.getContext().getColor(R.color.accent_orange)
                )
        );
    }

    private void unselectType(Button button) {

        button.setTextColor(
                root.getContext().getColor(R.color.text_primary)
        );

        button.setBackgroundTintList(
                ColorStateList.valueOf(
                        root.getContext().getColor(R.color.bg_secondary)
                )
        );
    }

    // =========================================================
    // TAB STYLING
    // =========================================================

    private void selectTab(TextView tab) {

        tab.setTextColor(
                root.getContext().getColor(R.color.text_primary)
        );

        tab.setBackgroundResource(R.drawable.nav_selected_bg);
    }

    private void unselectTab(TextView tab) {

        tab.setTextColor(
                root.getContext().getColor(R.color.text_secondary)
        );

        tab.setBackgroundColor(
                root.getContext().getColor(android.R.color.transparent)
        );
    }

    // =========================================================
    // UTILS
    // =========================================================

    private int toKnobProgress(int ampValue) {

        return (int) Math.round(
                ampValue * 100.0 / 127.0
        );
    }




    private void showNoiseGate() {

        currentEffect = EffectPage.NOISE_GATE;

        effectTitle.setText("Noise Gate");

        selectTab(tabNoiseGate);

        unselectTab(tabDelay);
        unselectTab(tabReverb);
        unselectTab(tabMod);

        modTypes.setVisibility(View.GONE);
        modControls.setVisibility(View.GONE);

        delayTypes.setVisibility(View.GONE);
        delayControls.setVisibility(View.GONE);

        reverbTypes.setVisibility(View.GONE);
        reverbControls.setVisibility(View.GONE);

        noiseGateControls.setVisibility(View.VISIBLE);
        noiseGateControls.setVisibility(View.VISIBLE);

        refreshSwitchUI();
    }



    private void refreshModulationUI() {

        knobMod1.setCurrentProgress(
                toKnobProgress(
                        ampState.getModulationParam1(),
                        0,
                        31
                )
        );

        knobMod2.setCurrentProgress(
                toKnobProgress(
                        ampState.getModulationParam2(),
                        0,
                        127
                )
        );

        knobMod3.setCurrentProgress(
                toKnobProgress(
                        ampState.getModulationParam3(),
                        0,
                        127
                )
        );

        knobMod4.setCurrentProgress(
                toKnobProgress(
                        ampState.getModulationParam4(),
                        0,
                        127
                )
        );
    }
}