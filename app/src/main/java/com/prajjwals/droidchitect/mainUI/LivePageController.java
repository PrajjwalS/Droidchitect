package com.prajjwals.droidchitect.mainUI;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.prajjwals.droidchitect.MainActivity;
import com.prajjwals.droidchitect.R;
import com.prajjwals.droidchitect.amp.AmpController;
import com.prajjwals.droidchitect.amp.AmpState;
import com.prajjwals.droidchitect.live.LiveConfig;
import com.prajjwals.droidchitect.live.LiveConfigManager;
import com.prajjwals.droidchitect.live.LiveSlot;
import com.prajjwals.droidchitect.patch.Patch;
import com.prajjwals.droidchitect.patch.PatchLoader;
import com.prajjwals.droidchitect.patch.PatchManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;

public class LivePageController {

    // =====================================================
    // CORE
    // =====================================================

    private final View root;

    private final PatchManager patchManager;

    private final LiveConfigManager liveConfigManager;

    private final AmpController ampController;

    private final AmpState ampState;

    // =====================================================
    // UI
    // =====================================================

    private final MaterialSwitch switchGoLive;

    private final GridLayout liveSlotContainer;

    private final MaterialButton buttonSwitchPatch;

    // =====================================================
    // STATE
    // =====================================================

    private LiveConfig config;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    @SuppressLint("WrongViewCast")
    public LivePageController(
            View root,
            PatchManager patchManager,
            LiveConfigManager liveConfigManager,
            AmpController ampController,
            AmpState ampState
    ) {

        this.root = root;

        this.patchManager = patchManager;

        this.liveConfigManager = liveConfigManager;

        this.ampController = ampController;

        this.ampState = ampState;

        switchGoLive = root.findViewById(R.id.switchGoLive);

        liveSlotContainer = root.findViewById(R.id.liveSlotContainer);

        buttonSwitchPatch = root.findViewById(R.id.buttonSwitchPatch);
    }

    // =====================================================
    // INIT
    // =====================================================

    public void init() {

        config = liveConfigManager.loadConfig();

        // always be in non go live mode when we enter this page.
        config.goLiveEnabled = false;
        liveConfigManager.saveConfig(config);

        setupGoLiveSwitch();
        setupSwitchPatchButton();
        refreshUI();
    }

    // =====================================================
    // GO LIVE
    // =====================================================

    private void setupGoLiveSwitch() {

        switchGoLive.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    config.goLiveEnabled = isChecked;
                    ((MainActivity) root.getContext()).setBottomNavVisible(!isChecked);
                    liveConfigManager.saveConfig(config);

                    refreshUI();
                }
        );
    }

    // =====================================================
    // SWITCH PATCH
    // =====================================================

    private void setupSwitchPatchButton() {
        buttonSwitchPatch.setOnClickListener(v -> switchToNextPatch());
    }

    private void switchToNextPatch() {

        for (int offset = 1; offset <= 8; offset++) {

            int index = (config.currentSlotIndex + offset) % 8;

            LiveSlot slot = config.slots.get(index);

            if (slot.patchFileName == null) {
                continue;
            }

            Patch patch = patchManager.loadPatch(slot.patchFileName);

            if (patch == null) {
                continue;
            }

            config.currentSlotIndex = index;

            liveConfigManager.saveConfig(config);

            applyPatch(patch);
            refreshSlots();

            return;
        }
    }

    // =====================================================
    // REFRESH
    // =====================================================

    private void refreshUI() {

        ((MainActivity) root.getContext()).setBottomNavVisible(!config.goLiveEnabled);

        switchGoLive.setChecked(config.goLiveEnabled);

        buttonSwitchPatch.setVisibility(
                config.goLiveEnabled
                        ? View.VISIBLE
                        : View.GONE
        );
        refreshSlots();
    }

    // =====================================================
    // SLOTS
    // =====================================================

    private void refreshSlots() {

        liveSlotContainer.removeAllViews();

        for (int i = 0; i < 8; i++) {
            addSlotCard(i);
        }
    }

    private void addSlotCard(int index) {

        View card = LayoutInflater.from(root.getContext()).inflate(
                        R.layout.live_slot_item,
                        liveSlotContainer,
                        false
                    );

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.width = 0;
        params.height = 0;

        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

        card.setLayoutParams(params);

        // =====================================================
        // VIEWS
        // =====================================================

        MaterialCardView liveSlotCard = card.findViewById(R.id.liveSlotCard);

        TextView textSlotTitle = card.findViewById(R.id.textSlotTitle);

        TextView textPatchName = card.findViewById(R.id.textPatchName);

        // =====================================================
        // SLOT
        // =====================================================

        LiveSlot slot = config.slots.get(index);

        textSlotTitle.setText("\uD83C\uDF9B\uFE0F SLOT " + (index + 1));

        // =====================================================
        // PATCH
        // =====================================================

        Patch patch = null;

        if (slot.patchFileName != null) {
            patch = patchManager.loadPatch(slot.patchFileName);
        }

        if (patch != null) {
            textPatchName.setText(patch.name);

        } else {
            textPatchName.setText("EMPTY");
        }

        // =====================================================
        // TAP
        // =====================================================

        Patch finalPatch = patch;

        liveSlotCard.setOnClickListener(v -> {

            // =============================================
            // CONFIG MODE
            // =============================================

            if (!config.goLiveEnabled) {
                showPatchPickerDialog(index);
                return;
            }

            // =============================================
            // LIVE MODE
            // =============================================

            if (finalPatch == null) {
                return;
            }

            config.currentSlotIndex = index;

            liveConfigManager.saveConfig(config);

            applyPatch(finalPatch);

            refreshSlots();
        });

        // =====================================================
        // LONG PRESS CLEAR
        // =====================================================

        liveSlotCard.setOnLongClickListener(v -> {

            if (config.goLiveEnabled) {
                return true;
            }

            slot.patchFileName = null;

            if (config.currentSlotIndex == index) {
                config.currentSlotIndex = 0;
            }

            liveConfigManager.saveConfig(config);

            refreshSlots();

            android.widget.Toast.makeText(
                    root.getContext(),
                    "Slot cleared",
                    android.widget.Toast.LENGTH_SHORT
            ).show();

            return true;
        });

        // =====================================================
        // ACTIVE SLOT
        // =====================================================

        if ( config.goLiveEnabled && config.currentSlotIndex == index) {

            liveSlotCard.setStrokeWidth(6);

            liveSlotCard.setStrokeColor(
                    root.getResources().getColor(
                            R.color.accent_orange
                    )
            );

        } else {
            liveSlotCard.setStrokeWidth(0);
        }

        // =====================================================
        // ADD
        // =====================================================

        liveSlotContainer.addView(card);
    }

    // =====================================================
    // PATCH PICKER
    // =====================================================

    private void showPatchPickerDialog(int slotIndex) {

        List<PatchManager.PatchEntry> entries = patchManager.getCachedPatches();

        if (entries.isEmpty()) {

            android.widget.Toast.makeText(
                    root.getContext(),
                    "No saved patches",
                    android.widget.Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String[] names = new String[entries.size()];

        for (int i = 0; i < entries.size(); i++) {

            names[i] = entries.get(i).patch.name;
        }

        AlertDialog dialog =
                new AlertDialog.Builder(
                        root.getContext()
                )
                        .setTitle("Assign Patch")

                        .setItems(
                                names,
                                (d, which) -> {

                                    PatchManager.PatchEntry entry =
                                            entries.get(which);

                                    LiveSlot slot =
                                            config.slots.get(
                                                    slotIndex
                                            );

                                    slot.patchFileName =
                                            entry.fileName;

                                    liveConfigManager.saveConfig(
                                            config
                                    );

                                    refreshSlots();
                                }
                        )

                        .create();

        dialog.show();

        if (dialog.getWindow() != null) {

            int width = (int)(
                    root.getResources()
                            .getDisplayMetrics()
                            .widthPixels * 0.85
            );
            int height = (int)(
                    root.getResources()
                            .getDisplayMetrics()
                            .heightPixels * 0.65
            );


            dialog.getWindow().setLayout(
                    width,
                    height
            );

            dialog.getWindow().setBackgroundDrawableResource(
                    R.drawable.patch_select_dialog_background
            );
        }
    }

    // =====================================================
    // APPLY PATCH
    // =====================================================

    private void applyPatch(Patch patch) {

        PatchLoader.applyPatch(
                patch,
                ampController,
                ampState,
                new PatchLoader.PatchLoadCallback() {

                    @Override
                    public void onSuccess(
                            Patch appliedPatch
                    ) {

                        root.post(() -> {

                            ampState.setCurrentPatchName(
                                    appliedPatch.name
                            );

                            ampState.setPatchDirty(
                                    false
                            );

                            android.widget.Toast.makeText(
                                    root.getContext(),
                                    "Patch applied",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onFailure(
                            Patch failedPatch
                    ) {

                        root.post(() ->

                                android.widget.Toast.makeText(
                                        root.getContext(),
                                        "Patch sync failed",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                }
        );
    }
}