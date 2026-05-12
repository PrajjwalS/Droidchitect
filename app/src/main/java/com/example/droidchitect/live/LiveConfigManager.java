package com.example.droidchitect.live;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class LiveConfigManager {

    private static final String TAG = "LIVE_CONFIG";

    private static final String FILE_NAME = "live_config.json";

    private final Context context;

    private final Gson gson;

    public LiveConfigManager(Context context) {

        this.context = context;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    // =====================================================
    // FILE
    // =====================================================

    private File getConfigFile() {
        return new File(
                context.getFilesDir(),
                FILE_NAME
        );
    }

    // =====================================================
    // LOAD
    // =====================================================

    public LiveConfig loadConfig() {

        try {

            File file = getConfigFile();

            if (!file.exists()) {
                return new LiveConfig();
            }

            FileReader reader = new FileReader(file);

            LiveConfig config = gson.fromJson(reader, LiveConfig.class);

            reader.close();

            if (config == null) {
                return new LiveConfig();
            }

            // SAFETY
            while (config.slots.size() < 8) {
                config.slots.add(new LiveSlot());
            }

            return config;

        } catch (Exception e) {

            Log.e(TAG, "Failed to load config", e);
            return new LiveConfig();
        }
    }

    // =====================================================
    // SAVE
    // =====================================================

    public boolean saveConfig(LiveConfig config) {

        try {

            FileWriter writer = new FileWriter(getConfigFile());
            gson.toJson(config, writer);
            writer.flush();
            writer.close();
            return true;

        } catch (Exception e) {

            Log.e(TAG, "Failed to save config", e);
            return false;
        }
    }

    public void removeDeletedPatchReferences(
            String deletedFileName
    ) {

        LiveConfig config = loadConfig();

        boolean changed = false;

        for (int i = 0; i < config.slots.size(); i++) {

            LiveSlot slot = config.slots.get(i);

            if (slot.patchFileName == null) {
                continue;
            }

            if (slot.patchFileName.equals(deletedFileName)) {

                slot.patchFileName = null;

                if (config.currentSlotIndex == i) {
                    config.currentSlotIndex = -1;
                }
                changed = true;
            }
        }

        if (changed) {
            saveConfig(config);
        }
    }

}