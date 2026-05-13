package com.prajjwals.droidchitect.mainUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prajjwals.droidchitect.R;
import com.prajjwals.droidchitect.amp.AmpController;
import com.prajjwals.droidchitect.amp.AmpState;
import com.prajjwals.droidchitect.live.LiveConfigManager;
import com.prajjwals.droidchitect.patch.Patch;
import com.prajjwals.droidchitect.patch.PatchDialogs;
import com.prajjwals.droidchitect.patch.PatchLoader;
import com.prajjwals.droidchitect.patch.PatchManager;

import java.util.ArrayList;
import java.util.List;

public class PatchAdapter
        extends RecyclerView.Adapter<PatchAdapter.ViewHolder> {

    // =========================================================
    // DATA
    // =========================================================

    private final List<PatchManager.PatchEntry> entries = new ArrayList<>();

    // =========================================================
    // DEPENDENCIES
    // =========================================================

    private final PatchPageController controller;

    private final PatchManager patchManager;

    private final AmpController ampController;

    private final AmpState ampState;

    private final LiveConfigManager liveConfigManager;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PatchAdapter(
            PatchPageController controller,
            PatchManager patchManager,
            AmpController ampController,
            AmpState ampState,
            LiveConfigManager liveConfigManager
    ) {

        this.controller = controller;

        this.patchManager = patchManager;

        this.ampController = ampController;

        this.ampState = ampState;

        this.liveConfigManager = liveConfigManager;
    }

    // =========================================================
    // DATA UPDATE
    // =========================================================

    public void setEntries(List<PatchManager.PatchEntry> newEntries) {

        entries.clear();

        entries.addAll(newEntries);

        notifyDataSetChanged();
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView patchName;

        TextView loadButton;

        TextView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            patchName = itemView.findViewById(R.id.patchItemName);
            loadButton = itemView.findViewById(R.id.patchItemLoadButton);
            deleteButton = itemView.findViewById(R.id.patchItemDeleteButton);
        }
    }

    // =========================================================
    // CREATE VIEW
    // =========================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.patch_list_item,
                        parent,
                        false
                    );

        return new ViewHolder(view);
    }

    // =========================================================
    // BIND VIEW
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        PatchManager.PatchEntry entry = entries.get(position);

        // =====================================================
        // NAME
        // =====================================================

        holder.patchName.setText("🎛️  " + entry.patch.name);

        holder.itemView.setOnClickListener(v ->

                PatchDialogs.showPatchSummaryDialog(
                        v.getContext(),
                        entry.patch
                )
        );

        // =====================================================
        // LOAD
        // =====================================================

        holder.loadButton.setOnClickListener(v ->

                PatchLoader.applyPatch(
                        entry.patch,
                        ampController,
                        ampState,
                        new PatchLoader.PatchLoadCallback() {

                            @Override
                            public void onSuccess(
                                    Patch appliedPatch) {

                                holder.itemView.post(() -> {

                                    ampState.setCurrentPatchName(
                                            appliedPatch.name
                                    );

                                    ampState.setPatchDirty(
                                            false
                                    );

                                    controller.setCurrentPatch(appliedPatch);

                                    controller.refresh();

                                    android.widget.Toast.makeText(
                                            holder.itemView.getContext(),
                                            "Patch applied",
                                            android.widget.Toast.LENGTH_SHORT
                                    ).show();
                                });
                            }

                            @Override
                            public void onFailure(
                                    Patch failedPatch
                            ) {

                                holder.itemView.post(() -> {

                                    android.widget.Toast.makeText(
                                            holder.itemView.getContext(),
                                            "Patch sync failed",
                                            android.widget.Toast.LENGTH_SHORT
                                    ).show();
                                });
                            }
                        }
                )
        );

        // =====================================================
        // DELETE
        // =====================================================

        holder.deleteButton.setOnClickListener(v ->

                PatchDialogs.showDeleteDialog(
                        holder.itemView.getContext(),
                        patchManager,
                        entry.patch,
                        entry.fileName,
                        success -> {

                            if (success) {

                                // update currently cached patches
                                patchManager.reloadCache();

                                // remove live page references
                                liveConfigManager.removeDeletedPatchReferences(
                                        entry.fileName
                                );

                                // clear currently selected patch if needed
                                if (entry.patch.name.equals(ampState.getCurrentPatchName())) {
                                    ampState.setCurrentPatchName(null);
                                    ampState.setPatchDirty(false);
                                    controller.setCurrentPatch(null);
                                }

                                controller.refresh();

                                android.widget.Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Patch deleted",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                android.widget.Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Failed to delete patch",
                                        android.widget.Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
        );
    }

    // =========================================================
    // COUNT
    // =========================================================

    @Override
    public int getItemCount() {
        return entries.size();
    }
}