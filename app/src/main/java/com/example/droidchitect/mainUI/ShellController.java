package com.example.droidchitect.mainUI;

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

    private final TextView status;

    public ShellController(View root, NavigationListener listener) {

        this.root = root;
        this.listener = listener;

        Button connect = root.findViewById(R.id.btn_connect);
        status = root.findViewById(R.id.status);

        tabAmp = root.findViewById(R.id.tab_amp);
        tabEffects = root.findViewById(R.id.tab_effects);
        tabPatch = root.findViewById(R.id.tab_live);

        connect.setOnClickListener(v -> listener.onConnectClicked());

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
        status.setText(connected ? "Connected" : "Disconnected");
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