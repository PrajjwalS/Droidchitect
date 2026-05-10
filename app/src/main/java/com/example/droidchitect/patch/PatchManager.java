package com.example.droidchitect.patch;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class PatchManager {

    private static final String TAG = "PATCH_MANAGER";

    private static final String PATCH_FOLDER = "patches";

    private final Context context;

    private final Gson gson;

    private final List<PatchEntry> cachedPatches = new ArrayList<>();

    // =========================================================
    // PATCH ENTRY
    // =========================================================

    public static class PatchEntry {
        public final Patch patch;
        public final String fileName;
        public PatchEntry(Patch patch, String fileName) {
            this.patch = patch;
            this.fileName = fileName;
        }
    }

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PatchManager(Context context) {

        this.context = context;

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }


    // =========================================================
    // PATCHES DIRECTORY
    // =========================================================

    private File getPatchesDirectory() {

        File dir = new File(
                context.getExternalFilesDir(null),
                PATCH_FOLDER
        );

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }


    // =========================================================
    // SAVE PATCH
    // =========================================================

    public boolean savePatch(Patch patch) {

        try {

            String safeName =
                    patch.name.replaceAll("[^a-zA-Z0-9_-]", "_");

            File file = new File(
                    getPatchesDirectory(),
                    safeName + ".json"
            );

            FileWriter writer = new FileWriter(file);

            gson.toJson(patch, writer);

            writer.flush();
            writer.close();

            Log.d(TAG, "Patch saved: " + file.getAbsolutePath());

            return true;

        } catch (Exception e) {

            Log.e(TAG, "Failed to save patch", e);

            return false;
        }
    }


    // =========================================================
    // LOAD PATCH
    // =========================================================

    public Patch loadPatch(String filename) {

        try {

            File file = new File(
                    getPatchesDirectory(),
                    filename
            );

            FileReader reader = new FileReader(file);

            Patch patch =
                    gson.fromJson(reader, Patch.class);

            reader.close();

            if (!isPatchSane(patch)) {

                Log.e(
                        TAG,
                        "Invalid patch schema: " + filename
                );

                return null;
            }


            return patch;

        } catch (Exception e) {

            Log.e(TAG, "Failed to load patch", e);

            return null;
        }
    }


    // =========================================================
    // LIST PATCHES
    // =========================================================

    public List<String> listPatchFiles() {

        List<String> patches = new ArrayList<>();

        File[] files =
                getPatchesDirectory().listFiles();

        if (files == null) {
            return patches;
        }

        for (File file : files) {

            if (file.isFile() &&
                    file.getName().endsWith(".json")) {

                patches.add(file.getName());
            }
        }

        return patches;
    }


    // =========================================================
    // DELETE PATCH
    // =========================================================

    public boolean deletePatch(String filename) {

        File file = new File(
                getPatchesDirectory(),
                filename
        );

        return file.delete();
    }

// =====================================================
// GENERATE UNIQUE PATCH NAME
// =====================================================

    public String generateUniquePatchName(
            String baseName
    ) {
        try {
            if (baseName == null ||
                    baseName.trim().isEmpty()) {
                baseName = "Imported Patch";
            }

            baseName = baseName.trim();

            // =============================================
            // CHECK ORIGINAL
            // =============================================

            File originalFile =
                    new File(
                            getPatchesDirectory(),
                            sanitizeFileName(
                                    baseName
                            ) + ".json"
                    );

            if (!originalFile.exists()) {
                return baseName;
            }

            // =============================================
            // FIND AVAILABLE SUFFIX
            // =============================================

            int index = 1;

            while (true) {

                String candidate =
                        baseName +
                                " (" +
                                index +
                                ")";

                File candidateFile =
                        new File(
                                getPatchesDirectory(),
                                sanitizeFileName(
                                        candidate
                                ) + ".json"
                        );

                if (!candidateFile.exists()) {
                    return candidate;
                }
                index++;
            }
        } catch (Exception e) {

            e.printStackTrace();

            return baseName;
        }
    }
    private String sanitizeFileName(
            String name
    ) {

        return name.replaceAll(
                "[\\\\/:*?\"<>|]",
                "_"
        );
    }


    public void reloadCache() {

        cachedPatches.clear();

        List<String> patchFiles =
                listPatchFiles();

        for (String fileName : patchFiles) {

            Patch patch =
                    loadPatch(fileName);

            if (patch == null) {
                continue;
            }

            cachedPatches.add(
                    new PatchEntry(
                            patch,
                            fileName
                    )
            );
        }
    }

    public List<PatchEntry> getCachedPatches() {

        return new ArrayList<>(cachedPatches);
    }


    // Patch Sanity Related
    private boolean isPatchSane(
            Patch patch
    ) {

        if (patch == null) {
            return false;
        }

        switch (patch.version) {

            case 1:
                return isPatchSaneV1(patch);

            default:
                return false;
        }
    }

    private boolean isPatchSaneV1(
            Patch patch
    ) {

        try {

            // =============================================
            // REQUIRED OBJECTS
            // =============================================

            if (patch == null) {
                return false;
            }

            if (patch.name == null) {
                return false;
            }

            if (patch.creator == null) {
                return false;
            }

            if (patch.tags == null) {
                return false;
            }

            // =============================================
            // TAGS
            // =============================================

            for (String tag : patch.tags) {

                if (tag == null) {
                    return false;
                }
            }

            // =============================================
            // VERSION
            // =============================================

            if (patch.version != 1) {
                return false;
            }

            // =============================================
            // AMP
            // =============================================

            if (!inRange(patch.voice, 0, 5)) {
                return false;
            }

            if (!inRange(patch.gain, 0, 127)) {
                return false;
            }

            if (!inRange(patch.volume, 0, 127)) {
                return false;
            }

            if (!inRange(patch.bass, 0, 127)) {
                return false;
            }

            if (!inRange(patch.middle, 0, 127)) {
                return false;
            }

            if (!inRange(patch.treble, 0, 127)) {
                return false;
            }

            if (!inRange(patch.isf, 0, 127)) {
                return false;
            }

            if (!inRange(patch.presence, 0, 127)) {
                return false;
            }

            if (!inRange(patch.resonance, 0, 127)) {
                return false;
            }

            // =============================================
            // MODULATION
            // =============================================

            // basic existence touch
            boolean modulationEnabled =
                    patch.modulationEnabled;

            if (!inRange(patch.modulationType, 0, 3)) {
                return false;
            }

            if (!inRange(patch.modulationParam1, 0, 31)) {
                return false;
            }

            if (!inRange(patch.modulationParam2, 0, 127)) {
                return false;
            }

            if (!inRange(patch.modulationParam3, 0, 127)) {
                return false;
            }

            if (!inRange(patch.modulationParam4, 0, 127)) {
                return false;
            }

            // =============================================
            // DELAY
            // =============================================

            // basic existence touch
            boolean delayEnabled =
                    patch.delayEnabled;

            if (!inRange(patch.delayType, 0, 3)) {
                return false;
            }

            if (!inRange(patch.delayLevel, 0, 127)) {
                return false;
            }

            if (!inRange(patch.delayFeedback, 0, 31)) {
                return false;
            }

            if (!inRange(patch.delayTime, 100, 2000)) {
                return false;
            }

            // =============================================
            // REVERB
            // =============================================

            // basic existence touch
            boolean reverbEnabled =
                    patch.reverbEnabled;

            if (!inRange(patch.reverbType, 0, 3)) {
                return false;
            }

            if (!inRange(patch.reverbLevel, 0, 127)) {
                return false;
            }

            if (!inRange(patch.reverbSize, 0, 31)) {
                return false;
            }

            // =============================================
            // NOISE GATE
            // =============================================

            // basic existence touch
            boolean noiseGateEnabled =
                    patch.noiseGateEnabled;

            if (!inRange(
                    patch.noiseGateSensitivity,
                    0,
                    127
            )) {
                return false;
            }

            if (!inRange(
                    patch.noiseGateAmount,
                    0,
                    127
            )) {
                return false;
            }

            return true;

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Patch sanity validation failed",
                    e
            );

            return false;
        }
    }


    private boolean inRange(
            int value,
            int min,
            int max
    ) {

        return value >= min &&
                value <= max;
    }

}