package com.example.droidchitect.amp_protocol;

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
}