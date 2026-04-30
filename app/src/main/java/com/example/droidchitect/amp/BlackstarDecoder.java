package com.example.droidchitect.amp;

import static com.example.droidchitect.amp.BlackstarConstants.*;
import android.util.Log;

public class BlackstarDecoder {

    private static final String TAG = "DROIDCHITECT_USB_DEBUG";

    private static boolean hasReceivedStateDump = false; // Note that this makes it global kind of a think for only one instance (we have one amp only too.)
    public static void reset() {
        hasReceivedStateDump = false;
    }

    private static int u(byte b) {
        return b & 0xFF;
    }

    public static void decode(byte[] data, int length, AmpState state) {

        if (data == null || length < 5) return;

        int reportId = u(data[Index.REPORT_ID]);
        int context  = u(data[Index.CONTEXT]);

        // 🔥 CRITICAL: state dump detection (architect-linux logic)
        if (reportId == REPORT_PARAMETER && context == 0x2A) {
            Log.d(TAG, "DECODE - FULL STATE DUMP");
            parseStateDump(data, state);
            return;
        }

        switch (reportId) {

            case REPORT_PARAMETER:
                parseParameter(data, state);
                break;

            case REPORT_TUNER:
                // ignore for now
                break;

            case REPORT_SPECIAL:
                // ignore for now
                break;

            default:
                // ignore unknown reports
                break;
        }
    }

    private static void parseParameter(byte[] data, AmpState state) {

        int paramId = u(data[Index.PARAM_ID]);
        int context = u(data[Index.CONTEXT]);
        int value   = u(data[Index.VALUE]);

        switch (paramId) {

            // ===== AMP =====
            case Param.VOICE:
                state.amplifier.voice = value;
                break;

            case Param.GAIN:
                state.amplifier.gain = value;
                break;

            case Param.VOLUME:
                state.amplifier.volume = value;
                break;

            case Param.BASS:
                state.amplifier.bass = value;
                break;

            case Param.MIDDLE:
                state.amplifier.middle = value;
                break;

            case Param.TREBLE:
                state.amplifier.treble = value;
                break;

            case Param.ISF:
                state.amplifier.isf = value;
                break;

            case Param.RESONANCE:
                state.amplifier.resonance = value;
                break;

            case Param.PRESENCE:
                state.amplifier.presence = value;
                break;

            // ===== MOD =====
            case Param.MOD_SWITCH:
                state.effects.modulation.enabled = (value == 1);
                break;

            case Param.MOD_TYPE:
                state.effects.modulation.type = value;
                break;

            case Param.MOD_1:
                state.effects.modulation.adjust1 = value;
                break;

            case Param.MOD_2:
                state.effects.modulation.adjust2 = value;
                break;

            case Param.MOD_3:
                state.effects.modulation.level = value;
                break;

            case Param.MOD_4:
                state.effects.modulation.rate = value;
                break;

            // ===== DELAY =====
            case Param.DELAY_SWITCH:
                state.effects.delay.enabled = (value == 1);
                break;

            case Param.DELAY_TYPE:
                state.effects.delay.type = value;
                break;

            case Param.DELAY_FEEDBACK:
                state.effects.delay.adjust1 = value;
                break;

            case Param.DELAY_TONE:
                state.effects.delay.adjust2 = value;
                break;

            case Param.DELAY_LEVEL:
                state.effects.delay.level = value;
                break;

            case Param.DELAY_TIME:
                if (data.length >= 6) {
                    int fine   = u(data[4]);
                    int coarse = u(data[5]);
                    state.effects.delay.tempo = (coarse << 8) | fine;
                }
                break;

            // ===== REVERB =====
            case Param.REVERB_SWITCH:
                state.effects.reverb.enabled = (value == 1);
                break;

            case Param.REVERB_TYPE:
                state.effects.reverb.type = value;
                break;

            case Param.REVERB_SIZE:
                state.effects.reverb.adjust1 = value;
                break;

            case Param.REVERB_LEVEL:
                state.effects.reverb.level = value;
                break;

            default:
                // unknown param → ignore
                break;
        }
    }

    private static void parseStateDump(byte[] data, AmpState state) {

        // architect-linux: value = data[paramId + 3]
        state.amplifier.voice     = u(data[Param.VOICE + 3]);
        state.amplifier.gain      = u(data[Param.GAIN + 3]);
        state.amplifier.volume    = u(data[Param.VOLUME + 3]);
        state.amplifier.bass      = u(data[Param.BASS + 3]);
        state.amplifier.middle    = u(data[Param.MIDDLE + 3]);
        state.amplifier.treble    = u(data[Param.TREBLE + 3]);
        state.amplifier.isf       = u(data[Param.ISF + 3]);
        state.amplifier.resonance = u(data[Param.RESONANCE + 3]);
        state.amplifier.presence  = u(data[Param.PRESENCE + 3]);
    }
}