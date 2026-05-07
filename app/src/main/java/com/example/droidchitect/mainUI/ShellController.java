package com.example.droidchitect.mainUI;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.droidchitect.R;

public class ShellController {

    public interface NavigationListener {
        void onAmpSelected();
        void onEffectsSelected();
        void onPatchSelected();

        void onConnectClicked();
    }

    private final View root;
    private final NavigationListener listener;

    private final TextView tabAmp;
    private final TextView tabEffects;
    private final TextView tabPatch;

    private final TextView statusChip;

    public ShellController(View root, NavigationListener listener) {

        this.root = root;
        this.listener = listener;

        statusChip = root.findViewById(R.id.status_chip);

        tabAmp = root.findViewById(R.id.tab_amp);
        tabEffects = root.findViewById(R.id.tab_effects);
        tabPatch = root.findViewById(R.id.tab_live);

        tabAmp.setOnClickListener(v -> {
            selectAmp();
            listener.onAmpSelected();
        });

        tabEffects.setOnClickListener(v -> {
            selectEffects();
            listener.onEffectsSelected();
        });

        tabPatch.setOnClickListener(v -> {
            selectPatch();
            listener.onPatchSelected();
        });
    }

    // ================= STATUS =================
    public void setConnected(boolean connected) {

        if (connected) {

            statusChip.setText("Connected");

            statusChip.setTextColor(
                    Color.WHITE
            );

            statusChip.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#FF7A00")
                    )
            );

        } else {

            statusChip.setText("Disconnected");

            statusChip.setTextColor(
                    Color.parseColor("#AAAAAA")
            );

            statusChip.setBackgroundTintList(
                    ColorStateList.valueOf(
                            Color.parseColor("#1F1F1F")
                    )
            );
        }
    }

    // ================= TAB STATES =================
    public void selectAmp() {
        setSelected(tabAmp);
        setUnselected(tabEffects);
        setUnselected(tabPatch);
    }

    public void selectEffects() {
        setSelected(tabEffects);
        setUnselected(tabAmp);
        setUnselected(tabPatch);
    }

    public void selectPatch() {
        setSelected(tabPatch);
        setUnselected(tabAmp);
        setUnselected(tabEffects);
    }

    private void setSelected(TextView t) {
        t.setTextColor(root.getContext().getColor(R.color.white));
        t.setBackgroundResource(R.drawable.nav_selected_bg);
    }

    private void setUnselected(TextView t) {
        t.setTextColor(root.getContext().getColor(R.color.gray));
        t.setBackgroundColor(
                root.getContext().getColor(android.R.color.transparent)
        );
    }
}