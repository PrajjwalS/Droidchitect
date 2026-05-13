package com.prajjwals.droidchitect.patch;

import com.prajjwals.droidchitect.amp.AmpState;

import java.util.ArrayList;
import java.util.List;

public class Patch {

    // =========================================================
    // METADATA
    // =========================================================

    public int version = 1;

    public String name = "New Patch";

    public String creator = "Droidchitect";

    public String about = "";

    public List<String> tags = new ArrayList<>();


    // =========================================================
    // AMPLIFIER
    // =========================================================

    public int voice;

    public int gain;
    public int volume;

    public int bass;
    public int middle;
    public int treble;

    public int isf;

    public int presence;
    public int resonance;


    // =========================================================
    // MODULATION
    // =========================================================

    public boolean modulationEnabled;

    public int modulationType;

    public int modulationParam1;
    public int modulationParam2;
    public int modulationParam3;
    public int modulationParam4;


    // =========================================================
    // DELAY
    // =========================================================

    public boolean delayEnabled;

    public int delayType;

    public int delayLevel;
    public int delayFeedback;
    public int delayTime;


    // =========================================================
    // REVERB
    // =========================================================

    public boolean reverbEnabled;

    public int reverbType;

    public int reverbLevel;
    public int reverbSize;


    // =========================================================
    // NOISE GATE
    // =========================================================

    public boolean noiseGateEnabled;

    public int noiseGateSensitivity;
    public int noiseGateAmount;


    @Override
    public String toString() {

        String[] voices = {
                "Clean Warm",
                "Clean Bright",
                "Crunch",
                "Super Crunch",
                "OD1",
                "OD2"
        };

        String voiceName = "Unknown";

        if (voice >= 0 && voice < voices.length) {
            voiceName = voices[voice];
        }

        String modTypeName = "Unknown";

        switch (modulationType) {

            case 0:
                modTypeName = "Phaser";
                break;

            case 1:
                modTypeName = "Chorus/Flanger";
                break;

            case 2:
                modTypeName = "Envelope";
                break;

            case 3:
                modTypeName = "Tremolo";
                break;
        }

        boolean harmonic =
                modulationParam2 == 1;

        String modExtra = "";

        switch (modulationType) {

            case 0:
                modExtra =
                        "mix=" + modulationParam1 +
                                ", depth=" + modulationParam3 +
                                ", speed=" + modulationParam4;
                break;

            case 1:
                modExtra =
                        "morph=" + modulationParam1 +
                                ", depth=" + modulationParam2 +
                                ", mix=" + modulationParam3 +
                                ", speed=" + modulationParam4;
                break;

            case 2:
                modExtra =
                        "sense=" + modulationParam1 +
                                ", depth=" + modulationParam3;
                break;

            case 3:
                modExtra =
                        "pitch/xover=" + modulationParam1 +
                                ", harmonic=" + harmonic +
                                ", depth=" + modulationParam3 +
                                ", speed=" + modulationParam4;
                break;
        }

        return "Patch[" +
                "name=" + name +
                ", creator=" + creator +
                ", version=" + version +
                "]\n" +

                "Amplifier[" +
                "voice=" + voice +
                " (" + voiceName + ")" +
                ", gain=" + gain +
                ", volume=" + volume +
                ", bass=" + bass +
                ", middle=" + middle +
                ", treble=" + treble +
                ", isf=" + isf +
                ", presence=" + presence +
                ", resonance=" + resonance +
                "]\n" +

                "Modulation[" +
                "enabled=" + modulationEnabled +
                ", type=" + modulationType +
                " (" + modTypeName + ")" +
                ", " + modExtra +
                "]\n" +

                "Delay[" +
                "enabled=" + delayEnabled +
                ", type=" + delayType +
                ", level=" + delayLevel +
                ", feedback=" + delayFeedback +
                ", time=" + delayTime +
                "]\n" +

                "Reverb[" +
                "enabled=" + reverbEnabled +
                ", type=" + reverbType +
                ", level=" + reverbLevel +
                ", size=" + reverbSize +
                "]\n" +

                "NoiseGate[" +
                "enabled=" + noiseGateEnabled +
                ", sensitivity=" + noiseGateSensitivity +
                ", amount=" + noiseGateAmount +
                "]";
    }


    /* This  function was basically for stress testing patch apply testing
    *  so that we can figure out how much delay between param change commands should be given
    * so that no USB packets are lost by the amp.
    * */
    public boolean roughlyEquals(AmpState state) {

        return

                voice == state.getVoice() &&

                        gain == state.getGain() &&
                        volume == state.getVolume() &&

                        bass == state.getBass() &&
                        middle == state.getMiddle() &&
                        treble == state.getTreble() &&

                        isf == state.getIsf() &&

                        presence == state.getPresence() &&
                        resonance == state.getResonance() &&

                        modulationEnabled ==
                                state.isModulationEnabled() &&

                        modulationType ==
                                state.getModulationType() &&

                        modulationParam1 ==
                                state.getModulationParam1() &&

                        modulationParam2 ==
                                state.getModulationParam2() &&

                        modulationParam3 ==
                                state.getModulationParam3() &&

                        modulationParam4 ==
                                state.getModulationParam4() &&

                        delayEnabled ==
                                state.isDelayEnabled() &&

                        delayType ==
                                state.getDelayType() &&

                        delayLevel ==
                                state.getDelayLevel() &&

                        delayFeedback ==
                                state.getDelayFeedback() &&

                        delayTime ==
                                state.getDelayTime() &&

                        reverbEnabled ==
                                state.isReverbEnabled() &&

                        reverbType ==
                                state.getReverbType() &&

                        reverbLevel ==
                                state.getReverbLevel() &&

                        reverbSize ==
                                state.getReverbSize() &&

                        noiseGateEnabled ==
                                state.isNoiseGateEnabled() &&

                        noiseGateSensitivity ==
                                state.getNoiseGateSensitivity() &&

                        noiseGateAmount ==
                                state.getNoiseGateAmount();
    }
}