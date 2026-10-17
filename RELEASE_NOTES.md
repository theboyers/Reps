# Release Notes

---

## v1.3.0 — October 17, 2026

### Strength Training

**Weight**
- Strength and Timed exercises now accept an optional weight, entered in pounds or kilograms
- The weight is shown on each weighted step while the workout runs, and summarised in the routine builder, grace-period list, and completion screen
- A blank or zero weight is treated as none and is not displayed during the workout

**Warmup sets**
- Add one or more warmup sets to any strength exercise
- Each warmup can have its own reps and weight; leave them blank to reuse the working set's values
- Warmup steps run first and are clearly labelled **WARMUP** in the workout timeline

### Audio cues in the background

- A short tick now sounds for each of the final five seconds of a rest
- A distinct tone plays when a rest ends and the next step begins
- These cues keep firing with the screen off or while you're in another app, kept on time by a lightweight foreground service
- Battery stays low: the CPU is held awake only while cues are pending, and released while waiting on rep-based steps
- New setting **Play sounds with screen off** (on by default) turns the background service off for a foreground-only experience

### Mid-workout control

- The active card now carries **Mark Complete**, **Skip**, and **Cancel** actions, plus a **gear** for the exercise's set settings (sets, reps, weight) — and, when possible, "machine busy — do later"
- **Cancel** removes the exercise from the workout entirely; a confirmation dialog prevents accidental removals
- Tap **Do next** on any upcoming exercise to move it up
- Adjustments update the current routine; they reach your saved library only if you press Save afterward

### Summary & history

- The finish screen shows **duration**, **sets done**, **reps done**, and **total weight lifted** (kept per unit for lb and kg)
- Add a **note** about the workout and **save it to a history** you can browse from the top bar, notes included
- History is stored locally with the routine library; nothing is sent anywhere

### UX
- Rest between sets now defaults to **90 seconds** when adding a new exercise
- The Reps field accepts only a number or a range (e.g. `10` or `10-14`)
- Rep-based steps now label the count (e.g. **12 reps**) instead of showing a bare number
- The exercise form now scrolls, so it stays reachable as the form grows on smaller screens
- Muscle-group options are ordered head-to-toe and expanded (traps, lats, obliques, lower back, glutes, hip flexors, adductors/abductors, quads, hamstrings, calves, and more)
- The routine library can be **filtered by muscle group**; the Save and Library icons are now distinct

### Testing
- Added unit tests for weight validation/formatting and warmup step expansion
- Added instrumented UI tests covering weight entry (lb/kg), warmup entry, validation, editing, and display during a workout

---

## v1.2.0 — May 20, 2026

### Timer Improvements

**Count-up mode**
- Settings now includes a **Count up / Count down** toggle for all workout timers
- In count-up mode the timer shows elapsed time (e.g. `0:23 elapsed / 1:00`) so you always know both how far you've come and what the total duration is
- Grace period always counts down regardless of this setting

**Background accuracy**
- Timers are now wall-clock based rather than tick based
- Returning to the app after a brief interruption (silencing a notification, checking a message) will show the correct remaining time immediately
- Reps is designed to stay in the foreground; no background service is used and sound cues are not guaranteed when another app is active — use the Pause button if you need to step away

### Testing

- Added a comprehensive test suite: 68 unit tests (JVM) covering input parsing, step building, and timer formatting, plus 50 instrumented UI tests covering the full editing, workout, and library flows

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

