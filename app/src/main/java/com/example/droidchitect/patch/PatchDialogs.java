package com.example.droidchitect.patch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.droidchitect.R;
import com.example.droidchitect.amp.AmpState;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class PatchDialogs {

    // =========================================================
    // SAVE PATCH
    // =========================================================

    public static void showSavePatchDialog(
            Context context,
            AmpState ampState,
            PatchManager patchManager,
            SavePatchCallback callback) {

        View dialogView = LayoutInflater.from(context).inflate(
                                R.layout.save_patch_dialog,
                                null
                          );

        EditText editName = dialogView.findViewById(R.id.editPatchName);

        EditText editCreator = dialogView.findViewById(R.id.editPatchCreator);

        EditText editTags = dialogView.findViewById(R.id.editPatchTags);

        EditText editAbout = dialogView.findViewById(R.id.editPatchAbout);

        new MaterialAlertDialogBuilder(
                context,
                R.style.ThemeOverlay_Droidchitect_Dialog)
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
                                        new ArrayList<>(
                                                Arrays.asList(
                                                        tagsText.split(",")
                                                )
                                        );
                            }

                            if (patch.name.isEmpty()) {

                                patch.name =
                                        "Untitled Patch";
                            }

                            boolean success =
                                    patchManager.savePatch(
                                            patch
                                    );

                            if (callback != null) {

                                callback.onPatchSaved(
                                        patch,
                                        success
                                );
                            }
                        }
                ).show();
    }

    // =========================================================
    // DELETE PATCH
    // =========================================================

    public static void showDeleteDialog(
            Context context,
            PatchManager patchManager,
            Patch patch,
            String fileName,
            DeletePatchCallback callback    ) {

        AlertDialog dialog =
                new AlertDialog.Builder(context, R.style.ThemeOverlay_Droidchitect_Dialog)
                        .setTitle("Delete Patch")
                        .setMessage(
                                "Delete \"" +
                                        patch.name +
                                        "\" ?"
                        )
                        .setPositiveButton(
                                "Delete",
                                (d, which) -> {

                                    boolean success =
                                            patchManager.deletePatch(fileName);

                                    if (callback != null) {
                                        callback.onDelete(success);
                                    }
                                }
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                context.getResources().getColor(R.color.accent_orange)
        );

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                context.getResources().getColor(R.color.text_secondary));

    }

    // =========================================================
    // CALLBACK
    // =========================================================

    public interface SavePatchCallback {
        void onPatchSaved(Patch patch, boolean success);
    }

    public interface DeletePatchCallback {
        void onDelete(boolean success);
    }

}