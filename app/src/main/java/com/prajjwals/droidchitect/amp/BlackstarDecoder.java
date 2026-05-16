package com.prajjwals.droidchitect.amp;

import static com.prajjwals.droidchitect.amp.BlackstarConstants.*;
import android.util.Log;

public class BlackstarDecoder {

    private static final String TAG = "DROIDCHITECT_USB_DEBUG";

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

                // hardware effect-type knob packets carry:
                // byte[4] = type
                // byte[5] = adjust1 position
                if (context == CONTEXT_EFFECT_TYPE && data.length >= 6) {
                    state.effects.modulation.adjust1 = u(data[5]);
                }

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


                if (context == CONTEXT_EFFECT_TYPE && data.length >= 6) {
                    state.effects.delay.adjust1 = u(data[5]);
                }

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

                if (context == CONTEXT_EFFECT_TYPE && data.length >= 6) {
                    state.effects.reverb.adjust1 = u(data[5]);
                }

                break;

            case Param.REVERB_SIZE:
                state.effects.reverb.adjust1 = value;
                break;

            case Param.REVERB_LEVEL:
                state.effects.reverb.level = value;
                break;

            // ===== NOISE GATE =====

            case Param.NOISE_GATE_SWITCH:
                state.effects.noiseGate.enabled = (value == 1);
                break;

            case Param.NOISE_GATE_SENS:
                state.effects.noiseGate.sensitivity = value;
                break;

            case Param.NOISE_GATE_AMOUNT:
                state.effects.noiseGate.amount = value;
                break;


            default:
                // unknown param → ignore
                break;
        }
    }

    private static void parseStateDump(byte[] data, AmpState state) {

        // architect-linux style:
        // value = data[paramId + 3]

        // ===== Amplifier =====
        state.amplifier.voice     = u(data[Param.VOICE + 3]);
        state.amplifier.gain      = u(data[Param.GAIN + 3]);
        state.amplifier.volume    = u(data[Param.VOLUME + 3]);
        state.amplifier.bass      = u(data[Param.BASS + 3]);
        state.amplifier.middle    = u(data[Param.MIDDLE + 3]);
        state.amplifier.treble    = u(data[Param.TREBLE + 3]);
        state.amplifier.isf       = u(data[Param.ISF + 3]);
        state.amplifier.resonance = u(data[Param.RESONANCE + 3]);
        state.amplifier.presence  = u(data[Param.PRESENCE + 3]);

        // ===== Effects ===== //


        // ===== MOD =====
        state.effects.modulation.enabled = u(data[Param.MOD_SWITCH + 3]) == 1;

        // Hardware effect-type knob encoding:
        //
        // high byte = modulation type
        // low byte  = adjust1 position
        //
        // Example:
        // 0x0000 -> 0x001F = Phaser
        // 0x0100 -> 0x011F = Chorus/Flanger
        // etc.

        int modType     = u(data[Param.MOD_TYPE + 3]);
        int modPosition = u(data[Param.MOD_TYPE + 4]);

        state.effects.modulation.type = modType;

        // IMPORTANT:
        // hardware effect-type knob controls adjust1
        //
        // UI layer later maps:
        //
        // Phaser          -> Mix
        // Chorus/Flanger  -> Morph
        // Envelope        -> Sens
        // Tremolo         -> Pitch/Xover
        //
        state.effects.modulation.adjust1 = modPosition;

        // Generic modulation params
        state.effects.modulation.adjust2 = u(data[Param.MOD_2 + 3]);
        state.effects.modulation.level = u(data[Param.MOD_3 + 3]);
        state.effects.modulation.rate = u(data[Param.MOD_4 + 3]);



        // ===== DELAY =====
        state.effects.delay.enabled = u(data[Param.DELAY_SWITCH + 3]) == 1;

        // Packed delay type encoding:
        //
        // high byte = delay type
        // low byte  = adjust1 position
        //
        int delayType     = u(data[Param.DELAY_TYPE + 3]);
        int delayPosition = u(data[Param.DELAY_TYPE + 4]);

        state.effects.delay.type = delayType;

        // Hardware effect-type knob controls adjust1
        //
        // Linear -> Feedback
        // Analog -> Feedback
        // Tape   -> Feedback
        // Multi  -> Feedback
        //
        state.effects.delay.adjust1 = delayPosition;

        // Generic delay params
        state.effects.delay.adjust2 = u(data[Param.DELAY_TONE + 3]);
        state.effects.delay.level = u(data[Param.DELAY_LEVEL + 3]);

        // Delay time (16-bit)
        int fine   = u(data[Param.DELAY_TIME + 3]);
        int coarse = u(data[Param.DELAY_TIME + 4]);

        state.effects.delay.tempo = (coarse << 8) | fine;





        // ===== REVERB =====
        state.effects.reverb.enabled = u(data[Param.REVERB_SWITCH + 3]) == 1;

        // Packed reverb type encoding:
        //
        // high byte = reverb type
        // low byte  = adjust1 position
        //
        int reverbType     = u(data[Param.REVERB_TYPE + 3]);
        int reverbPosition = u(data[Param.REVERB_TYPE + 4]);

        state.effects.reverb.type = reverbType;

        // Hardware effect-type knob controls adjust1
        //
        // Room   -> Size
        // Hall   -> Size
        // Spring -> Size
        // Plate  -> Size
        //
        state.effects.reverb.adjust1 = reverbPosition;

        // Generic reverb params
        state.effects.reverb.level = u(data[Param.REVERB_LEVEL + 3]);




        // ===== NOISE GATE =====
        state.effects.noiseGate.enabled     = u(data[Param.NOISE_GATE_SWITCH + 3]) == 1;
        state.effects.noiseGate.sensitivity = u(data[Param.NOISE_GATE_SENS + 3]);
        state.effects.noiseGate.amount      = u(data[Param.NOISE_GATE_AMOUNT + 3]);

    }



}