package com.example.droidchitect.patch;

import android.util.Log;

import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;

public class PatchLoader {

    private static final String TAG = "PATCH_LOADER";

    // =========================================================
    // Thread to do the job, we want any latest request to be fulfilled first
    //    so current thread may get interrupted in case new applyPatch call comes
    // =========================================================
    private static volatile Thread currentPatchThread = null;

    // interface Callbacks to tell the patch load status
    public interface PatchLoadCallback {
        void onSuccess(Patch patch);
        void onFailure(Patch patch);
    }



    // =========================================================
    // TUNING
    // ========================================================

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

// INTERRUPT CHECK
    private static boolean shouldStop() {
        return Thread.currentThread().isInterrupted();
    }


    // ONLY FOR AMP TESTER
    public static Thread getCurrentPatchThread() {
        return currentPatchThread;
    }

// =========================================================
// SEND + DELAY
// =========================================================

    private static boolean sendCommand(Runnable command, int comm_delay) {

        if (shouldStop()) {
            return false;
        }

        command.run();

        // add Delay
        try {
            Thread.sleep(comm_delay);
        } catch (InterruptedException e) {
            return false;
        }

        return !shouldStop();
    }

    private static int sendMismatches(
            Patch patch,
            AmpController controller,
            AmpState state,
            int comm_delay) {

        int mismatches = 0;

        // =====================================================
        // AMPLIFIER
        // =====================================================

        if (patch.voice != state.getVoice()) {
            if (!sendCommand(() -> controller.setVoice(patch.voice), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.gain != state.getGain()) {
            if (!sendCommand(() -> controller.setGain(patch.gain), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.volume != state.getVolume()) {
            if (!sendCommand(() -> controller.setVolume(patch.volume), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.bass != state.getBass()) {
            if (!sendCommand(() -> controller.setBass(patch.bass), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.middle != state.getMiddle()) {
            if (!sendCommand(() -> controller.setMiddle(patch.middle), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.treble != state.getTreble()) {
            if (!sendCommand(() -> controller.setTreble(patch.treble), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.isf != state.getIsf()) {
            if (!sendCommand(() -> controller.setIsf(patch.isf), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.presence != state.getPresence()) {
            if (!sendCommand(() -> controller.setPresence(patch.presence), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.resonance != state.getResonance()) {
            if (!sendCommand(() -> controller.setResonance(patch.resonance), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        // =====================================================
        // MODULATION
        // =====================================================

        if (patch.modulationEnabled != state.isModulationEnabled()) {
            if (!sendCommand(() -> controller.toggleMod(patch.modulationEnabled), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.modulationType != state.getModulationType()) {
            if (!sendCommand(() -> controller.setModulationType(patch.modulationType), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.modulationParam1 != state.getModulationParam1()) {
            if (!sendCommand(() -> controller.setModulationParam1(patch.modulationParam1), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.modulationParam2 != state.getModulationParam2()) {

            if (!sendCommand(() -> controller.setModulationParam2(patch.modulationParam2), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.modulationParam3 != state.getModulationParam3()) {

            if (!sendCommand(() -> controller.setModulationParam3(patch.modulationParam3), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.modulationParam4 != state.getModulationParam4()) {
            if (!sendCommand(() -> controller.setModulationParam4(patch.modulationParam4), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        // =====================================================
        // DELAY
        // =====================================================

        if (patch.delayEnabled != state.isDelayEnabled()) {
            if (!sendCommand(() -> controller.toggleDelay(patch.delayEnabled), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.delayType != state.getDelayType()) {
            if (!sendCommand(() -> controller.setDelayType(patch.delayType), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.delayLevel != state.getDelayLevel()) {
            if (!sendCommand(() -> controller.setDelayLevel(patch.delayLevel), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.delayFeedback != state.getDelayFeedback()) {
            if (!sendCommand(() -> controller.setDelayFeedback(patch.delayFeedback), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.delayTime != state.getDelayTime()) {
            if (!sendCommand(() -> controller.setDelayTime(patch.delayTime), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        // =====================================================
        // REVERB
        // =====================================================

        if (patch.reverbEnabled != state.isReverbEnabled()) {
            if (!sendCommand(() -> controller.toggleReverb(patch.reverbEnabled), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.reverbType != state.getReverbType()) {
            if (!sendCommand(() -> controller.setReverbType(patch.reverbType), comm_delay)) {
                return mismatches;
            }

            mismatches++;
        }

        if (patch.reverbLevel != state.getReverbLevel()) {
            if (!sendCommand(() -> controller.setReverbLevel(patch.reverbLevel), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.reverbSize != state.getReverbSize()) {
            if (!sendCommand(() -> controller.setReverbSize(patch.reverbSize), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        // =====================================================
        // NOISE GATE
        // =====================================================

        if (patch.noiseGateEnabled != state.isNoiseGateEnabled()) {
            if (!sendCommand(() -> controller.toggleNoiseGate(patch.noiseGateEnabled), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.noiseGateSensitivity != state.getNoiseGateSensitivity()) {
            if (!sendCommand(() -> controller.setNoiseGateSensitivity(patch.noiseGateSensitivity), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        if (patch.noiseGateAmount != state.getNoiseGateAmount()) {
            if (!sendCommand(() -> controller.setNoiseGateAmount(patch.noiseGateAmount), comm_delay)) {
                return mismatches;
            }
            mismatches++;
        }

        return mismatches;
    }



// =========================================================
// APPLY PATCH
// =========================================================
    public static synchronized void applyPatch(
            Patch patch,
            AmpController controller,
            AmpState ampState,
            PatchLoadCallback callback) {

        // CANCEL PREVIOUS THREAD if already running
        if (currentPatchThread != null && currentPatchThread.isAlive()) {
            currentPatchThread.interrupt();
        }

        // CREATE THREAD
        Thread thread = new Thread(() -> {

            long startTime = System.currentTimeMillis();

            try {

                for (int loop = 1; loop <= MAX_SYNC_LOOPS; loop++) {

                    if (shouldStop()) {
                        return;
                    }
                    int comm_delay = 0; // No delay except for 1st time
                    if (loop == 1)
                        comm_delay = COMMAND_DELAY_MS;

                    int mismatches = sendMismatches(patch, controller, ampState, comm_delay);

                    Log.d(TAG, "Loop " + loop + " mismatches = " + mismatches);

                    if (mismatches == 0) {
                        long totalTime = System.currentTimeMillis() - startTime;
                        Log.d(TAG, "Patch synchronized in " + totalTime + " ms");

                        // Notify the caller that patch load was successful.
                        if (callback != null) {
                            callback.onSuccess(patch);
                        }
                        return;
                    }

                    // NOTE: if this thread gets interrupted then don't really need to notify the caller
                    //       Next thread will do that possibly.

                    try {
                        Thread.sleep(VERIFY_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                // Let caller know Patch Load failed
                Log.e(
                        TAG,
                        "Patch synchronization failed\n\n"
                                + "EXPECTED PATCH:\n"
                                + patch
                                + "\n\n"
                                + "ACTUAL AMP STATE:\n"
                                + ampState
                );
                if (callback != null) {
                    callback.onFailure(patch);
                }

            } finally {

                if (Thread.currentThread() == currentPatchThread) {
                    currentPatchThread = null;
                }
            }
        });

        currentPatchThread = thread;
        thread.start();
    }

}