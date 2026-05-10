# Droidchitect Patch Schema V1

This document describes the JSON schema used for storing and sharing Droidchitect guitar amplifier patches.

Schema Version:

```json
"version": 1
```

---

# Example Patch

```json
{
  "about": "guitar vol knob reduce to control volume only ... it should be okay ..depends on speaker size.",
  "bass": 70,
  "creator": "Architect",
  "delayEnabled": true,
  "delayFeedback": 23,
  "delayLevel": 60,
  "delayTime": 576,
  "delayType": 1,
  "gain": 76,
  "isf": 73,
  "middle": 63,
  "modulationEnabled": false,
  "modulationParam1": 11,
  "modulationParam2": 51,
  "modulationParam3": 32,
  "modulationParam4": 23,
  "modulationType": 2,
  "name": "Creed - One Last Breath - dirty rhythm",
  "noiseGateAmount": 29,
  "noiseGateEnabled": false,
  "noiseGateSensitivity": 18,
  "presence": 49,
  "resonance": 127,
  "reverbEnabled": true,
  "reverbLevel": 37,
  "reverbSize": 23,
  "reverbType": 0,
  "tags": [],
  "treble": 63,
  "version": 1,
  "voice": 4,
  "volume": 44
}
```

---

# Field Reference

## Metadata

| Field | Type | Description |
|---|---|---|
| version | integer | Patch schema version |
| name | string | User-visible patch name |
| creator | string | Patch author |
| about | string | Notes/instructions for patch |
| tags | string[] | Optional tags |

---

# Amplifier

All amplifier values are integer ranges.

| Field | Range |
|---|---|
| voice | 0–5 |
| gain | 0–127 |
| volume | 0–127 |
| bass | 0–127 |
| middle | 0–127 |
| treble | 0–127 |
| isf | 0–127 |
| presence | 0–127 |
| resonance | 0–127 |

---

# Modulation

| Field | Type | Range |
|---|---|---|
| modulationEnabled | boolean | true/false |
| modulationType | integer | 0–3 |
| modulationParam1 | integer | 0–31 |
| modulationParam2 | integer | 0–127 |
| modulationParam3 | integer | 0–127 |
| modulationParam4 | integer | 0–127 |

## Modulation Parameter Mapping

The meaning of modulation parameters depends on modulation type.

General mapping:

| Blackstar XML | Droidchitect |
|---|---|
| Adjust1 | modulationParam1 |
| Adjust2 | modulationParam2 |
| Level | modulationParam3 |
| Rate | modulationParam4 |

---

# Delay

| Field | Type | Range |
|---|---|---|
| delayEnabled | boolean | true/false |
| delayType | integer | 0–3 |
| delayLevel | integer | 0–127 |
| delayFeedback | integer | 0–31 |
| delayTime | integer | 100–2000 |

## Delay Parameter Mapping

| Blackstar XML | Droidchitect |
|---|---|
| Level | delayLevel |
| Feedback | delayFeedback |
| Time | delayTime |

---

# Reverb

| Field | Type | Range |
|---|---|---|
| reverbEnabled | boolean | true/false |
| reverbType | integer | 0–3 |
| reverbLevel | integer | 0–127 |
| reverbSize | integer | 0–31 |

## Reverb Parameter Mapping

| Blackstar XML | Droidchitect |
|---|---|
| Level | reverbLevel |
| Size | reverbSize |

---

# Noise Gate

| Field | Type | Range |
|---|---|---|
| noiseGateEnabled | boolean | true/false |
| noiseGateSensitivity | integer | 0–127 |
| noiseGateAmount | integer | 0–127 |

---

# Validation Rules

A valid patch must:

- Contain all required fields
- Match the correct JSON data types
- Use supported schema version
- Stay within valid parameter ranges

Invalid or malformed patches are rejected during loading.

---

# Notes

- All parameter ranges are hardware-derived.
- Delay time minimum value is 100.
- Unknown future schema versions are currently rejected.
- Modulation parameter meanings vary by effect type.
