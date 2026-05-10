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
    // PATCH DIRECTORY
    // =========================================================

    private File getPatchesDirectory() {

        File dir = new File(
                context.getFilesDir(),
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

}