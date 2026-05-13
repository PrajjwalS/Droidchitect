
package com.prajjwals.droidchitect.mainUI;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.view.View;
import android.widget.TextView;


import com.prajjwals.droidchitect.R;
import com.prajjwals.droidchitect.amp.AmpController;
import com.prajjwals.droidchitect.amp.AmpState;
import com.prajjwals.droidchitect.amp.BlackstarConstants;
import com.prajjwals.droidchitect.patch.Patch;
import com.prajjwals.droidchitect.patch.PatchLoader;
import com.prajjwals.droidchitect.patch.PatchManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import com.prajjwals.droidchitect.patch.PatchDialogs;
import com.prajjwals.droidchitect.live.LiveConfigManager;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;

public class PatchPageController {

    // =========================================================
    // CORE
    // =========================================================

    private final View root;

    private final AmpState ampState;

    private final AmpController controller;

    private final PatchManager patchManager;

    // =========================================================
    // CURRENT PATCH
    // =========================================================

    private Patch currentPatch;

    // =========================================================
    // CURRENT PATCH UI
    // =========================================================

    private final TextView currentPatchName;

    private final TextView currentPatchSummary;

    private final TextView currentPatchAbout;

    // =========================================================
    // BUTTONS
    // =========================================================

    private final MaterialButton saveCurrentPatchButton;

    private final MaterialButton exportPatchButton;

    private final MaterialButton importPatchButton;

    // =========================================================
    // PATCH LIST
    // =========================================================

    private final RecyclerView patchRecyclerView;
    private final PatchAdapter patchAdapter;

    private final Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    private final TextInputEditText patchSearchInput;

    // Live stuff
    LiveConfigManager liveConfigManager;

    // PATCH IMPORT RELATED
    public static final int IMPORT_PATCH_REQUEST = 1001;

    // PATCH EXPORT related
    public static final int EXPORT_PATCH_REQUEST = 1002;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PatchPageController(
            View root,
            AmpState ampState,
            AmpController controller,
            PatchManager patchManager,
            LiveConfigManager liveConfigManager)
    {
        this.liveConfigManager = liveConfigManager;
        this.root = root;
        this.ampState = ampState;
        this.controller = controller;
        this.patchManager = patchManager;

        // =====================================================
        // CURRENT PATCH UI
        // =====================================================

        currentPatchName = root.findViewById(R.id.currentPatchName);

        currentPatchSummary = root.findViewById(R.id.currentPatchSummary);

        currentPatchAbout = root.findViewById(R.id.currentPatchAbout);

        // =====================================================
        // BUTTONS
        // =====================================================

        saveCurrentPatchButton = root.findViewById(R.id.saveCurrentPatchButton);

        exportPatchButton = root.findViewById(R.id.exportPatchButton);

        importPatchButton = root.findViewById(R.id.importPatchButton);

        // =====================================================
        // PATCH LIST
        // =====================================================

        patchRecyclerView = root.findViewById(R.id.patchRecyclerView);
        patchRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        patchAdapter = new PatchAdapter(this, patchManager, controller, ampState, liveConfigManager);

        patchRecyclerView.setAdapter(patchAdapter);
        patchRecyclerView.setHasFixedSize(true);
        // Try without the below line .. it adds some animation (but maybe slower with lower end devices)
        patchRecyclerView.setItemAnimator(null);

        // Patch search filter
        patchSearchInput = root.findViewById(R.id.patchSearchInput);
    }

    // =========================================================
    // INIT
    // =========================================================

    public void init() {
        setupCurrentPatchCard();
        setupButtons();
        setupSearch();
        refresh();
    }

    // =========================================================
    // SETUP
    // =========================================================

    private void setupCurrentPatchCard() {
        root.findViewById(R.id.currentPatchCard).setOnClickListener(v -> {

            if (currentPatch != null) {

                PatchDialogs.showPatchSummaryDialog(
                        root.getContext(),
                        currentPatch
                );
            }
        });
    }

    private void setupButtons() {

        saveCurrentPatchButton.setOnClickListener(v ->
                PatchDialogs.showSavePatchDialog(
                        root.getContext(),
                        ampState,
                        patchManager,
                        (patch, success) -> {
                            if (!success) {

                                android.widget.Toast.makeText(
                                        root.getContext(),
                                        "Failed to save patch",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            patchManager.reloadCache(); // maybe wrap this function in a thread .. if patches grow too big

                            currentPatch = patch;
                            ampState.setCurrentPatchName(
                                    patch.name
                            );

                            ampState.setPatchDirty(false);
                            refreshCurrentPatchCard();
                            refreshPatchList();

                            android.widget.Toast.makeText(
                                    root.getContext(),
                                    "Patch saved",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();

                        }
                )
        );

        importPatchButton.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.setType("*/*");

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            intent.addCategory(Intent.CATEGORY_OPENABLE);

            ((Activity) root.getContext())
                    .startActivityForResult(
                            intent,
                            IMPORT_PATCH_REQUEST
                    );
        });

        exportPatchButton.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

            ((Activity) root.getContext())
                    .startActivityForResult(
                            intent,
                            EXPORT_PATCH_REQUEST
                    );
        });
    }

    private void setupSearch() {

        patchSearchInput.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {

                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        if (searchRunnable != null) {

                            searchHandler.removeCallbacks(
                                    searchRunnable
                            );
                        }

                        searchRunnable = () ->
                                refreshPatchList();

                        searchHandler.postDelayed(
                                searchRunnable,
                                150
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }
                }
        );
    }

    // =========================================================
    // REFRESH
    // =========================================================

    public void refresh() {
        refreshCurrentPatchCard();
        refreshPatchList();
    }

    // =========================================================
    // CURRENT PATCH CARD
    // =========================================================

    public void setCurrentPatch(Patch patch) {
        currentPatch = patch;
    }
    private void refreshCurrentPatchCard() {

        if (currentPatch == null) {

            String currentPatchNameValue = ampState.getCurrentPatchName();

            if (currentPatchNameValue != null) {

                for (PatchManager.PatchEntry entry : patchManager.getCachedPatches()) {

                    Patch patch = entry.patch;

                    if (patch == null) {
                        continue;
                    }

                    if (currentPatchNameValue.equals(
                            patch.name
                    )) {

                        currentPatch = patch;

                        break;
                    }
                }
            }
        }

        if (currentPatch == null) {
            currentPatchName.setText("🎛️  Unsaved Patch");
            currentPatchSummary.setText("Current amplifier state");
            currentPatchAbout.setText("Save the current amplifier configuration as a patch.");
            return;
        }

        String title = "🎛️  " + currentPatch.name;

        if (ampState.isPatchDirty()) {
            title += " *";
        }

        currentPatchName.setText(title);

        currentPatchSummary.setText(buildPatchSummary(currentPatch));

        if (currentPatch.about != null && !currentPatch.about.trim().isEmpty()) {
            currentPatchAbout.setText(currentPatch.about);
        } else {
            currentPatchAbout.setText("No description");
        }
    }

    private void toggleCurrentPatchAboutExpanded() {

        if (currentPatchAbout.getMaxLines() == 2) {
            currentPatchAbout.setMaxLines(Integer.MAX_VALUE);
            currentPatchAbout.setEllipsize(null);
        } else {

            currentPatchAbout.setMaxLines(2);
            currentPatchAbout.setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    // =========================================================
    // PATCH LIST
    // =========================================================

    private void refreshPatchList() {
        List<PatchManager.PatchEntry> entries = getFilteredPatches();
        patchAdapter.setEntries(entries);
    }

    private List<PatchManager.PatchEntry> getFilteredPatches() {

        List<PatchManager.PatchEntry> results = new ArrayList<>();

        String query = getSearchQuery();

        for (PatchManager.PatchEntry entry : patchManager.getCachedPatches()) {

            if (!matchesQuery(entry.patch, query)) {
                continue;
            }

            results.add(entry);
        }

        return results;
    }


    private String getSearchQuery() {

        if (patchSearchInput.getText() == null) {
            return "";
        }

        return patchSearchInput
                .getText()
                .toString()
                .trim()
                .toLowerCase();
    }

    private boolean matchesQuery(Patch patch, String query) {

        if (query.isEmpty()) {
            return true;
        }

        boolean matchesName = patch.name != null && patch.name.toLowerCase().contains(query);

        boolean matchesCreator = patch.creator != null && patch.creator.toLowerCase().contains(query);

        boolean matchesAbout = patch.about != null && patch.about.toLowerCase().contains(query);

        return matchesName || matchesCreator || matchesAbout;
    }

    // =========================================================
    // PATCH CARD
    // =========================================================


    private void loadPatch(Patch patch) {

        PatchLoader.applyPatch(
                patch,
                controller,
                ampState,
                new PatchLoader.PatchLoadCallback() {

                    @Override
                    public void onSuccess(Patch appliedPatch) {

                        root.post(() -> {
                            currentPatch = appliedPatch;
                            ampState.setCurrentPatchName(appliedPatch.name);
                            ampState.setPatchDirty(false);
                            refreshCurrentPatchCard();

                            // Toast this
                            android.widget.Toast.makeText(
                                    root.getContext(),
                                    "Patch applied",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onFailure(Patch failedPatch) {

                        root.post(() -> {

                            android.widget.Toast.makeText(
                                    root.getContext(),
                                    "Patch sync failed",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();
                        });
                    }
                }
        );
    }
    // =========================================================
    // PATCH SUMMARY
    // =========================================================

    private String buildPatchSummary(Patch patch) {

        List<String> items = new ArrayList<>();

        items.add(BlackstarConstants.VOICES[patch.voice]);

        if (patch.modulationEnabled) {
            items.add("Mod");
        }

        if (patch.delayEnabled) {
            items.add("Delay");
        }

        if (patch.reverbEnabled) {
            items.add("Reverb");
        }

        if (patch.noiseGateEnabled) {
            items.add("Gate");
        }

        return TextUtils.join(
                " • ",
                items
        );
    }


    // Patch import related
    public void importPatchUris(java.util.List<android.net.Uri> uris) {
        PatchManager.ImportSummary summary = patchManager.importPatchUris(uris);

        patchManager.reloadCache();

        refreshPatchList();

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(
                        root.getContext(),
                        R.style.ThemeOverlay_Droidchitect_Dialog
                )
                        .setTitle(
                                "Patch Import Summary"
                        )
                        .setMessage(
                                summary.buildSummaryText()
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .create();

        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(
                root.getContext()
                        .getResources()
                        .getColor(R.color.accent_orange)
        );
    }


    // EXPORT PAGE HANDLER

    public void exportPatches(android.net.Uri folderUri) {

        boolean success = patchManager.exportAllPatchesZip(folderUri);

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(
                        root.getContext(),
                        R.style.ThemeOverlay_Droidchitect_Dialog
                )
                        .setTitle(
                                success
                                        ? "Export Complete"
                                        : "Export Failed"
                        )
                        .setMessage(
                                success
                                        ? "All patches exported successfully."
                                        : "Failed to export patches."
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .create();

        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(
                root.getContext()
                        .getResources()
                        .getColor(R.color.accent_orange)
                );
    }
}
