package com.example.droidchitect;

import android.util.Log;

import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;
import com.example.droidchitect.patch.Patch;
import com.example.droidchitect.patch.PatchLoader;

public class AmpTester {
    Patch patchA = new Patch();
    Patch patchB = new Patch();

    private void initPatchA() {
        patchA.name = "A";
        patchA.voice = 0;
        patchA.gain = 10;
        patchA.volume = 20;
        patchA.bass = 30;
        patchA.middle = 40;
        patchA.treble = 50;
        patchA.isf = 60;
        patchA.presence = 70;
        patchA.resonance = 80;
        patchA.modulationEnabled = true;
        patchA.modulationType = 3;
        patchA.modulationParam1 = 5;
        patchA.modulationParam2 = 1;
        patchA.modulationParam3 = 100;
        patchA.modulationParam4 = 110;
        patchA.delayEnabled = true;
        patchA.delayType = 2;
        patchA.delayLevel = 15;
        patchA.delayFeedback = 20;
        patchA.delayTime = 1400;
        patchA.reverbEnabled = true;
        patchA.reverbType = 1;
        patchA.reverbLevel = 90;
        patchA.reverbSize = 10;
        patchA.noiseGateEnabled = true;
        patchA.noiseGateSensitivity = 55;
        patchA.noiseGateAmount = 77;
    }

    private void initPatchB() {
        patchB.name = "B";
        patchB.voice = 5;
        patchB.gain = 5;
        patchB.volume = 5;
        patchB.bass = 110;
        patchB.middle = 15;
        patchB.treble = 127;
        patchB.isf = 5;
        patchB.presence = 25;
        patchB.resonance = 10;
        patchB.modulationEnabled = true;
        patchB.modulationType = 1; // Chorus/Flanger
        patchB.modulationParam1 = 31;
        patchB.modulationParam2 = 127;
        patchB.modulationParam3 = 5;
        patchB.modulationParam4 = 0;
        patchB.delayEnabled = true;
        patchB.delayType = 3;
        patchB.delayLevel = 31;
        patchB.delayFeedback = 31;
        patchB.delayTime = 2000;
        patchB.reverbEnabled = true;
        patchB.reverbType = 3;
        patchB.reverbLevel = 127;
        patchB.reverbSize = 31;
        patchB.noiseGateEnabled = true;
        patchB.noiseGateSensitivity = 127;
        patchB.noiseGateAmount = 0;
    }

    public AmpTester() {
        initPatchA();
        initPatchB();
    }

    /* Stress for figuring out patch command delays
    *
    * NOTE i tested from values from 5ms to 50 ms ... sometimes 5ms is failing once in 120 times
    *                                                 sometimes 50ms is failing once in 60 times
    *
    *   So this is not working + we would want to have different delays for every individuals amp most
    *    probably.
    *    Better approach would be to set a standard delay (like  5ms) per command, and then run a
    *       job every 50ms (or lesser 20 or 30 ms?) to check if full patch was really applied to ampState.
    *       If not .. then .. send those commands again.
    *
    *   My goal is to least achieve guaranteed less than half a second patch applies.
    *
    *   in conclusion : failure is NOT purely timing based !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    *
    *   Retiring this stress test function. but this was an important part of this project.
    *   The other patch test function (runPatchConvergenceTest) uses the better approach addressed above and
    *  verifies if convergence is under X ms or not
    *
    public void runPatchStressTest(AmpController controller, AmpState ampState) {


        new Thread(() -> {

            int command_delay_cache = PatchLoader.getSendCommandDelay();
            int command_delay_test_val = 5;    // Keep Changing this value to figure out best delay for your amp
            int estimated_commands_sent = 30;   // Note: We have around 27 Commands to be sent in a Patch. assume 30
            int wait_after_full_patch_sent = 100;
            int post_apply_wait_test_val = (estimated_commands_sent * command_delay_test_val ) + wait_after_full_patch_sent; // ms
            int total_runs = 1000;


            PatchLoader.setSendCommandDelay(command_delay_test_val);

            Log.d("PATCH_TEST" , "Running Patch Stress Test with : sendCommandDelay: "
                    + command_delay_test_val + "ms "
                    + "PostApplyWait: " + post_apply_wait_test_val +"ms");

            int passes = 0;


            for (int i = 0; i < total_runs; i++) {

                Patch target =
                        (i % 2 == 0)
                                ? patchA
                                : patchB;

                PatchLoader.applyPatch(
                        target,
                        controller,
                        AmpState
                );

                // wait for patch apply
                try {
                    Thread.sleep(post_apply_wait_test_val);
                } catch (Exception ignored) {
                }

                boolean ok =
                        target.roughlyEquals(ampState);

                if (ok) {

                    passes++;

                } else {

                    Log.e(
                            "PATCH_TEST",
                            "FAILED ITERATION " + i +
                                    "\nEXPECTED:\n" + target +
                                    "\nACTUAL:\n" + ampState
                    );
                    break;
                }

                Log.d(
                        "PATCH_TEST",
                        "Iteration "
                                + i +
                                " pass=" + ok
                );
            }

            Log.i(
                    "PATCH_TEST",
                    "FINAL RESULT = "
                            + passes +
                            "/" +
                            total_runs +
                            " with : sendCommandDelay: "
                            + command_delay_test_val + "ms "
                            + "PostApplyWait: " + post_apply_wait_test_val +"ms"
            );
            PatchLoader.setSendCommandDelay(command_delay_cache);

        }).start();
    }
    */


    /*
    * Last few runs results (Note that these failures aren't ending in bad config state, they
    *                        converge to correct state, just not under 320 ms)
    *
    *
2026-05-09 14:08:44.990 27795-28016 PATCH_TEST              com.example.droidchitect             I  ====================================
2026-05-09 14:08:44.990 27795-28016 PATCH_TEST              com.example.droidchitect             I  FINAL RESULTS
2026-05-09 14:08:44.990 27795-28016 PATCH_TEST              com.example.droidchitect             I  Total Iterations = 1000
2026-05-09 14:08:44.991 27795-28016 PATCH_TEST              com.example.droidchitect             I  Pass Threshold Duration = 320
2026-05-09 14:08:44.991 27795-28016 PATCH_TEST              com.example.droidchitect             I  Passes = 991
2026-05-09 14:08:44.991 27795-28016 PATCH_TEST              com.example.droidchitect             I  Failures = 9
2026-05-09 14:08:44.992 27795-28016 PATCH_TEST              com.example.droidchitect             I  Pass Percentage = 99.1%
2026-05-09 14:08:44.993 27795-28016 PATCH_TEST              com.example.droidchitect             I  Average Convergence = 254.09ms
2026-05-09 14:08:44.994 27795-28016 PATCH_TEST              com.example.droidchitect             I  Min Convergence = 232ms
2026-05-09 14:08:44.994 27795-28016 PATCH_TEST              com.example.droidchitect             I  Max Convergence = 1283ms
2026-05-09 14:08:44.994 27795-28016 PATCH_TEST              com.example.droidchitect             I  Command Delay = 5ms
2026-05-09 14:08:44.994 27795-28016 PATCH_TEST              com.example.droidchitect             I  Verify Interval = 15ms
2026-05-09 14:08:44.995 27795-28016 PATCH_TEST              com.example.droidchitect             I  Max Sync Loops = 20
2026-05-09 14:08:44.995 27795-28016 PATCH_TEST              com.example.droidchitect             I  ====================================

    *
    * */
    public void runPatchConvergenceTest(
            AmpController controller,
            AmpState ampState
    ) {

        new Thread(() -> {

            int totalRuns = 1000;

            int passes = 0;

            int failures = 0;

            long totalConvergenceTime = 0;

            long maxConvergenceTime = 0;

            long minConvergenceTime = Long.MAX_VALUE;

            long maxAllowedTimeMs = 320;

            Log.i(
                    "PATCH_TEST",
                    "===================================="
            );

            Log.i(
                    "PATCH_TEST",
                    "STARTING PATCH CONVERGENCE TEST"
            );

            Log.i(
                    "PATCH_TEST",
                    "===================================="
            );

            for (int i = 0; i < totalRuns; i++) {

                Patch target =
                        (i % 2 == 0)
                                ? patchA
                                : patchB;

                long startTime =
                        System.currentTimeMillis();

                // =============================================
                // APPLY PATCH
                // =============================================

                PatchLoader.applyPatch(
                        target,
                        controller,
                        ampState
                );

                // =============================================
                // WAIT UNTIL PATCH THREAD FINISHES
                // =============================================

                while (PatchLoader.isBusy()) {

                    try {

                        Thread.sleep(5);

                    } catch (Exception ignored) {
                    }
                }

                // =============================================
                // FINAL CONVERGENCE TIME
                // =============================================

                long convergenceTime =
                        System.currentTimeMillis()
                                - startTime;

                // =============================================
                // TRACK STATS
                // =============================================

                totalConvergenceTime += convergenceTime;

                if (convergenceTime > maxConvergenceTime) {

                    maxConvergenceTime = convergenceTime;
                }

                if (convergenceTime < minConvergenceTime) {

                    minConvergenceTime = convergenceTime;
                }

                // =============================================
                // VALIDATE FINAL STATE
                // =============================================

                boolean correctState =
                        target.roughlyEquals(ampState);

                boolean withinTime =
                        convergenceTime <= maxAllowedTimeMs;

                boolean success =
                        correctState && withinTime;

                // =============================================
                // RESULT
                // =============================================

                if (success) {

                    passes++;

                    Log.i(
                            "PATCH_TEST",
                            "PASS iteration="
                                    + i +
                                    " convergence="
                                    + convergenceTime +
                                    "ms"
                    );

                } else {

                    failures++;

                    Log.e(
                            "PATCH_TEST",
                            "FAIL iteration="
                                    + i
                    );

                    Log.e(
                            "PATCH_TEST",
                            "Convergence Time="
                                    + convergenceTime
                                    + "ms"
                    );

                    Log.e(
                            "PATCH_TEST",
                            "Correct State="
                                    + correctState
                    );

                    Log.e(
                            "PATCH_TEST",
                            "Within Time="
                                    + withinTime
                    );

                    Log.e(
                            "PATCH_TEST",
                            "EXPECTED:\n"
                                    + target
                    );

                    Log.e(
                            "PATCH_TEST",
                            "ACTUAL:\n"
                                    + ampState
                    );
                }
            }

            // =====================================================
            // FINAL RESULTS
            // =====================================================

            double passPercentage =

                    ((double) passes / totalRuns)
                            * 100.0;

            double averageConvergence =

                    (double) totalConvergenceTime
                            / totalRuns;

            Log.i(
                    "PATCH_TEST",
                    "===================================="
            );

            Log.i(
                    "PATCH_TEST",
                    "FINAL RESULTS"
            );

            Log.i(
                    "PATCH_TEST",
                    "Total Iterations = "
                            + totalRuns
            );

            Log.i(
                    "PATCH_TEST",
                    "Pass Threshold Duration = "
                            + maxAllowedTimeMs
            );

            Log.i(
                    "PATCH_TEST",
                    "Passes = "
                            + passes
            );

            Log.i(
                    "PATCH_TEST",
                    "Failures = "
                            + failures
            );

            Log.i(
                    "PATCH_TEST",
                    "Pass Percentage = "
                            + passPercentage
                            + "%"
            );

            Log.i(
                    "PATCH_TEST",
                    "Average Convergence = "
                            + averageConvergence
                            + "ms"
            );

            Log.i(
                    "PATCH_TEST",
                    "Min Convergence = "
                            + minConvergenceTime
                            + "ms"
            );

            Log.i(
                    "PATCH_TEST",
                    "Max Convergence = "
                            + maxConvergenceTime
                            + "ms"
            );

            Log.i(
                    "PATCH_TEST",
                    "Command Delay = "
                            + PatchLoader.getSendCommandDelay()
                            + "ms"
            );

            Log.i(
                    "PATCH_TEST",
                    "Verify Interval = "
                            + PatchLoader.getVerifyInterval()
                            + "ms"
            );

            Log.i(
                    "PATCH_TEST",
                    "Max Sync Loops = "
                            + PatchLoader.getMaxSyncLoops()
            );

            Log.i(
                    "PATCH_TEST",
                    "===================================="
            );

        }).start();
    }
}