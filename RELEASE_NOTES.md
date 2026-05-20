# Release Notes

---

## v1.1.0 — May 20, 2026

### UX & Accessibility

**Workout Summary Screen**
- Completion screen now shows a full checklist of every exercise you finished — name, sets, and reps/duration
- Elapsed workout time is shown at the top (e.g. "32 min 14 sec")
- Removed the "Great work!" message — Reps doesn't hand-hold

**Haptic Feedback**
- Tapping **Done** (rep-based sets) now fires a haptic confirmation
- Tapping **Skip / Skip Rest** does the same
- The last 3 seconds of any timed step or rest pulse once per second as a tactile countdown cue

**TalkBack / Screen Reader**
- The active countdown timer (work and rest) is now a live region — TalkBack announces remaining time as it counts down
- Exercise cards in the routine builder announce their position and name to screen readers

**Font Scaling**
- Exercise names in the active workout card, routine builder, and completion summary now gracefully truncate at very large system font sizes instead of overflowing

---

## v1.0.0 — May 20, 2026

Initial public release of **Reps** — a no-frills workout runner for Android.

### Features

**Build a Routine**
- Add exercises as **Strength** (sets × reps), **Timed** (sets × duration), or **Cardio** (single duration)
- Set rest time per set and a global rest between exercises
- Long-press any exercise card to drag it into order
- Edit or delete exercises from the card's overflow menu at any time

**Run a Workout**
- Configurable grace-period countdown before the first step begins
- Scrolling timeline shows every set and rest period — past, current, and upcoming
- Timed sets and rest periods count down and advance automatically
- Rep-based sets wait for you to tap Done
- Per-side exercises (Left / Right) tracked as separate steps
- Sound cue fires when any timer reaches zero
- Pause at any point and resume exactly where you left off

**Save and Restore**
- Save routines to a local library with a name, intensity label, and muscle group tags
- Load any saved routine back onto the builder in one tap
- Edit saved routines in-place
- Export your entire library to a JSON file; import it back on any device

### Privacy
No account. No internet. No ads. No analytics. No permissions required.  
All data stays on your device.

### Requirements
- Android 8.0 (API 26) or higher

### Install
Download `Reps-release.apk` below and sideload, or find Reps on the Google Play Store.

> **Note:** To sideload, you may need to enable *Install unknown apps* in your Android settings for your browser or file manager.

### License
Released under the [GNU General Public License v3.0](https://github.com/theboyers/Reps/blob/main/LICENSE).  
Source code: https://github.com/theboyers/Reps

