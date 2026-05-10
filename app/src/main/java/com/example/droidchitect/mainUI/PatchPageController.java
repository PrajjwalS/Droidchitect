
package com.example.droidchitect.mainUI;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.droidchitect.R;
import com.example.droidchitect.amp.AmpController;
import com.example.droidchitect.amp.AmpState;
import com.example.droidchitect.amp.BlackstarConstants;
import com.example.droidchitect.patch.Patch;
import com.example.droidchitect.patch.PatchLoader;
import com.example.droidchitect.patch.PatchManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import com.example.droidchitect.patch.PatchDialogs;
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

    // =========================================================
    // PATCH LIST
    // =========================================================

    private final LinearLayout patchListContainer;

    private final TextInputEditText patchSearchInput;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PatchPageController(
            View root,
            AmpState ampState,
            AmpController controller,
            PatchManager patchManager)
    {
        this.root = root;
        this.ampState = ampState;
        this.controller = controller;
        this.patchManager = patchManager;

        // =====================================================
        // CURRENT PATCH UI
        // =====================================================

        currentPatchName =
                root.findViewById(
                        R.id.currentPatchName
                );

        currentPatchSummary =
                root.findViewById(
                        R.id.currentPatchSummary
                );

        currentPatchAbout =
                root.findViewById(
                        R.id.currentPatchAbout
                );

        // =====================================================
        // BUTTONS
        // =====================================================

        saveCurrentPatchButton =
                root.findViewById(
                        R.id.saveCurrentPatchButton
                );

        exportPatchButton =
                root.findViewById(
                        R.id.exportPatchButton
                );

        // =====================================================
        // PATCH LIST
        // =====================================================

        patchListContainer =
                root.findViewById(
                        R.id.patchListContainer
                );

        patchSearchInput =
                root.findViewById(
                        R.id.patchSearchInput
                );
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
        currentPatchAbout.setOnClickListener(v ->
                toggleCurrentPatchAboutExpanded()
        );
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

        exportPatchButton.setOnClickListener(v -> {

            // TODO

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

                        refreshPatchList();
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

    private void refreshCurrentPatchCard() {

        if (currentPatch == null) {

            String currentPatchNameValue =
                    ampState.getCurrentPatchName();

            if (currentPatchNameValue != null) {

                for (PatchManager.PatchEntry entry :
                        patchManager.getCachedPatches()) {

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

            currentPatchName.setText(
                    "🎛️  Unsaved Patch"
            );

            currentPatchSummary.setText(
                    "Current amplifier state"
            );

            currentPatchAbout.setText(
                    "Save the current amplifier configuration as a patch."
            );

            return;
        }

        String title =
                "🎛️  " + currentPatch.name;

        if (ampState.isPatchDirty()) {
            title += " *";
        }

        currentPatchName.setText(title);

        currentPatchSummary.setText(
                buildPatchSummary(currentPatch)
        );

        if (currentPatch.about != null &&
                !currentPatch.about.trim().isEmpty()) {

            currentPatchAbout.setText(
                    currentPatch.about
            );

        } else {

            currentPatchAbout.setText(
                    "No description"
            );
        }
    }

    private void toggleCurrentPatchAboutExpanded() {

        if (currentPatchAbout.getMaxLines() == 2) {

            currentPatchAbout.setMaxLines(
                    Integer.MAX_VALUE
            );

            currentPatchAbout.setEllipsize(
                    null
            );

        } else {

            currentPatchAbout.setMaxLines(2);

            currentPatchAbout.setEllipsize(
                    TextUtils.TruncateAt.END
            );
        }
    }

    // =========================================================
    // PATCH LIST
    // =========================================================

    private void refreshPatchList() {

        patchListContainer.removeAllViews();

        List<PatchManager.PatchEntry> entries =
                getFilteredPatches();

        if (entries.isEmpty()) {

            addEmptyState();

            return;
        }

        for (PatchManager.PatchEntry entry : entries) {

            addPatchCard(entry);
        }
    }

    private List<PatchManager.PatchEntry>
    getFilteredPatches() {

        List<PatchManager.PatchEntry> results =
                new ArrayList<>();

        String query =
                getSearchQuery();

        for (PatchManager.PatchEntry entry :
                patchManager.getCachedPatches()) {

            if (!matchesQuery(
                    entry.patch,
                    query
            )) {
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

    private boolean matchesQuery(
            Patch patch,
            String query
    ) {

        if (query.isEmpty()) {
            return true;
        }

        boolean matchesName =
                patch.name != null &&
                        patch.name.toLowerCase()
                                .contains(query);

        boolean matchesCreator =
                patch.creator != null &&
                        patch.creator.toLowerCase()
                                .contains(query);

        boolean matchesAbout =
                patch.about != null &&
                        patch.about.toLowerCase()
                                .contains(query);

        return matchesName ||
                matchesCreator ||
                matchesAbout;
    }

    private void addEmptyState() {

        TextView emptyView =
                new TextView(
                        root.getContext()
                );

        emptyView.setText(
                "No saved patches"
        );

        emptyView.setTextSize(14);

        emptyView.setPadding(
                24,
                24,
                24,
                24
        );

        emptyView.setGravity(
                Gravity.CENTER
        );

        emptyView.setTextColor(
                root.getResources().getColor(
                        R.color.text_secondary
                )
        );

        patchListContainer.addView(
                emptyView
        );
    }

    // =========================================================
    // PATCH CARD
    // =========================================================

    private void addPatchCard(
            PatchManager.PatchEntry entry
    ) {

        View card =
                LayoutInflater.from(
                        root.getContext()
                ).inflate(
                        R.layout.patch_list_item,
                        patchListContainer,
                        false
                );

        // =====================================================
        // VIEWS
        // =====================================================

        TextView patchName =
                card.findViewById(
                        R.id.patchItemName
                );

        TextView loadButton =
                card.findViewById(
                        R.id.patchItemLoadButton
                );

        TextView deleteButton =
                card.findViewById(
                        R.id.patchItemDeleteButton
                );

        // =====================================================
        // DATA
        // =====================================================

        patchName.setText(
                "🎛️  " + entry.patch.name
        );

        // =====================================================
        // ACTIONS
        // =====================================================

        loadButton.setOnClickListener(v ->
                loadPatch(
                        entry.patch
                )
        );

        deleteButton.setOnClickListener(v ->
                PatchDialogs.showDeleteDialog(
                        root.getContext(),
                        patchManager,
                        entry.patch,
                        entry.fileName,
                        success -> {

                            if (success) {
                                patchManager.reloadCache();
                                refreshPatchList();
                                android.widget.Toast.makeText(
                                        root.getContext(),
                                        "Patch deleted",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                android.widget.Toast.makeText(
                                        root.getContext(),
                                        "Failed to delete patch",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
        );

        // =====================================================
        // ADD
        // =====================================================

        patchListContainer.addView(
                card
        );
    }

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

    private String buildPatchSummary(
            Patch patch
    ) {

        List<String> items =
                new ArrayList<>();

        items.add(
                BlackstarConstants.VOICES[
                        patch.voice
                        ]
        );

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

}
