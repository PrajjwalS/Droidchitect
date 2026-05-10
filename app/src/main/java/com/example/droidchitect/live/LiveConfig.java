package com.example.droidchitect.live;

import java.util.ArrayList;
import java.util.List;

public class LiveConfig {

    public boolean goLiveEnabled = false;

    public int currentSlotIndex = -1;

    public List<LiveSlot> slots =
            new ArrayList<>();

    public LiveConfig() {

        for (int i = 0; i < 8; i++) {
            slots.add(new LiveSlot());
        }
    }
}