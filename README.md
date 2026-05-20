# Reps

An Android app for building and running custom workout routines. You define the work. It keeps you moving through it.

No streaks. No badges. No social feed. No "great job!" No suggested workouts. No form tips. Just your routine, running.

---

## What it does

**Build a routine**
- Add exercises as Strength (sets × reps), Timed (sets × duration), or Cardio (single duration)
- Set rest time per set and global rest between exercises
- Long-press any exercise card to drag it into the order you want
- Edit or delete exercises from the card's overflow menu

**Save and restore**
- Save a routine to your local library with a name, intensity label, and muscle group tags
- Load any saved routine back onto the builder in one tap
- Edit a saved routine in-place — changes are written back when you save
- Export your entire library to a JSON file; import it back on any device

**Run the workout**
- A configurable grace-period countdown gives you time to get into position before the first step
- A scrolling timeline shows every set and rest period in sequence — past, current, and upcoming
- Timed steps and rest periods count down automatically and advance on their own
- Rep-based steps wait for you to tap Done
- Per-side exercises (Left / Right) are tracked as separate steps in the sequence
- Pause at any point; a sound cue fires when any timer hits zero

---

## What it does not do

- Track your weight, body measurements, or progress over time
- Tell you how to perform an exercise
- Judge whether your routine is good or bad
- Require an account or any internet access
- Send notifications, collect analytics, or phone home

---

## Requirements

- Android 8.0 (API 26) or higher
- No permissions required

---

## Building

Open the project in Android Studio and run on a device or emulator, or build from the command line:

```powershell
# Debug APK
.\gradlew assembleDebug
# Output: app\build\outputs\apk\debug\Reps-debug.apk

# Release APK (requires a signing keystore — see Build > Generate Signed APK in Android Studio)
.\gradlew assembleRelease
# Output: app\build\outputs\apk\release\Reps-release.apk
```

---

## Project structure

```
app/src/main/java/net/theboyers/reps/
  MainActivity.kt               — entry point, theme bootstrap
  RoutineTrackerScreen.kt       — entire app UI and state
app/src/main/res/
  xml/backup_rules.xml          — Auto Backup scope (SharedPreferences only)
  xml/data_extraction_rules.xml
app/proguard-rules.pro          — keep rules for Gson data classes
```

All app state lives in `RoutineTrackerScreen.kt`. There is no database, no ViewModel, no repository layer — SharedPreferences holds the routine library as a Gson-serialised JSON string.

---

## Tech stack

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Persistence | SharedPreferences (Gson serialisation) |
| Drag-to-reorder | [reorderable](https://github.com/Calvin-LL/Reorderable) 2.4.0 |
| Minimum SDK | 26 (Android 8.0) |

---

## License

[GNU General Public License v3.0](LICENSE) — free to use, modify, and distribute; forks and derivatives must be released under the same license with attribution maintained.

---

## Google Play Store Listing

> Copy and paste the sections below directly into the Play Console.

---

### Short description
*(80 characters max)*

```
Build and run custom workout routines. No noise. Just your work.
```

---

### Full description
*(4000 characters max — paste as plain text in Play Console)*

```
Reps is a no-frills workout runner for people who already know what they want to do and just need something to keep them moving through it.

You build your routine. Reps runs it.

── BUILDING A ROUTINE ──

Add exercises as one of three types:
  • Strength — sets × reps (e.g. 4 sets of 8 squats)
  • Timed — sets × duration (e.g. 3 sets of 30-second planks)
  • Cardio — a single duration (e.g. 20-minute run)

Set a rest period between sets for each exercise, and a global rest between exercises. Long-press any exercise card to drag it into the order you want. Edit or delete exercises from the card's overflow menu at any time.

── RUNNING A WORKOUT ──

Tap Start. A configurable grace-period countdown gives you time to get into position before the first step begins.

A scrolling timeline shows every set and rest period in sequence — past, current, and upcoming — so you always know where you are and what's next.

  • Timed sets and all rest periods count down automatically and advance on their own.
  • Rep-based sets wait for you to tap Done when you're finished.
  • Per-side exercises (Left / Right) are tracked as separate steps.
  • A sound cue fires when any timer reaches zero.
  • Pause at any point and pick up exactly where you left off.

── SAVING AND RESTORING ──

Save any routine to your local library with a name, an intensity label, and muscle group tags. Load a saved routine back onto the builder in one tap. Edit a saved routine in-place — changes write back when you save again.

Export your entire library to a JSON file and import it back on any device.

── WHAT REPS DOES NOT DO ──

  • No account required. No internet access needed or used.
  • No ads, no analytics, no crash reporting, no data collection of any kind.
  • No progress tracking, no body measurements, no streaks, no badges.
  • No suggested workouts. No form tips. No "great job!"
  • Does not require any Android permissions.

If you want a fitness social network, a coaching app, or a progress journal, Reps is the wrong app. If you want something that runs your workout and stays out of the way, this is it.

── PRIVACY ──

Reps collects nothing. All data stays on your device. Full privacy policy: https://github.com/theboyers/Reps/blob/main/PRIVACY_POLICY.md

── OPEN SOURCE ──

Reps is open source under the GNU General Public License v3.0. Source code and full license at: https://github.com/theboyers/Reps
```
