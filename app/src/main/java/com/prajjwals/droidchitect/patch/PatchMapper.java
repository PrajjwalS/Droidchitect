package com.prajjwals.droidchitect.patch;

import com.prajjwals.droidchitect.amp.AmpState;

public class PatchMapper {

    public static Patch fromAmpState(AmpState state) {

        Patch patch = new Patch();

        // =====================================================
        // AMPLIFIER
        // =====================================================

        patch.voice = state.getVoice();

        patch.gain = state.getGain();
        patch.volume = state.getVolume();

        patch.bass = state.getBass();
        patch.middle = state.getMiddle();
        patch.treble = state.getTreble();

        patch.isf = state.getIsf();

        patch.presence = state.getPresence();
        patch.resonance = state.getResonance();

        // =====================================================
        // MODULATION
        // =====================================================

        patch.modulationEnabled = state.isModulationEnabled();
        patch.modulationType = state.getModulationType();
        patch.modulationParam1 = state.getModulationParam1();
        patch.modulationParam2 = state.getModulationParam2();
        patch.modulationParam3 = state.getModulationParam3();
        patch.modulationParam4 = state.getModulationParam4();

        // =====================================================
        // DELAY
        // =====================================================

        patch.delayEnabled = state.isDelayEnabled();
        patch.delayType = state.getDelayType();
        patch.delayLevel = state.getDelayLevel();
        patch.delayFeedback = state.getDelayFeedback();
        patch.delayTime = state.getDelayTime();

        // =====================================================
        // REVERB
        // =====================================================

        patch.reverbEnabled = state.isReverbEnabled();
        patch.reverbType = state.getReverbType();
        patch.reverbLevel = state.getReverbLevel();
        patch.reverbSize = state.getReverbSize();

        // =====================================================
        // NOISE GATE
        // =====================================================

        patch.noiseGateEnabled = state.isNoiseGateEnabled();
        patch.noiseGateSensitivity = state.getNoiseGateSensitivity();
        patch.noiseGateAmount = state.getNoiseGateAmount();

        return patch;
    }
}