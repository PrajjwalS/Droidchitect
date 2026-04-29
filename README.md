# Blackstar ID:Core V4 Android Controller App — Design & Development Plan

---

# 1. Idea Description

We aim to build an **Android application** that can fully control a Blackstar ID:Core V4 amplifier over USB.

The app will:

* Replicate and extend functionality of the official Architect software
* Provide a **mobile-first, performance-oriented interface**
* Enable **real-time patch switching (like a footswitch)**
* Allow **patch creation, editing, storage, and sharing**

---

# 2. Core Objectives

* Replace the need for desktop Architect software
* Provide a **fast, reliable live-performance interface**
* Maintain compatibility with official patch formats
* Keep architecture clean and extensible

---

# 3. Protocol Source & References

The communication protocol will be derived from:

### Primary Source:

* https://codeberg.org/roderik333/architect-linux

This repository contains:

* Reverse-engineered USB communication
* Message formats
* Command structures

### Supporting Sources:

* USB sniffing (if needed):

  * `usbmon` (Linux)
  * Wireshark USB capture
* Observed behavior from official Architect app

---

# 4. High-Level Architecture

```text
Android UI
   ↓
ViewModel / Controller
   ↓
Protocol Layer (Java)
   ↓
USB Transport (Android USB API)
```

---

# 5. Feature Set

---

## 5.1 Amp Control (Edit Page)

### Knobs / Sliders

#### Preamp Section:

* Gain
* Volume
* ISF

#### EQ Section:

* Bass
* Mid
* Treble
* Presence
* Resonance

#### Effects:

* Modulation (type + level)
* Delay (level, time, feedback)
* Reverb (level, type)
* Noise Gate (on/off + threshold)

---

### Controls Behavior

* All controls implemented using **sliders (SeekBar)**
* Value range:

  * Likely 0–127 (to be confirmed via protocol)
* UI value mapped to protocol value

---

## 5.2 Patch System

### Patch Definition:

A patch = full snapshot of amp state

```java
class Patch {
    String name;
    AmpState state;
    long createdAt;
}
```

---

### Patch Operations:

* Save new patch
* Update existing patch
* Delete patch
* Rename patch

---

### Storage Strategy:

#### Primary:

* Local database (Room)

#### Secondary:

* JSON export/import

---

### Backup Options:

* Export JSON file
* Share via:

  * File system
  * Cloud (future)

---

## 5.3 Patch Library Page

Features:

* List all patches
* Search / filter
* Favorite patches
* Import / export

---

## 5.4 Live Performance Mode

---

### Patch Slots

* N total slots (e.g., 8)
* User selects M patches (M ≤ N)
* Each slot holds a patch

---

### Slot Behavior

* Tap slot → instantly apply patch
* Highlight active slot

---

### Big Action Button

Primary interaction:

```
NEXT PATCH
[TAP TO SWITCH]
```

Features:

* Full-width
* Bottom-positioned
* Thumb-friendly
* Instant response

---

### Patch Switching Logic

#### Option 1:

Sequential:

```
1 → 2 → 3 → 4 → loop
```

#### Option 2 (preferred):

Custom order:

```
User-defined sequence
```
---

# 6. UI Design Guidelines

### Theme:

* Primary: Black + Orange

### Design Principles:

* High contrast
* Large touch targets
* Minimal clutter
* Fast visual feedback

---

### UI Components:

* Sliders (SeekBar)
* Buttons
* Grid layouts (for slots)
* Highlight active states

---

# 7. Technical Details

---

## 7.1 USB Communication

Using Android:

* `UsbManager`
* `UsbDeviceConnection`
* `bulkTransfer()`

---

## 7.2 Protocol Layer

Responsibilities:

* Build command packets
* Parse incoming messages
* Maintain amp state

---

## 7.3 State Management

Single source of truth:

```text
AmpState
```

Rules:

* UI reflects AmpState
* USB updates modify AmpState
* No duplication

---

## 7.4 Performance Requirements

* Patch switch latency: **< 100 ms**
* No UI blocking
* Background USB read thread

---

