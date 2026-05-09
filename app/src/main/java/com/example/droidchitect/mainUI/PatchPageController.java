package com.example.droidchitect.mainUI;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.droidchitect.patch.PatchMapper;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;



import java.util.List;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;


import android.widget.Toast;


public class PatchPageController {

    // =========================================================
    // CORE
    // =========================================================

    private final View root;

    private final AmpState ampState;

    private final AmpController controller;

    private final MainActivity activity;


    // =========================================================
    // CURRENT PATCH UI
    // =========================================================

    private final TextView currentPatchName;

    private final TextView currentPatchSummary;

    private final TextView currentPatchAbout;

    Context context;
    // =========================================================
    // ACTION BUTTONS
    // =========================================================

    private final MaterialButton saveCurrentPatchButton;

    private final MaterialButton exportPatchButton;

    // =========================================================
    // PATCH LIST
    // =========================================================

    private final LinearLayout patchListContainer;

    // =========================================================
    // PATCH manager
    // =========================================================
    private final PatchManager patchManager;


    // =========================================================
    // To track current patch
    // =========================================================
    private Patch currentPatch;


    // =========================================================
    // Filter for searchig patches
    // =========================================================
    private final TextInputEditText patchSearchInput;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PatchPageController(

            MainActivity activity,
            View root,
            AmpState ampState,
            AmpController controller
    ){
        this.activity = activity;
        this.context = activity;

        this.root = root;

        this.ampState = ampState;

        this.controller = controller;

        // =====================================================
        // CURRENT PATCH
        // =====================================================

        currentPatchName =
                root.findViewById(R.id.currentPatchName);

        currentPatchSummary =
                root.findViewById(R.id.currentPatchSummary);

        currentPatchAbout =
                root.findViewById(R.id.currentPatchAbout);
        currentPatchAbout.setOnClickListener(v -> {

            if (currentPatchAbout.getMaxLines() == 2) {

                currentPatchAbout.setMaxLines(
                        Integer.MAX_VALUE
                );

                currentPatchAbout.setEllipsize(
                        null
                );

            } else {

                currentPatchAbout.setMaxLines(
                        2
                );

                currentPatchAbout.setEllipsize(
                        TextUtils.TruncateAt.END
                );
            }
        });

        // =====================================================
        // BUTTONS
        // =====================================================

        saveCurrentPatchButton =
                root.findViewById(R.id.saveCurrentPatchButton);

        exportPatchButton =
                root.findViewById(R.id.exportPatchButton);

        // =====================================================
        // PATCH manager
        // =====================================================
        patchManager =
                new PatchManager(context);

        // =====================================================
        // PATCH search filter
        // =====================================================
        patchSearchInput =
                root.findViewById(
                        R.id.patchSearchInput
                );

        // =====================================================
        // PATCH LIST
        // =====================================================

        patchListContainer =
                root.findViewById(R.id.patchListContainer);

    }

    // =========================================================
    // INIT
    // =========================================================

    public void init() {

        setupButtons();
        setupSearch();
        refresh();
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

        // =========================================
        // NO PATCH LOADED
        // =========================================

        if (currentPatch == null) {

            currentPatchName.setText(
                    "\uD83C\uDF9B\uFE0F  "+ "Unsaved Patch"
            );

            currentPatchSummary.setText(
                    "Current amplifier state"
            );

            currentPatchAbout.setText(
                    "Save the current amplifier configuration as a patch."
            );

            return;
        }

        // =========================================
        // PATCH LOADED
        // =========================================

        currentPatchName.setText(
                "\uD83C\uDF9B\uFE0F  " + currentPatch.name
        );

        currentPatchSummary.setText(
                buildPatchSummary(
                        currentPatch
                )
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
    // =========================================================
    // BUTTONS
    // =========================================================

    private void setupButtons() {

        saveCurrentPatchButton.setOnClickListener(v -> {
            showSavePatchDialog();
        });

        exportPatchButton.setOnClickListener(v -> {

        });
    }

    // Helper Functions
    private void showSavePatchDialog() {

        View dialogView =
                LayoutInflater.from(root.getContext())
                        .inflate(
                                R.layout.save_patch_dialog,
                                null
                        );

        EditText editName =
                dialogView.findViewById(
                        R.id.editPatchName
                );

        EditText editCreator =
                dialogView.findViewById(
                        R.id.editPatchCreator
                );

        EditText editTags =
                dialogView.findViewById(
                        R.id.editPatchTags
                );

        EditText editAbout =
                dialogView.findViewById(
                        R.id.editPatchAbout
                );

        new MaterialAlertDialogBuilder(
                root.getContext(),
                R.style.ThemeOverlay_Droidchitect_Dialog
        )

                .setTitle("Save Patch")

                .setView(dialogView)

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Save",
                        (dialog, which) -> {

                            Patch patch =
                                    PatchMapper.fromAmpState(
                                            ampState
                                    );

                            // =====================================
                            // METADATA
                            // =====================================

                            patch.name =
                                    editName.getText()
                                            .toString()
                                            .trim();

                            patch.creator =
                                    editCreator.getText()
                                            .toString()
                                            .trim();

                            patch.about =
                                    editAbout.getText()
                                            .toString()
                                            .trim();

                            String tagsText =
                                    editTags.getText()
                                            .toString()
                                            .trim();

                            if (!tagsText.isEmpty()) {

                                patch.tags =
                                        Arrays.asList(
                                                tagsText.split(",")
                                        );
                            }

                            // =====================================
                            // FALLBACK NAME
                            // =====================================

                            if (patch.name.isEmpty()) {

                                patch.name =
                                        "Untitled Patch";
                            }

                            // =====================================
                            // SAVE
                            // =====================================

                            patchManager.savePatch(
                                    patch
                            );

                            currentPatch = patch;

                            refreshCurrentPatchCard();

                            refreshPatchList();
                        })

                .show();
    }

    private void refreshPatchList() {

        patchListContainer.removeAllViews();

        String query =
                "";

        if (patchSearchInput.getText() != null) {

            query =
                    patchSearchInput
                            .getText()
                            .toString()
                            .trim()
                            .toLowerCase();
        }

        List<String> patchFiles =
                patchManager.listPatchFiles();

        for (String fileName : patchFiles) {

            Patch patch =
                    patchManager.loadPatch(
                            fileName
                    );

            if (patch == null) {
                continue;
            }

            // =================================================
            // FILTER
            // =================================================

            if (!query.isEmpty()) {

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

                if (!matchesName &&
                        !matchesCreator &&
                        !matchesAbout) {

                    continue;
                }
            }

            addPatchCard(
                    patch,
                    fileName
            );
        }
    }

    private void addPatchCard(
            Patch patch,
            String fileName
    ) {

        // =====================================================
        // OUTER CONTAINER
        // =====================================================

        LinearLayout outerContainer =
                new LinearLayout(
                        root.getContext()
                );

        outerContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams outerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        outerParams.setMargins(
                0,
                0,
                0,
                8
        );

        outerContainer.setLayoutParams(
                outerParams
        );

        // =====================================================
        // CARD
        // =====================================================

        LinearLayout card =
                new LinearLayout(
                        root.getContext()
                );

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setBackgroundResource(
                R.drawable.patch_card_bg
        );

        card.setElevation(2f);

        card.setPadding(
                16,
                14,
                16,
                12
        );

        // =====================================================
        // PATCH NAME
        // =====================================================

        TextView name =
                new TextView(
                        root.getContext()
                );

        name.setText(
                "\uD83C\uDF9B\uFE0F  " + patch.name
        );

        // MATCH CURRENT PATCH CARD
        name.setTextSize(17);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        name.setTextColor(
                root.getResources().getColor(
                        R.color.text_primary
                )
        );

        name.setMaxLines(1);

        name.setEllipsize(
                android.text.TextUtils.TruncateAt.END
        );

        card.addView(name);

        // =====================================================
        // BUTTON ROW
        // =====================================================

        LinearLayout buttonRow =
                new LinearLayout(
                        root.getContext()
                );

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        buttonRow.setPadding(
                0,
                10,
                0,
                0
        );

        // =====================================================
        // LOAD BUTTON
        // =====================================================

        TextView loadButton =
                new TextView(
                        root.getContext()
                );

        loadButton.setText("LOAD");

        loadButton.setTextSize(9);

        loadButton.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        loadButton.setTextColor(
                root.getResources().getColor(
                        R.color.text_primary
                )
        );

        loadButton.setGravity(
                android.view.Gravity.CENTER
        );

        loadButton.setBackgroundResource(
                R.drawable.patch_chip_bg
        );

        loadButton.setPadding(
                0,
                6,
                0,
                6
        );

        loadButton.setOnClickListener(v -> {

            PatchLoader.applyPatch(
                    patch,
                    controller,
                    ampState
            );

            currentPatch = patch;

            refreshCurrentPatchCard();
        });

        // =====================================================
        // DELETE BUTTON
        // =====================================================

        TextView deleteButton =
                new TextView(
                        root.getContext()
                );

        deleteButton.setText("DELETE");

        deleteButton.setTextSize(9);

        deleteButton.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        deleteButton.setTextColor(
                root.getResources().getColor(
                        R.color.text_primary
                )
        );

        deleteButton.setGravity(
                android.view.Gravity.CENTER
        );

        deleteButton.setBackgroundResource(
                R.drawable.patch_chip_bg
        );

        deleteButton.setPadding(
                0,
                6,
                0,
                6
        );

        deleteButton.setOnClickListener(v -> {

            AlertDialog dialog =
                    new AlertDialog.Builder(
                            root.getContext(),
                            R.style.ThemeOverlay_Droidchitect_Dialog
                    )
                            .setTitle("Delete Patch")
                            .setMessage(
                                    "Delete \"" +
                                            patch.name +
                                            "\" ?"
                            )
                            .setPositiveButton(
                                    "Delete",
                                    (d, which) -> {

                                        patchManager.deletePatch(
                                                fileName
                                        );

                                        refreshPatchList();
                                    }
                            )
                            .setNegativeButton(
                                    "Cancel",
                                    null
                            )
                            .create();

            dialog.show();


            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setTextColor(
                    root.getResources().getColor(
                            R.color.accent_orange
                    )
            );

            dialog.getButton(
                    AlertDialog.BUTTON_NEGATIVE
            ).setTextColor(
                    root.getResources().getColor(
                            R.color.text_secondary
                    )
            );
        });

        // =====================================================
        // BUTTON LAYOUTS
        // =====================================================

        LinearLayout.LayoutParams loadParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        loadParams.setMargins(
                0,
                0,
                8,
                0
        );

        loadButton.setLayoutParams(
                loadParams
        );

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        deleteButton.setLayoutParams(
                deleteParams
        );

        buttonRow.addView(
                loadButton
        );

        buttonRow.addView(
                deleteButton
        );

        card.addView(
                buttonRow
        );

        outerContainer.addView(
                card
        );

        patchListContainer.addView(
                outerContainer
        );
    }

    private String buildPatchSummary(
            Patch patch
    ) {

        List<String> items =
                new ArrayList<>();

        items.add(
                BlackstarConstants.VOICES[patch.voice]
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
}