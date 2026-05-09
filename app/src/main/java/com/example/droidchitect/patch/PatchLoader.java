package com.example.droidchitect.patch;

import android.util.Log;

import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;

public class PatchLoader {

    private static final String TAG = "PATCH_LOADER";

    // =========================================================
    // Thread to do the job, we only allow 1 patch application at a time.
    // =========================================================
    private static Thread currentPatchThread = null;


    // =========================================================
    // TUNING
    // =========================================================

    /*
     * Initial fast send pacing
     */
    private static int COMMAND_DELAY_MS = 5;

    /*
     * Time between sync verification loops
     */
    private static int VERIFY_INTERVAL_MS = 15;

    /*
     * Maximum reconciliation attempts
     */
    private static int MAX_SYNC_LOOPS = 20;


    // =========================================================
    // Thread helpers
    // =========================================================

    public static boolean isBusy() {

        return currentPatchThread != null
                && currentPatchThread.isAlive();
    }

    public static void waitUntilIdle() {

        try {

            if (currentPatchThread != null) {

                currentPatchThread.join();
            }

        } catch (Exception ignored) {
        }
    }


    // =========================================================
    // CONFIG
    // =========================================================

    public static void setSendCommandDelay(int ms) {
        COMMAND_DELAY_MS = ms;
    }

    public static int getSendCommandDelay() {
        return COMMAND_DELAY_MS;
    }

    public static void setVerifyInterval(int ms) {
        VERIFY_INTERVAL_MS = ms;
    }

    public static void setMaxSyncLoops(int loops) {
        MAX_SYNC_LOOPS = loops;
    }

    public static int getMaxSyncLoops() {
        return MAX_SYNC_LOOPS;
    }

    public static int getVerifyInterval() {
        return VERIFY_INTERVAL_MS;
    }


    // =========================================================
    // DELAY HELPER
    // =========================================================

    private static void delay() {

        try {
            Thread.sleep(COMMAND_DELAY_MS);
        } catch (Exception ignored) {
        }
    }


    // =========================================================
    // APPLY PATCH
    // =========================================================

    public static void applyPatch(
            Patch patch,
            AmpController controller,
            AmpState ampState
    ) {

        if (isBusy()) {
            Log.w(
                    TAG,
                    "PatchLoader busy, ignoring apply request"
            );
            return;
        }

        currentPatchThread = new Thread(() -> {

            long startTime =
                    System.currentTimeMillis();

            Log.d(
                    TAG,
                    "Starting synchronized patch apply"
            );

            // =================================================
            // FIRST PASS
            // =================================================

            sendAll(patch, controller);

            // =================================================
            // VERIFY + RESEND LOOP
            // =================================================

            for (int loop = 1;
                 loop <= MAX_SYNC_LOOPS;
                 loop++) {

                try {

                    Thread.sleep(VERIFY_INTERVAL_MS);

                } catch (Exception ignored) {
                }

                int mismatches =
                        sendMismatches(
                                patch,
                                controller,
                                ampState
                        );

                Log.d(
                        TAG,
                        "Loop " +
                                loop +
                                " mismatches = " +
                                mismatches
                );

                // SUCCESS
                if (mismatches == 0) {

                    long totalTime =
                            System.currentTimeMillis()
                                    - startTime;

                    Log.d(
                            TAG,
                            "Patch synchronized in " +
                                    loop +
                                    " loops, " +
                                    totalTime +
                                    " ms"
                    );
                    currentPatchThread = null;
                    return;
                }
            }

            Log.e(
                    TAG,
                    "Patch synchronization failed"
            );

            currentPatchThread = null;
        });

        currentPatchThread.start();

    }


    // =========================================================
    // SEND ALL
    // =========================================================

    private static void sendAll(
            Patch patch,
            AmpController controller
    ) {

        // =====================================================
        // AMPLIFIER
        // =====================================================

        controller.setVoice(patch.voice);
        delay();

        controller.setGain(patch.gain);
        delay();

        controller.setVolume(patch.volume);
        delay();

        controller.setBass(patch.bass);
        delay();

        controller.setMiddle(patch.middle);
        delay();

        controller.setTreble(patch.treble);
        delay();

        controller.setIsf(patch.isf);
        delay();

        controller.setPresence(patch.presence);
        delay();

        controller.setResonance(patch.resonance);
        delay();


        // =====================================================
        // MODULATION
        // =====================================================

        controller.toggleMod(
                patch.modulationEnabled
        );
        delay();

        controller.setModulationType(
                patch.modulationType
        );
        delay();

        controller.setModulationParam1(
                patch.modulationParam1
        );
        delay();

        controller.setModulationParam2(
                patch.modulationParam2
        );
        delay();

        controller.setModulationParam3(
                patch.modulationParam3
        );
        delay();

        controller.setModulationParam4(
                patch.modulationParam4
        );
        delay();


        // =====================================================
        // DELAY
        // =====================================================

        controller.toggleDelay(
                patch.delayEnabled
        );
        delay();

        controller.setDelayType(
                patch.delayType
        );
        delay();

        controller.setDelayLevel(
                patch.delayLevel
        );
        delay();

        controller.setDelayFeedback(
                patch.delayFeedback
        );
        delay();

        controller.setDelayTime(
                patch.delayTime
        );
        delay();


        // =====================================================
        // REVERB
        // =====================================================

        controller.toggleReverb(
                patch.reverbEnabled
        );
        delay();

        controller.setReverbType(
                patch.reverbType
        );
        delay();

        controller.setReverbLevel(
                patch.reverbLevel
        );
        delay();

        controller.setReverbSize(
                patch.reverbSize
        );
        delay();


        // =====================================================
        // NOISE GATE
        // =====================================================

        controller.toggleNoiseGate(
                patch.noiseGateEnabled
        );
        delay();

        controller.setNoiseGateSensitivity(
                patch.noiseGateSensitivity
        );
        delay();

        controller.setNoiseGateAmount(
                patch.noiseGateAmount
        );
        delay();
    }


    // =========================================================
    // SEND MISMATCHES
    // =========================================================

    private static int sendMismatches(
            Patch patch,
            AmpController controller,
            AmpState state
    ) {

        int mismatches = 0;

        // =====================================================
        // AMPLIFIER
        // =====================================================

        if (patch.voice != state.getVoice()) {
            controller.setVoice(patch.voice);
            mismatches++;
        }

        if (patch.gain != state.getGain()) {
            controller.setGain(patch.gain);
            mismatches++;
        }

        if (patch.volume != state.getVolume()) {
            controller.setVolume(patch.volume);
            mismatches++;
        }

        if (patch.bass != state.getBass()) {
            controller.setBass(patch.bass);
            mismatches++;
        }

        if (patch.middle != state.getMiddle()) {
            controller.setMiddle(patch.middle);
            mismatches++;
        }

        if (patch.treble != state.getTreble()) {
            controller.setTreble(patch.treble);
            mismatches++;
        }

        if (patch.isf != state.getIsf()) {
            controller.setIsf(patch.isf);
            mismatches++;
        }

        if (patch.presence != state.getPresence()) {
            controller.setPresence(patch.presence);
            mismatches++;
        }

        if (patch.resonance != state.getResonance()) {
            controller.setResonance(patch.resonance);
            mismatches++;
        }


        // =====================================================
        // MODULATION
        // =====================================================

        if (patch.modulationEnabled
                != state.isModulationEnabled()) {
            controller.toggleMod(
                    patch.modulationEnabled
            );
            mismatches++;
        }

        if (patch.modulationType
                != state.getModulationType()) {
            controller.setModulationType(
                    patch.modulationType
            );
            mismatches++;
        }

        if (patch.modulationParam1
                != state.getModulationParam1()) {
            controller.setModulationParam1(
                    patch.modulationParam1
            );
            mismatches++;
        }

        if (patch.modulationParam2
                != state.getModulationParam2()) {
            controller.setModulationParam2(
                    patch.modulationParam2
            );
            mismatches++;
        }

        if (patch.modulationParam3
                != state.getModulationParam3()) {
            controller.setModulationParam3(
                    patch.modulationParam3
            );
            mismatches++;
        }

        if (patch.modulationParam4
                != state.getModulationParam4()) {
            controller.setModulationParam4(
                    patch.modulationParam4
            );
            mismatches++;
        }


        // =====================================================
        // DELAY
        // =====================================================

        if (patch.delayEnabled
                != state.isDelayEnabled()) {
            controller.toggleDelay(
                    patch.delayEnabled
            );
            mismatches++;
        }

        if (patch.delayType
                != state.getDelayType()) {
            controller.setDelayType(
                    patch.delayType
            );
            mismatches++;
        }

        if (patch.delayLevel
                != state.getDelayLevel()) {
            controller.setDelayLevel(
                    patch.delayLevel
            );
            mismatches++;
        }

        if (patch.delayFeedback
                != state.getDelayFeedback()) {
            controller.setDelayFeedback(
                    patch.delayFeedback
            );
            mismatches++;
        }

        if (patch.delayTime
                != state.getDelayTime()) {
            controller.setDelayTime(
                    patch.delayTime
            );
            mismatches++;
        }


        // =====================================================
        // REVERB
        // =====================================================

        if (patch.reverbEnabled
                != state.isReverbEnabled()) {
            controller.toggleReverb(
                    patch.reverbEnabled
            );
            mismatches++;
        }

        if (patch.reverbType
                != state.getReverbType()) {
            controller.setReverbType(
                    patch.reverbType
            );
            mismatches++;
        }

        if (patch.reverbLevel
                != state.getReverbLevel()) {
            controller.setReverbLevel(
                    patch.reverbLevel
            );
            mismatches++;
        }

        if (patch.reverbSize
                != state.getReverbSize()) {
            controller.setReverbSize(
                    patch.reverbSize
            );
            mismatches++;
        }


        // =====================================================
        // NOISE GATE
        // =====================================================

        if (patch.noiseGateEnabled
                != state.isNoiseGateEnabled()) {
            controller.toggleNoiseGate(
                    patch.noiseGateEnabled
            );
            mismatches++;
        }

        if (patch.noiseGateSensitivity
                != state.getNoiseGateSensitivity()) {
            controller.setNoiseGateSensitivity(
                    patch.noiseGateSensitivity
            );
            mismatches++;
        }

        if (patch.noiseGateAmount
                != state.getNoiseGateAmount()) {
            controller.setNoiseGateAmount(
                    patch.noiseGateAmount
            );
            mismatches++;
        }

        return mismatches;
    }



}