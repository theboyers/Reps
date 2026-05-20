# Copilot Instructions for Reps

Reps is a no-frills workout runner for people who already know what they want to do and just need something to keep them moving through it.

Read this file completely before suggesting or writing any code. These rules are not guidelines — they are constraints. Do not work around them.

---

## The One Thing This App Does

Reps does exactly one thing: it takes a workout routine the user has defined, and it runs it.

That's it. Build the routine. Run the routine. Done.

Every feature request, every code change, every UI addition must be evaluated against that single sentence. If it doesn't directly serve *building* or *running* a routine, it does not belong in this app.

---

## What Reps Is

- A tool, not a coach
- A runner, not a tracker
- Local-only, not connected
- Fast and focused, not comprehensive

The user comes to Reps knowing exactly what they want to do. Reps does not teach them. It does not motivate them. It does not judge them. It just runs their workout.

---

## Absolute Constraints — Never Violate These

These are not open to interpretation or "well, technically..." exceptions.

### No Network, Ever
- No `INTERNET` permission. Never add it.
- No HTTP clients, no REST calls, no WebSockets, no Firebase, no analytics SDKs.
- No "phone home" of any kind, even for crash reporting or diagnostics.
- No cloud sync. No cloud backup. No cloud anything.

### No Data Collection, Ever
- No analytics (no Firebase Analytics, no Mixpanel, no Amplitude, nothing).
- No crash reporting (no Crashlytics, no Sentry, no Bugsnag, nothing).
- No logging that persists or could be exfiltrated.
- No A/B testing frameworks.

### No Accounts, Ever
- No login, no sign-up, no guest mode that implies a "real" mode exists.
- No OAuth, no social login, no "continue with Google/Apple/Facebook".

### No Permissions Beyond What Currently Exists
- The app currently requires **zero** Android permissions. Keep it that way.
- Do not add `VIBRATE`, `INTERNET`, `RECEIVE_BOOT_COMPLETED`, `POST_NOTIFICATIONS`, or any other permission without an extremely compelling reason that is explicitly approved.
- HapticFeedback via the Compose API is fine — it does not require the `VIBRATE` permission.

### No Monetization Infrastructure
- No ads (no AdMob, no AdSense, no banner/interstitial/rewarded ads of any kind).
- No in-app purchases, no subscriptions, no "premium" tiers, no paywalls.
- No "rate this app" prompts that are tied to engagement metrics.

### No Social Features
- No sharing workouts to social media.
- No leaderboards, no challenges, no following other users.
- No community features of any kind.

### No Gamification
- No streaks. No badges. No points. No XP. No levels.
- No "you're on a roll!" messaging. No milestone celebrations.
- No progress graphs or charts. No personal records detected or displayed.

### No Hand-Holding
- No suggested workouts. Users define their own.
- No form tips, technique cues, or exercise descriptions.
- No "this looks like a lot of volume, are you sure?" warnings.
- No motivational text. No "great job!" No "you crushed it!" No emoji celebrations on the completion screen.
- The completion screen shows facts: elapsed time, what was done. Nothing more.

### No Third-Party SDKs Unless Absolutely Necessary
Current approved dependencies:
- `androidx.*` — standard Jetpack libs
- `com.google.code.gson:gson` — local serialization only
- `sh.calvin.reorderable:reorderable` — drag-to-reorder UI

Before adding any new dependency, ask: can this be done with what's already here? If yes, do that instead.

---

## Architecture Rules

### Keep it flat
All app logic lives in `RoutineTrackerScreen.kt`. There is no ViewModel, no repository layer, no use-case layer, no dependency injection container. Do not introduce any of these. The app is small enough that a single-file architecture is correct and intentional.

### State storage
- `SharedPreferences` for user preferences and the routine library (serialized via Gson).
- `rememberSaveable` / `remember` for in-memory UI state.
- Do not introduce Room, SQLite, DataStore, or any other persistence layer.

### No new files without good reason
If a new function or composable can live in `RoutineTrackerScreen.kt`, it belongs there. New files should only be created for genuinely distinct concerns (e.g. a new theme file, a new data class file if the existing one grows unwieldy).

---

## UI & UX Rules

### Material 3, nothing else
Use `MaterialTheme`, `MaterialTheme.colorScheme`, and `MaterialTheme.typography` throughout. Do not hardcode colors or font sizes. Dynamic color is intentionally disabled — the app uses a fixed accent palette.

### No new screens
Reps has four states: EDITING, GRACE, RUNNING, COMPLETE. These map to sections of a single `Scaffold`. Do not add new `Activity` classes or `NavHost` navigation graphs. New UI surfaces should be `ModalBottomSheet` or `AlertDialog`.

### Controls must be reachable one-handed
Primary action buttons (Done, Skip, Pause) are at the bottom of the screen. Keep them there.

### Text must survive large font sizes
Always specify `maxLines` and `overflow = TextOverflow.Ellipsis` on any `Text` that could overflow at system font scale ≥ 2×. The timer countdown display is an exception — let it scale.

### Accessibility is not optional
- Every interactive `Icon` must have a meaningful `contentDescription` or be nested inside a labeled control.
- Timer countdowns must carry `Modifier.semantics { liveRegion = LiveRegionMode.Polite }` so TalkBack users hear the countdown.
- New exercise cards and list items must have a `semantics { contentDescription = "..." }` that describes their content.

### Haptic feedback
Use `LocalHapticFeedback` + `HapticFeedbackType.LongPress` for confirmatory actions (Done, Skip, drag-to-reorder). Do not use `Vibrator` service (requires permission).

---

## Tone and Copy Rules

The app's voice is direct, functional, and silent when there's nothing useful to say.

| Situation | Correct | Wrong |
|---|---|---|
| Workout complete | "Done." + elapsed time + exercise list | "Workout complete! Great job! 🎉" |
| Empty state | "No exercises yet" | "Your journey starts here!" |
| Save confirmation | "Routine saved to library." | "Awesome! Your routine is ready 💪" |
| Error message | "Sets must be a positive number." | "Oops! Something doesn't look right." |
| Button labels | "Start Workout", "Skip Rest", "Done" | "Let's Go!", "I need a break", "Nailed it!" |

The app never speaks in first person on behalf of the user ("Your journey..."). It never uses exclamation points except where truly needed for clarity. It never uses workout-culture slang.

---

## What Good Feature Requests Look Like

A feature belongs in Reps if it:
1. Makes building a routine faster or more accurate
2. Makes running a workout more reliable or less distracting
3. Makes the app more accessible to more people
4. Reduces friction without adding complexity

A feature does **not** belong in Reps if it:
- Requires an internet connection
- Requires a new Android permission
- Tracks or stores anything beyond the routine definition itself
- Motivates, coaches, or evaluates the user
- Adds UI that the user must interact with during the workout (beyond Pause, Done, Skip)
- Could reasonably belong in a different, more specialized app

---

## Before You Write Any Code, Ask Yourself

1. **Does this require internet access or a permission?** → Stop.
2. **Does this collect, store, or transmit any user behavior data?** → Stop.
3. **Does this praise, judge, motivate, or coach the user?** → Stop.
4. **Does this belong in a fitness tracking app, not a workout runner?** → Stop.
5. **Can this be done with the existing dependencies?** → If yes, use them.
6. **Does this make the workout harder to run, not easier?** → Stop.

If you pass all six, proceed.

---

## Project Summary (for context)

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Persistence | SharedPreferences (Gson serialization) |
| Drag-to-reorder | `sh.calvin.reorderable:reorderable` |
| Min SDK | 26 (Android 8.0) |
| Internet permission | None |
| Other permissions | None |
| Entry point | `MainActivity.kt` |
| All logic | `RoutineTrackerScreen.kt` |
| Theme | `ui/theme/` |

Exercise types: `STRENGTH` (sets × reps), `TIMED` (sets × duration), `CARDIO` (single duration).  
Workout phases: `EDITING` → `GRACE` → `RUNNING` → `COMPLETE`.  
Per-side exercises split each set into a Left step followed by a Right step.  
Routines are saved as `List<SavedRoutine>` serialized to JSON in SharedPreferences under key `saved_routines`.
