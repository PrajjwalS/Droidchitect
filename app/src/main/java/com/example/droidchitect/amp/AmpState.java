package com.example.droidchitect.amp;

public class AmpState {

    private boolean initial_init_done = false;
    public Amplifier amplifier = new Amplifier();
    public Effects effects = new Effects();

    public boolean isInitial_init_done() {
        return initial_init_done;
    }

    public void setInitial_init_done(boolean x) {
        initial_init_done = x;
    }
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

    @Override
    public String toString() {
        String voiceName = "Unknown";

        if (amplifier.voice >= 0 &&
                amplifier.voice < BlackstarConstants.VOICES.length) {

            voiceName = BlackstarConstants.VOICES[amplifier.voice];
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
                ", level=" + effects.modulation.level +
                ", rate=" + effects.modulation.rate +
                ", adj1=" + effects.modulation.adjust1 +
                ", adj2=" + effects.modulation.adjust2 +
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
}