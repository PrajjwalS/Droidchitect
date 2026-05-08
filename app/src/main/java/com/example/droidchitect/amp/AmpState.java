package com.example.droidchitect.amp;

public class AmpState {
    public Amplifier amplifier = new Amplifier();
    public Effects effects = new Effects();

    public static class Amplifier {
        public int voice;
        public int gain;
        public int volume;
        public int bass;
        public int middle;
        public int treble;
        public int isf;
        public int tvp;
        public int presence;
        public int resonance;
    }

    public static class Effects {
        public Delay delay = new Delay();
        public Reverb reverb = new Reverb();
        public Modulation modulation = new Modulation();

        public NoiseGate noiseGate = new NoiseGate();
    }

    public static class Delay {
        public int type;
        public int adjust1;
        public int adjust2;
        public int level;
        public int tempo;
        public boolean enabled;
    }

    public static class Reverb {
        public int type;
        public int adjust1;
        public int adjust2;
        public int level;
        public boolean enabled;
    }

    public static class Modulation {
        public int type;
        public int adjust1;
        public int adjust2;
        public int level;
        public int rate;
        public boolean enabled;
    }

    public static class NoiseGate {
        public int sensitivity;
        public int amount;
        public boolean enabled;
    }

    @Override
    public String toString() {

        String voiceName = "Unknown";

        if (amplifier.voice >= 0 &&
                amplifier.voice < BlackstarConstants.VOICES.length) {

            voiceName = BlackstarConstants.VOICES[amplifier.voice];
        }

        String modTypeName = "Unknown";

        switch (effects.modulation.type) {

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
                effects.modulation.adjust2 == 1;

        String modExtra = "";

        switch (effects.modulation.type) {

            case 0:
                modExtra =
                        "mix=" + effects.modulation.adjust1 +
                                ", depth=" + effects.modulation.level +
                                ", speed=" + effects.modulation.rate;
                break;

            case 1:
                modExtra =
                        "morph=" + effects.modulation.adjust1 +
                                ", depth=" + effects.modulation.adjust2 +
                                ", mix=" + effects.modulation.level +
                                ", speed=" + effects.modulation.rate;
                break;

            case 2:
                modExtra =
                        "sense=" + effects.modulation.adjust1 +
                                ", depth=" + effects.modulation.level;
                break;

            case 3:
                modExtra =
                        "pitch/xover=" + effects.modulation.adjust1 +
                                ", harmonic=" + harmonic +
                                ", depth=" + effects.modulation.level +
                                ", speed=" + effects.modulation.rate;
                break;
        }

        return "Amplifier[" +
                "voice=" + amplifier.voice + " (" + voiceName + ")" +
                ", gain=" + amplifier.gain +
                ", volume=" + amplifier.volume +
                ", bass=" + amplifier.bass +
                ", middle=" + amplifier.middle +
                ", treble=" + amplifier.treble +
                ", isf=" + amplifier.isf +
                ", presence=" + amplifier.presence +
                ", resonance=" + amplifier.resonance +
                "]\n" +

                "Modulation[" +
                "enabled=" + effects.modulation.enabled +
                ", type=" + effects.modulation.type +
                " (" + modTypeName + ")" +
                ", " + modExtra +
                "]\n" +

                "Delay[" +
                "enabled=" + effects.delay.enabled +
                ", type=" + effects.delay.type +
                ", level=" + effects.delay.level +
                ", tempo=" + effects.delay.tempo +
                ", adj1=" + effects.delay.adjust1 +
                ", adj2=" + effects.delay.adjust2 +
                "]\n" +

                "Reverb[" +
                "enabled=" + effects.reverb.enabled +
                ", type=" + effects.reverb.type +
                ", level=" + effects.reverb.level +
                ", adj1=" + effects.reverb.adjust1 +
                ", adj2=" + effects.reverb.adjust2 +
                "]\n" +

                "NoiseGate[" +
                "enabled=" + effects.noiseGate.enabled +
                ", sensitivity=" + effects.noiseGate.sensitivity +
                ", amount=" + effects.noiseGate.amount +
                "]";
    }

    // ================= AMPLIFIER SETTERS =================

    public void setVoice(int value) {
        amplifier.voice = value;
    }

    public void setGain(int value) {
        amplifier.gain = value;
    }

    public void setVolume(int value) {
        amplifier.volume = value;
    }

    public void setBass(int value) {
        amplifier.bass = value;
    }

    public void setMiddle(int value) {
        amplifier.middle = value;
    }

    public void setTreble(int value) {
        amplifier.treble = value;
    }

    public void setIsf(int value) {
        amplifier.isf = value;
    }

    public void setPresence(int value) {
        amplifier.presence = value;
    }

    public void setResonance(int value) {
        amplifier.resonance = value;
    }

    public int getVoice() {
        return amplifier.voice;
    }

    public int getGain() {
        return amplifier.gain;
    }

    public int getVolume() {
        return amplifier.volume;
    }

    public int getBass() {
        return amplifier.bass;
    }

    public int getMiddle() {
        return amplifier.middle;
    }

    public int getTreble() {
        return amplifier.treble;
    }

    public int getIsf() {
        return amplifier.isf;
    }

    public int getPresence() {
        return amplifier.presence;
    }

    public int getResonance() {
        return amplifier.resonance;
    }


    public void setDelayEnabled(boolean enabled) {
        effects.delay.enabled = enabled;
    }

    public boolean isDelayEnabled() {
        return effects.delay.enabled;
    }

    public void setDelayType(int type) {
        effects.delay.type = type;
    }

    public int getDelayType() {
        return effects.delay.type;
    }

    public void setDelayLevel(int value) {
        effects.delay.level = value;
    }

    public int getDelayLevel() {
        return effects.delay.level;
    }

    public void setDelayFeedback(int value) {
        effects.delay.adjust1 = value;
    }

    public int getDelayFeedback() {
        return effects.delay.adjust1;
    }

    public void setDelayTime(int value) {
        effects.delay.tempo = value;
    }

    public int getDelayTime() {
        return effects.delay.tempo;
    }

    public void setReverbEnabled(boolean enabled) {
        effects.reverb.enabled = enabled;
    }

    public boolean isReverbEnabled() {
        return effects.reverb.enabled;
    }

    public void setReverbType(int type) {
        effects.reverb.type = type;
    }

    public int getReverbType() {
        return effects.reverb.type;
    }

    public void setReverbLevel(int value) {
        effects.reverb.level = value;
    }

    public int getReverbLevel() {
        return effects.reverb.level;
    }

    public void setReverbSize(int value) {
        effects.reverb.adjust1 = value;
    }

    public int getReverbSize() {
        return effects.reverb.adjust1;
    }

    // ================= NOISE GATE =================

    public void setNoiseGateEnabled(boolean enabled) {
        effects.noiseGate.enabled = enabled;
    }

    public boolean isNoiseGateEnabled() {
        return effects.noiseGate.enabled;
    }

    public void setNoiseGateSensitivity(int value) {
        effects.noiseGate.sensitivity = value;
    }

    public int getNoiseGateSensitivity() {
        return effects.noiseGate.sensitivity;
    }

    public void setNoiseGateAmount(int value) {
        effects.noiseGate.amount = value;
    }

    public int getNoiseGateAmount() {
        return effects.noiseGate.amount;
    }

    // ================= MODULATION =================

    public void setModulationEnabled(boolean enabled) {
        effects.modulation.enabled = enabled;
    }

    public boolean isModulationEnabled() {
        return effects.modulation.enabled;
    }

    public void setModulationType(int type) {
        effects.modulation.type = type;
    }

    public int getModulationType() {
        return effects.modulation.type;
    }

    public void setModulationParam1(int value) {
        effects.modulation.adjust1 = value;
    }

    public int getModulationParam1() {
        return effects.modulation.adjust1;
    }

    public void setModulationParam2(int value) {
        effects.modulation.adjust2 = value;
    }

    public int getModulationParam2() {
        return effects.modulation.adjust2;
    }

    public void setModulationParam3(int value) {
        effects.modulation.level = value;
    }

    public int getModulationParam3() {
        return effects.modulation.level;
    }

    public void setModulationParam4(int value) {
        effects.modulation.rate = value;
    }

    public int getModulationParam4() {
        return effects.modulation.rate;
    }

}