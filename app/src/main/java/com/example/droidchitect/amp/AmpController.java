package com.example.droidchitect.amp;

import android.util.Log;

import com.example.droidchitect.amp.BlackstarConstants;
import com.example.droidchitect.amp.BlackstarEncoder;
import com.example.droidchitect.usb.UsbConnectionManager;

public class AmpController {

    private static final String TAG = "AMP_CONTROLLER";

    private final UsbConnectionManager usb;

    public AmpController(UsbConnectionManager usb) {
        this.usb = usb;
    }

    // ===== CORE SEND =====

    private void send(byte[] packet) {
        if (!usb.isConnected()) {
            Log.d(TAG, "Not connected. Ignoring command.");
            return;
        }

        usb.send(packet);
    }

    // ===== AMP CONTROLS =====
    public void setVoice(int voice) {

        if (voice < 0 || voice > 5) {
            return;
        }

        send(BlackstarEncoder.buildVoice(voice));
    }

    public void setGain(int gain) {
        send(BlackstarEncoder.buildGain(clamp(gain, 0, 127)));
    }

    public void setVolume(int volume) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.VOLUME,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(volume, 0, 127)
        ));
    }

    public void setIsf(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.ISF,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setPresence(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.PRESENCE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setResonance(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.RESONANCE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setBass(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.BASS,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setMiddle(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.MIDDLE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setTreble(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.TREBLE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }


    // ====================== EFFECTS ==============//

    // ===== MODULATION =====

    public void setModulationType(int value) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.MOD_TYPE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 3)
        ));
    }

    // PARAM 1 -> 0-31
    public void setModulationParam1(int value) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.MOD_1,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 31)
        ));
    }

    // PARAM 2 -> 0-127
    public void setModulationParam2(int value) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.MOD_2,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    // PARAM 3 -> 0-127
    public void setModulationParam3(int value) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.MOD_3,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    // PARAM 4 -> 0-127
    public void setModulationParam4(int value) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.MOD_4,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }


    public void toggleMod(boolean enabled) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.MOD_SWITCH,
                BlackstarConstants.CONTEXT_PATCH,
                enabled ? 1 : 0
        ));
    }

    public void toggleDelay(boolean enabled) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.DELAY_SWITCH,
                BlackstarConstants.CONTEXT_PATCH,
                enabled ? 1 : 0
        ));
    }

    public void toggleReverb(boolean enabled) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.REVERB_SWITCH,
                BlackstarConstants.CONTEXT_PATCH,
                enabled ? 1 : 0
        ));
    }

    public void setDelayTime(int ms) {
        send(BlackstarEncoder.buildDelayTime(ms));
    }

    public void setDelayType(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.DELAY_TYPE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 3)
        ));
    }

    public void setDelayLevel(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.DELAY_LEVEL,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setDelayFeedback(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.DELAY_FEEDBACK,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setReverbType(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.REVERB_TYPE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 3)
        ));
    }

    public void setReverbLevel(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.REVERB_LEVEL,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setReverbSize(int value) {
        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.REVERB_SIZE,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    // ===== NOISE GATE =====

    public void toggleNoiseGate(boolean enabled) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.NOISE_GATE_SWITCH,
                BlackstarConstants.CONTEXT_PATCH,
                enabled ? 1 : 0
        ));
    }

    public void setNoiseGateSensitivity(int value) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.NOISE_GATE_SENS,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    public void setNoiseGateAmount(int value) {

        send(BlackstarEncoder.buildParam(
                BlackstarConstants.Param.NOISE_GATE_AMOUNT,
                BlackstarConstants.CONTEXT_PATCH,
                clamp(value, 0, 127)
        ));
    }

    // ===== UTIL =====

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}