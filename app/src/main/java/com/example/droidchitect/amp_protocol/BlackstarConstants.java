package com.example.droidchitect.amp_protocol;

public final class BlackstarConstants {

    private BlackstarConstants() {}

    // ===== Device =====
    public static final int VENDOR_ID  = 0x27D4;
    public static final int PRODUCT_ID = 0x0013;

    // ===== Indices ======
    public static final class Index {
        public static final int REPORT_ID = 0;
        public static final int PARAM_ID  = 1;
        public static final int RESERVED  = 2;
        public static final int CONTEXT   = 3;
        public static final int VALUE     = 4;
    }

    // ===== Report IDs =====
    public static final int REPORT_STATE_SYNC = 0x02;
    public static final int REPORT_PARAMETER  = 0x03;
    public static final int REPORT_SPECIAL    = 0x08;
    public static final int REPORT_TUNER      = 0x09;

    // ===== Context =====
    public static final int CONTEXT_PATCH       = 0x01;
    public static final int CONTEXT_EFFECT_TYPE = 0x02;
    public static final int CONTEXT_VOICE       = 0x28;

    // ===== Parameter IDs =====
    public static final class Param {

        // Amplifier
        public static final int VOICE      = 0x01;
        public static final int GAIN       = 0x02;
        public static final int VOLUME     = 0x03;
        public static final int BASS       = 0x04;
        public static final int MIDDLE     = 0x05;
        public static final int TREBLE     = 0x06;
        public static final int ISF        = 0x07;

        public static final int RESONANCE  = 0x0B;
        public static final int PRESENCE   = 0x0C;

        // Unknowns (keep them for completeness)
        public static final int UNKNOWN_08 = 0x08;
        public static final int UNKNOWN_09 = 0x09;
        public static final int UNKNOWN_0A = 0x0A;
        public static final int UNKNOWN_0D = 0x0D;
        public static final int UNKNOWN_0E = 0x0E;

        // Noise Gate
        public static final int NOISE_GATE_SENS   = 0x26;
        public static final int NOISE_GATE_AMOUNT = 0x27;
        public static final int NOISE_GATE_SWITCH = 0x28;

        // Effects switches
        public static final int MOD_SWITCH    = 0x0F;
        public static final int DELAY_SWITCH  = 0x10;
        public static final int REVERB_SWITCH = 0x11;

        // Modulation
        public static final int MOD_TYPE = 0x12;
        public static final int MOD_1    = 0x13;
        public static final int MOD_2    = 0x14;
        public static final int MOD_3    = 0x15;
        public static final int MOD_4    = 0x16;

        // Delay
        public static final int DELAY_TYPE     = 0x17;
        public static final int DELAY_FEEDBACK = 0x18;
        public static final int DELAY_TONE     = 0x19;
        public static final int DELAY_LEVEL    = 0x1A;
        public static final int DELAY_TIME     = 0x1B;

        // Reverb
        public static final int REVERB_TYPE  = 0x1D;
        public static final int REVERB_SIZE  = 0x1E;
        public static final int REVERB_LEVEL = 0x20;
    }

    // ===== State dump offsets =====
    public static final class Offset {
        public static final int VOICE      = 4;
        public static final int GAIN       = 5;
        public static final int VOLUME     = 6;
        public static final int BASS       = 7;
        public static final int MIDDLE     = 8;
        public static final int TREBLE     = 9;
        public static final int ISF        = 10;
        public static final int RESONANCE  = 11;
        public static final int PRESENCE   = 12;
    }

    // ===== Voice Names =====
    public static final String[] VOICES = {
            "Clean Warm",
            "Clean Bright",
            "Crunch",
            "Super Crunch",
            "OD1",
            "OD2"
    };
}