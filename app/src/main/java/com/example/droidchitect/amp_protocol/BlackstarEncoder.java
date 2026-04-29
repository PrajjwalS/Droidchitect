package com.example.droidchitect.amp_protocol;

import static com.example.droidchitect.amp_protocol.BlackstarConstants.*;

public class BlackstarEncoder {

    private static final int PACKET_SIZE = 64;

    // ===== INIT =====
    //public static byte[] buildInit() {
    //    byte[] packet = new byte[PACKET_SIZE];
    //    packet[0] = 0x07;
    //    return packet;
    //}
    public static byte[] buildOutsiderInit() {
        byte[] packet = new byte[64];

        packet[0] = (byte) 0x81;
        packet[1] = 0x00;
        packet[2] = 0x00;
        packet[3] = 0x04;
        packet[4] = 0x03;
        packet[5] = 0x06;
        packet[6] = 0x02;
        packet[7] = 0x7A;

        return packet;
    }

    // ===== STATE REQUEST =====
    //public static byte[] buildStateRequest() {
    //    byte[] packet = new byte[PACKET_SIZE];

    //    packet[0] = REPORT_STATE_SYNC; // 0x02
    //    packet[1] = 0x06;
    //    packet[2] = 0x00;
    //    packet[3] = 0x3C;

    //    return packet;
    //}

    // ===== GENERIC PARAM MESSAGE =====
    public static byte[] buildParam(int paramId, int context, int value) {
        byte[] packet = new byte[PACKET_SIZE];

        packet[0] = REPORT_PARAMETER; // 0x03
        packet[1] = (byte) paramId;
        packet[2] = 0x00;
        packet[3] = (byte) context;
        packet[4] = (byte) value;

        return packet;
    }

    // ===== VOICE CHANGE =====
    public static byte[] buildVoice(int voice) {
        return buildParam(
                Param.VOICE,
                CONTEXT_VOICE,   // IMPORTANT: 0x28
                voice
        );
    }

    // ===== GAIN =====
    public static byte[] buildGain(int gain) {
        return buildParam(
                Param.GAIN,
                CONTEXT_PATCH,
                gain
        );
    }

    // ===== DELAY TIME (SPECIAL 2 BYTE) =====
    public static byte[] buildDelayTime(int delayMs) {

        delayMs = Math.max(100, Math.min(2000, delayMs));

        int fine = delayMs % 256;
        int coarse = delayMs / 256;

        byte[] packet = new byte[PACKET_SIZE];

        packet[0] = REPORT_PARAMETER;
        packet[1] = (byte) Param.DELAY_TIME;
        packet[2] = 0x00;
        packet[3] = 0x02; // special context
        packet[4] = (byte) fine;
        packet[5] = (byte) coarse;

        return packet;
    }
}