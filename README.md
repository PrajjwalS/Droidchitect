
# Droidchitect

> Reverse Engineered, Low-latency Android controller and live-performance patch manager for Blackstar ID:Core V4 amplifiers.


<table>
<tr>

<td width="35%">

<img src="https://github.com/user-attachments/assets/bee351bc-81bc-4616-a875-3408c55de255" width="260"/>

</td>

<td width="65%">

Droidchitect is a fully native Android application for controlling Blackstar ID:Core V4 amplifiers over USB.

Built with a strong focus on:
- Fast live patch switching with Live Mode with Multiple Patch Slots. 
- Real-time amplifier synchronization (Hardware <-> UI)
- Mobile-first UX for musicians
- Portable patch import/export system
- Reverse engineered USB protocol support

### Performance Highlights

- ~254ms average patch convergence
- 99.1% successful convergence under 320ms
- Stress tested over 1000 patch transitions
- Interrupt-safe patch application architecture

</td>

</tr>
</table>

---

---

# Getting Started - How To Use This App

## Requirements

- Blackstar ID:Core V4 amplifier
- Android device with USB OTG support
- USB cable / OTG adapter

## Steps

1. Install The App on your android device
2. It is all Plug and Play (The app will automatically detect your amp when you plug in via USB port)
3. Jump To the Feature Section to get tips on how to use all sneaky little features.

Once connected, Droidchitect will begin synchronizing with the amplifier state automatically.
The app detects usb plugged in and out to check amp status connected/disconnected.

---

# Special Thanks
Extending thanks to the following repo owners/maintainers for making this project super easy.
1. https://codeberg.org/roderik333/architect-linux
2. https://github.com/ahmmedrejowan/RotaryKnob
   

# Features Overview

<table>
<tr>

<td width="55%">

## Real-Time Amp Control

Control nearly the complete amplifier state directly from Android.

### Amplifier Controls
- Voice Selection
- Gain
- Volume
- ISF
- Bass / Middle / Treble
- Presence
- Resonance

### Real-Time Synchronization
Droidchitect continuously synchronizes with the amplifier state:
- Hardware knob changes reflect in the UI
- UI changes reflect on the amplifier
- Dirty patch tracking updates automatically
- No manual refreshes required


</td>

<td width="25%">

<img alt="1_Amp_Page" src="https://github.com/user-attachments/assets/7daaf28b-2833-4a97-8e35-a3598bc42460" width="100%"/>


</td>

</tr>
</table>

---

<table>
<tr>

<td width="25%">

<img width="100%" alt="2_Effects_Page" src="https://github.com/user-attachments/assets/4cbafa7a-f884-434e-a851-cdb042496781" />
</td>

<td width="55%">

## Effects Control

Full control over amplifier effects with real-time updates.

### Supported Effects
- Modulation
- Delay
- Reverb
- Noise Gate

### Features
- Effect type switching
- Parameter editing
- Real-time hardware synchronization
- Responsive knob controls
- Hardware-authoritative state synchronization

Designed to feel fast and responsive during actual usage and live tweaking.

</td>

</tr>
</table>

---

<table>
<tr>

<td width="55%">



## Patch Library

Droidchitect includes a complete portable patch ecosystem.

### Patch Features
- Save patches locally
- Import patches
- Export patches
- ZIP export support
- Portable JSON patch format
- Batch import support
- Patch schema validation
- Corruption and sanity checks
- Tap on Any Patch Card or Live Patch Card to show full Patch Summary
- Download Some Patches from the sample patches folder and just export them!

### Patch Management
- Fast RecyclerView-based rendering
- Search and filtering
- Current patch dirty tracking
- Live patch assignment support

Malformed or unsupported patches are safely rejected during import.

</td>

<td width="25%">

<img width="100%" alt="3_Patch_Page" src="https://github.com/user-attachments/assets/d3a33683-e5fb-4ae5-a171-2d79ac9f825a" />
</td>

</tr>
</table>

---

<table>
<tr>

<td width="25%">

<img width="100%" alt="5_Live_Page_Live_Switch" src="https://github.com/user-attachments/assets/fca40c7c-4946-4cba-a7f9-ecd17690700e" />
</td>

<td width="55%">

## Go Live! Mode

One of the flagship features of Droidchitect.

Go Live! mode was built specifically for live playing/practicing workflows and acts as a mobile patch switcher alternative.

### Features
- Large button controls
- Multi-slot patch switching
- One-handed operation
- Rapid patch convergence
- Designed for live usage scenarios
- When GoLive! is Disabled You Can Configure your Slot Patches 
(Tap to Open Select List and Long Press To Clean Slot)

### Why It Exists
The official ecosystem does not provide a fast and mobile-first live switching workflow.

Droidchitect focuses heavily on reducing patch switching latency while maintaining synchronization reliability.

</td>

</tr>
</table>


---

# Performance & Benchmarks

A major focus of Droidchitect is achieving fast and reliable patch switching. Why? Why Not!

The patch application system was engineered with:
- Dedicated worker threading
- Interrupt-safe patch application
- Verification-based synchronization loops
- Race-condition resistant convergence logic
- Real-time amplifier state verification

The system ensures:
- The latest requested patch always wins
- Interrupted patch transitions terminate safely
- UI remains responsive during synchronization
- Hardware and UI state remain synchronized

---

# Threading & Synchronization Architecture

Patch application in Droidchitect is intentionally designed around interrupt-safe worker threading.

### Design Goals
- Prevent stale patch application
- Avoid race conditions during rapid switching
- Ensure newest requested patch always wins
- Keep UI responsive during synchronization
- Maintain reliable amplifier convergence

### Synchronization Strategy
The patch loader continuously:
- sends parameter updates
- verifies amplifier convergence
- interrupts stale synchronization loops
- aborts older patch requests safely

This allows aggressive rapid switching without UI freezes or invalid patch states.

---

## Patch Switching Benchmarks

Stress tested over **1000 patch transitions**.

```text
====================================
FINAL RESULTS
====================================

Total Iterations = 1000
Pass Threshold Duration = 320ms

Passes = 991
Failures = 9

Pass Percentage = 99.1%

Average Convergence = 254.09ms
Min Convergence = 232ms
Max Convergence = 1283ms

Command Delay = 5ms
Verify Interval = 15ms
Max Sync Loops = 20

====================================
```
## Thread Architecture Stress Testing

The synchronization system was additionally stress tested under:

Rapid repeated patch switching
Mid-transition interruption
Concurrent patch requests
UI-trigger spam conditions
Tested Behaviors
Correct thread interruption handling
Stale patch cancellation
Synchronization recovery
Final-state correctness verification

The patch loader was specifically engineered to prioritize:
Latest Requested Patch → Final Applied Patch (even during aggressive repeated switching scenarios.)

Checkout runPatchThreadTakeoverTest function from AmpTester.java 

---

# Contributing

Contributions are very welcome.

Droidchitect is still evolving and collaboration is highly appreciated.

Whether you are:
- a musician
- an Android developer
- a reverse engineering enthusiast
- a UI/UX contributor
- a Blackstar amplifier owner

feel free to contribute.
Please raise Issues as you seem them.

---

# 🐞 Known Issues

1. When connected with app if you change hardware knobs while on Patch Page, the diry patch marker "*"
   Doesn't show up until and unless you go to the Amp Page or Effects Page (whatever you change)
   This really isnt a priority bug.
<Will keep adding more>


