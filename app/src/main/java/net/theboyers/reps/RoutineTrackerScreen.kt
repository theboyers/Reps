package net.theboyers.reps

import androidx.core.content.ContextCompat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.LocalActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Switch
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.theboyers.reps.ui.theme.RepsTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.rememberModalBottomSheetState

data class RoutineEntry(
    val exerciseName: String,
    val details: String,
    val sets: String,
    val repsOrDuration: String,
    val restBetweenSetsSeconds: Int,
    val type: String = "STRENGTH",
    val isPerSide: Boolean = false,
    /** Canonical weight label for strength exercises (e.g. "135 lb"), or null when unspecified. */
    val weight: String? = null,
    /** Number of warmup sets performed before the working sets (0 = none). */
    val warmupSets: Int = 0,
    /** Reps for each warmup set; null/blank falls back to the working reps. */
    val warmupReps: String? = null,
    /** Canonical weight label for warmup sets; null/blank falls back to the working weight. */
    val warmupWeight: String? = null
)

/** A named workout routine stored in the user's library. */
data class SavedRoutine(
    val id: String? = null,
    val name: String? = null,
    val intensity: String? = null,
    val muscleGroup: String? = null,
    val entries: List<RoutineEntry>,
    val restBetweenExercisesSeconds: Int = 0,
    val savedAt: Long? = null
)

/** A completed workout, saved to the user's history. */
data class WorkoutSession(
    val id: String? = null,
    val completedAt: Long = 0L,
    val durationSeconds: Int = 0,
    val setsCompleted: Int = 0,
    val repsCompleted: Int = 0,
    val volumeLb: Double = 0.0,
    val volumeKg: Double = 0.0,
    val routineName: String? = null,
    val note: String? = null,
    val exerciseNames: List<String> = emptyList()
)

val intensityOptions  = listOf("Easy", "Moderate", "Hard", "Max")
// Ordered roughly head-to-toe, with whole-body and non-strength categories last.
val muscleGroupOptions = listOf(
    "Neck & Traps",
    "Shoulders",
    "Chest",
    "Back & Lats",
    "Triceps",
    "Biceps",
    "Forearms & Grip",
    "Core & Abs",
    "Obliques",
    "Lower Back",
    "Glutes",
    "Hip Flexors",
    "Adductors & Abductors",
    "Quads",
    "Hamstrings",
    "Calves & Shins",
    "Full Body",
    "Upper Body",
    "Lower Body",
    "Cardio / Endurance",
    "Stretching & Flexibility",
    "Other"
)

enum class WorkoutPhase {
    EDITING,
    GRACE,
    RUNNING,
    COMPLETE
}

enum class RunningMode {
    WORK,
    REST_BETWEEN_SETS,
    REST_BETWEEN_EXERCISES
}

enum class RestUnit(val label: String, val secondsMultiplier: Int) {
    SECONDS("Seconds", 1),
    MINUTES("Minutes", 60)
}

enum class WeightUnit(val label: String) {
    LB("lb"),
    KG("kg")
}

enum class ExerciseType { STRENGTH, TIMED, CARDIO }

/** Returns null if the input is valid, or a user-facing error message. */
fun validateRoutineInput(exerciseName: String, sets: String): String? {
    if (exerciseName.isBlank()) return "Exercise name is required."
    if (sets.isNotBlank() && sets.toIntOrNull() == null) return "Sets must be a number."
    if (sets.toIntOrNull() != null && sets.toInt() <= 0) return "Sets must be at least 1."
    return null
}

/** Converts an amount and unit pair to a total seconds value. Returns null if the amount is invalid. */
fun parseRestSeconds(amountText: String, unit: RestUnit): Int? {
    if (amountText.isBlank()) return 0

    val amount = amountText.toIntOrNull() ?: return null
    if (amount < 0) return null

    return amount * unit.secondsMultiplier
}

/** Returns null if the weight input is valid (including blank or zero), or a user-facing error message. */
fun validateWeight(amountText: String): String? {
    if (amountText.isBlank()) return null
    val amount = amountText.toDoubleOrNull() ?: return "Weight must be a number."
    if (amount < 0) return "Weight cannot be negative."
    return null
}

/**
 * Formats a weight amount and unit into a canonical label (e.g. "135 lb", "2.5 kg").
 * Returns null when the amount is blank or not a positive number.
 */
fun formatWeight(amountText: String, unit: WeightUnit): String? {
    val amount = amountText.trim().toDoubleOrNull() ?: return null
    if (amount <= 0) return null
    val display = if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString()
    return "$display ${unit.label}"
}

/** Extracts the numeric portion of a canonical weight label (e.g. "135 lb" -> "135"). Blank when absent. */
fun weightAmountOf(label: String?): String {
    if (label.isNullOrBlank()) return ""
    return label.trim().removeSuffix("lb").removeSuffix("kg").trim()
}

/** Keeps digits and at most a single decimal point from raw weight input. */
fun sanitizeWeightInput(raw: String): String {
    val filtered = raw.filter { it.isDigit() || it == '.' }
    val firstDot = filtered.indexOf('.')
    return if (firstDot == -1) filtered
    else filtered.substring(0, firstDot + 1) + filtered.substring(firstDot + 1).replace(".", "")
}

/** Resolves the unit of a canonical weight label, defaulting to pounds when absent or unrecognised. */
fun weightUnitOf(label: String?): WeightUnit =
    if (label?.trim()?.lowercase()?.endsWith("kg") == true) WeightUnit.KG else WeightUnit.LB

/**
 * Appends a reps unit to a bare rep count for display (e.g. "12" -> "12 reps").
 * Values that already carry text (e.g. "10/side", "45 sec", "AMRAP") are returned unchanged.
 */
fun formatRepsLabel(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isBlank()) return ""
    if (trimmed.any { it.isLetter() }) return trimmed
    return if (trimmed == "1") "1 rep" else "$trimmed reps"
}

/** Restricts rep input to a bare number or a numeric range (e.g. "10" or "10-14"). */
fun sanitizeRepsInput(raw: String): String {
    val cleaned = raw.filter { it.isDigit() || it == '-' }
    return (Regex("^\\d*(?:-\\d*)?").find(cleaned)?.value ?: "").trimStart('-')
}

/** Returns null if the reps input is a blank, a number, or a range; otherwise a user-facing error. */
fun validateReps(input: String): String? {
    val trimmed = input.trim()
    if (trimmed.isBlank()) return null
    return if (Regex("^\\d+(?:-\\d+)?$").matches(trimmed)) null
           else "Reps must be a number or a range (e.g. 10 or 10-14)."
}

/** Returns the duration in seconds if the input is time-based (e.g. "45 sec", "2 min"), or null for rep-based entries. */
fun parseWorkDurationSeconds(input: String): Int? {
    val s = input.trim().lowercase()
    if (s.isBlank()) return null
    val number = Regex("\\d+").find(s)?.value?.toIntOrNull() ?: return null
    return when {
        s.contains("min") -> number * 60
        s.contains("sec") || s.contains("s") && !s.contains("set") -> number
        else -> null
    }
}

/** Formats a duration in seconds into a human-readable string (e.g. "1 min", "1 min 30 sec"). Returns "None" for zero. */
fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return "None"

    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return when {
        minutes > 0 && remainingSeconds > 0 -> "$minutes min $remainingSeconds sec"
        minutes > 0 -> if (minutes == 1) "1 min" else "$minutes min"
        else -> "$seconds sec"
    }
}

/** Formats a seconds value as M:SS when over a minute, or plain seconds otherwise. */
fun formatCountdown(seconds: Int): String {
    if (seconds <= 0) return "0"
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "%d:%02d".format(m, s) else "$s"
}

// A workout step is either an active work set or a rest period between steps.

sealed class WorkoutStep {
    data class Work(
        val exerciseName: String,
        val details: String,
        val setNumber: Int,
        val totalSets: Int,
        val repsOrDurationLabel: String,
        val durationSeconds: Int,
        val side: String = "",
        val weight: String = "",
        val isWarmup: Boolean = false,
        val exerciseIndex: Int = 0
    ) : WorkoutStep()

    data class Rest(
        val durationSeconds: Int,
        val upNextName: String
    ) : WorkoutStep()
}

/** Expands routine entries into a flat, ordered sequence of work and rest steps. */
fun buildWorkoutSteps(
    entries: List<RoutineEntry>,
    restBetweenExercisesSeconds: Int
): List<WorkoutStep> {
    val steps = mutableListOf<WorkoutStep>()
    entries.forEachIndexed { exIdx, entry ->
        val setsPerSide = entry.sets.toIntOrNull() ?: 1
        val sides = if (entry.isPerSide) listOf("Left", "Right") else listOf("")
        val totalSets = setsPerSide * sides.size

        // Warmup sets (strength only) are emitted first, each followed by the
        // exercise's rest so the user is recovered before the working sets begin.
        val warmupCount = entry.warmupSets.coerceAtLeast(0)
        if (warmupCount > 0) {
            val warmupReps = entry.warmupReps?.takeIf { it.isNotBlank() } ?: entry.repsOrDuration
            val warmupWeight = entry.warmupWeight?.takeIf { it.isNotBlank() } ?: entry.weight.orEmpty()
            repeat(warmupCount) { wIdx ->
                steps.add(
                    WorkoutStep.Work(
                        exerciseName = entry.exerciseName,
                        details = entry.details,
                        setNumber = wIdx + 1,
                        totalSets = warmupCount,
                        repsOrDurationLabel = warmupReps,
                        durationSeconds = 0,
                        side = "",
                        weight = warmupWeight,
                        isWarmup = true,
                        exerciseIndex = exIdx
                    )
                )
                if (entry.restBetweenSetsSeconds > 0) {
                    steps.add(WorkoutStep.Rest(durationSeconds = entry.restBetweenSetsSeconds, upNextName = ""))
                }
            }
        }

        var stepNum = 0
        repeat(setsPerSide) { setIdx ->
            sides.forEachIndexed { sideIdx, side ->
                stepNum++
                steps.add(
                    WorkoutStep.Work(
                        exerciseName = entry.exerciseName,
                        details = entry.details,
                        setNumber = stepNum,
                        totalSets = totalSets,
                        repsOrDurationLabel = entry.repsOrDuration,
                        durationSeconds = parseWorkDurationSeconds(entry.repsOrDuration) ?: 0,
                        side = side,
                        weight = entry.weight.orEmpty(),
                        exerciseIndex = exIdx
                    )
                )
                val isLastStep = setIdx == setsPerSide - 1 && sideIdx == sides.lastIndex
                if (!isLastStep && entry.restBetweenSetsSeconds > 0) {
                    steps.add(WorkoutStep.Rest(durationSeconds = entry.restBetweenSetsSeconds, upNextName = ""))
                }
            }
        }
        if (exIdx < entries.lastIndex && restBetweenExercisesSeconds > 0) {
            steps.add(
                WorkoutStep.Rest(
                    durationSeconds = restBetweenExercisesSeconds,
                    upNextName = entries[exIdx + 1].exerciseName
                )
            )
        }
    }
    return steps
}

/** A timed audio cue scheduled at an absolute wall-clock time. */
enum class AudioCueType { TICK, REST_END, TIMER_END }

data class AudioCue(val atMillis: Long, val type: AudioCueType)

/**
 * Builds the timed audio-cue schedule for the workout from [fromIndex] onwards.
 *
 * [fromEndTimeMs] is the wall-clock time the current step ends (0 for a
 * rep-based step, which waits for the user). Walks forward through consecutive
 * timed steps until it reaches a rep-based step, whose end is unknown:
 *  - the final five seconds of every rest produce a [AudioCueType.TICK]
 *  - the end of every rest produces a [AudioCueType.REST_END] ("next up") tone
 *  - the end of a timed work step produces a [AudioCueType.TIMER_END] tone
 *
 * Cues already in the past (relative to [nowMs]) are dropped.
 */
fun buildAudioCueSchedule(
    steps: List<WorkoutStep>,
    fromIndex: Int,
    fromEndTimeMs: Long,
    nowMs: Long
): List<AudioCue> {
    if (fromEndTimeMs <= 0L || fromIndex !in steps.indices) return emptyList()

    val cues = mutableListOf<AudioCue>()

    fun addCuesFor(step: WorkoutStep, endMs: Long) {
        when (step) {
            is WorkoutStep.Rest -> {
                for (secsBefore in 5 downTo 1) {
                    val at = endMs - secsBefore * 1000L
                    if (at > nowMs) cues.add(AudioCue(at, AudioCueType.TICK))
                }
                if (endMs > nowMs) cues.add(AudioCue(endMs, AudioCueType.REST_END))
            }
            is WorkoutStep.Work -> {
                if (endMs > nowMs) cues.add(AudioCue(endMs, AudioCueType.TIMER_END))
            }
        }
    }

    var index = fromIndex
    var endMs = fromEndTimeMs
    addCuesFor(steps[index], endMs)

    index++
    while (index < steps.size) {
        val step = steps[index]
        val dur = when (step) {
            is WorkoutStep.Work -> step.durationSeconds
            is WorkoutStep.Rest -> step.durationSeconds
        }
        if (dur <= 0) break // rep-based step — wait for the user
        endMs += dur * 1000L
        addCuesFor(step, endMs)
        index++
    }

    return cues.sortedBy { it.atMillis }
}

/**
 * Returns the index (into the routine entries) of the exercise the workout is
 * currently on or about to start, based on the next work step at or after
 * [currentStepIndex] (falling back to the preceding one at the very end).
 */
fun currentExerciseIndex(steps: List<WorkoutStep>, currentStepIndex: Int): Int {
    if (currentStepIndex !in steps.indices) return -1
    for (i in currentStepIndex until steps.size) {
        val s = steps[i]
        if (s is WorkoutStep.Work) return s.exerciseIndex
    }
    for (i in currentStepIndex downTo 0) {
        val s = steps[i]
        if (s is WorkoutStep.Work) return s.exerciseIndex
    }
    return -1
}

/**
 * True when the workout is positioned before [exerciseIndex] has performed any
 * set — i.e. it can be deferred ("machine busy") without disturbing progress.
 */
fun canDeferExercise(steps: List<WorkoutStep>, currentStepIndex: Int, exerciseIndex: Int): Boolean {
    if (exerciseIndex < 0) return false
    return when (val step = steps.getOrNull(currentStepIndex)) {
        // First set (or first warmup) of the exercise, not yet performed.
        is WorkoutStep.Work -> step.exerciseIndex == exerciseIndex && step.setNumber == 1
        // A rest that leads into the next exercise.
        is WorkoutStep.Rest -> step.upNextName.isNotBlank()
        null -> false
    }
}

/** Returns [list] with the item at [from] moved to [to]. */
fun <T> moveWithin(list: List<T>, from: Int, to: Int): List<T> {
    if (from !in list.indices || to !in list.indices || from == to) return list
    val mutable = list.toMutableList()
    mutable.add(to, mutable.removeAt(from))
    return mutable
}

/**
 * Index of the first step belonging to [exerciseIndex], or -1 when that
 * exercise is no longer present — used to resume after cancelling an exercise.
 */
fun firstStepOfExercise(steps: List<WorkoutStep>, exerciseIndex: Int): Int =
    steps.indexOfFirst { it is WorkoutStep.Work && it.exerciseIndex == exerciseIndex }

/** Shifts every number in a rep value by [delta] (e.g. "10-14" + 1 -> "11-15"). */
fun shiftReps(input: String, delta: Int): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return if (delta > 0) "1" else ""
    var found = false
    val shifted = Regex("\\d+").replace(trimmed) { match ->
        found = true
        val value = (match.value.toIntOrNull() ?: 0) + delta
        value.coerceAtLeast(0).toString()
    }
    return if (found) shifted else trimmed
}

/** Adjusts a weight label by [delta] in [unit]; returns null when it drops to nothing. */
fun shiftWeight(label: String?, delta: Double, unit: WeightUnit): String? {
    val current = weightAmountOf(label).toDoubleOrNull() ?: 0.0
    val next = current + delta
    if (next <= 0) return null
    val amount = if (next % 1.0 == 0.0) next.toLong().toString() else next.toString()
    return formatWeight(amount, unit)
}

/** A representative rep count from a reps label (first number), or null when none. */
fun representativeReps(repsLabel: String): Int? =
    Regex("\\d+").find(repsLabel)?.value?.toIntOrNull()

/** Totals gathered from the work sets the user actually completed. */
data class WorkoutStats(
    val setsCompleted: Int = 0,
    val repsCompleted: Int = 0,
    val volumeLb: Double = 0.0,
    val volumeKg: Double = 0.0
)

/** Sums completed work sets into sets, reps, and volume (kept per unit). */
fun computeWorkoutStats(completed: List<WorkoutStep.Work>): WorkoutStats {
    var sets = 0
    var reps = 0
    var volumeLb = 0.0
    var volumeKg = 0.0
    completed.forEach { work ->
        sets++
        // Only rep-based sets contribute reps and volume; timed sets do not.
        if (work.durationSeconds == 0) {
            val repCount = representativeReps(work.repsOrDurationLabel) ?: 0
            reps += repCount
            val weight = weightAmountOf(work.weight).toDoubleOrNull() ?: 0.0
            if (weight > 0 && repCount > 0) {
                if (weightUnitOf(work.weight) == WeightUnit.KG) volumeKg += weight * repCount
                else volumeLb += weight * repCount
            }
        }
    }
    return WorkoutStats(sets, reps, volumeLb, volumeKg)
}

/** Formats a volume number with thousands separators and no unnecessary decimals. */
fun formatVolume(value: Double): String {
    val rounded = Math.round(value).toLong()
    return String.format(Locale.US, "%,d", rounded)
}

/** Main screen composable. Manages the full workout lifecycle: editing, grace period, active workout, and completion. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineTrackerScreen(onThemeChange: (String) -> Unit = {}) {
    var exerciseName by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }
    var sets by rememberSaveable { mutableStateOf("") }
    var repsOrDuration by rememberSaveable { mutableStateOf("") }
    var restBetweenSetsAmount by rememberSaveable { mutableStateOf("90") }
    var restBetweenSetsUnit by rememberSaveable { mutableStateOf(RestUnit.SECONDS) }
    var durationAmount by rememberSaveable { mutableStateOf("") }
    var durationUnit by rememberSaveable { mutableStateOf(RestUnit.MINUTES) }
    var exerciseType by rememberSaveable { mutableStateOf(ExerciseType.STRENGTH) }
    var isPerSide by rememberSaveable { mutableStateOf(false) }
    var weightAmount by rememberSaveable { mutableStateOf("") }
    var weightUnit by rememberSaveable { mutableStateOf(WeightUnit.LB) }
    var warmupEnabled by rememberSaveable { mutableStateOf(false) }
    var warmupSets by rememberSaveable { mutableStateOf("1") }
    var warmupReps by rememberSaveable { mutableStateOf("") }
    var warmupWeightAmount by rememberSaveable { mutableStateOf("") }
    var warmupWeightUnit by rememberSaveable { mutableStateOf(WeightUnit.LB) }
    var routineRestBetweenExercisesAmount by rememberSaveable { mutableStateOf("") }
    var routineRestBetweenExercisesUnit by rememberSaveable { mutableStateOf(RestUnit.SECONDS) }
    val routineRestBetweenExercisesSeconds = parseRestSeconds(routineRestBetweenExercisesAmount, routineRestBetweenExercisesUnit) ?: 0
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var exerciseMenuOpenIndex by remember { mutableStateOf<Int?>(null) }
    var exerciseDeleteConfirmIndex by remember { mutableStateOf<Int?>(null) }

    val routineEntries = remember { mutableStateListOf<RoutineEntry>() }
    // Stable keys parallel to routineEntries, used to drive drag-to-reorder animations.
    val entryIds = remember { mutableStateListOf<String>() }

    fun clearForm() {
        exerciseName = ""
        details = ""
        sets = ""
        repsOrDuration = ""
        restBetweenSetsAmount = "90"
        restBetweenSetsUnit = RestUnit.SECONDS
        durationAmount = ""
        durationUnit = RestUnit.MINUTES
        exerciseType = ExerciseType.STRENGTH
        isPerSide = false
        weightAmount = ""
        weightUnit = WeightUnit.LB
        warmupEnabled = false
        warmupSets = "1"
        warmupReps = ""
        warmupWeightAmount = ""
        warmupWeightUnit = WeightUnit.LB
        errorMessage = null
        editingIndex = null
    }

    fun loadEntryIntoForm(index: Int) {
        val e = routineEntries[index]
        exerciseName = e.exerciseName
        details = e.details
        sets = e.sets
        repsOrDuration = e.repsOrDuration
        isPerSide = e.isPerSide
        weightAmount = weightAmountOf(e.weight)
        weightUnit = weightUnitOf(e.weight)
        warmupEnabled = e.warmupSets > 0
        warmupSets = if (e.warmupSets > 0) e.warmupSets.toString() else "1"
        warmupReps = e.warmupReps.orEmpty()
        warmupWeightAmount = weightAmountOf(e.warmupWeight)
        warmupWeightUnit = weightUnitOf(e.warmupWeight)
        val sec = e.restBetweenSetsSeconds
        if (sec > 0 && sec % 60 == 0) {
            restBetweenSetsAmount = (sec / 60).toString()
            restBetweenSetsUnit = RestUnit.MINUTES
        } else {
            restBetweenSetsAmount = if (sec > 0) sec.toString() else ""
            restBetweenSetsUnit = RestUnit.SECONDS
        }
        if (parseWorkDurationSeconds(e.repsOrDuration) != null) {
            exerciseType = when (e.type) {
                "TIMED"  -> ExerciseType.TIMED
                "CARDIO" -> ExerciseType.CARDIO
                else     -> if ((e.sets.toIntOrNull() ?: 1) > 1) ExerciseType.TIMED else ExerciseType.CARDIO
            }
            val s = e.repsOrDuration.trim().lowercase()
            val number = Regex("\\d+").find(s)?.value ?: ""
            durationUnit = if (s.contains("min")) RestUnit.MINUTES else RestUnit.SECONDS
            durationAmount = number
        } else {
            exerciseType = ExerciseType.STRENGTH
            durationAmount = ""
            durationUnit = RestUnit.MINUTES
        }
        errorMessage = null
        editingIndex = index
    }

    var workoutPhase by rememberSaveable { mutableStateOf(WorkoutPhase.EDITING) }
    var isPaused by rememberSaveable { mutableStateOf(false) }
    var graceSecondsRemaining by rememberSaveable { mutableIntStateOf(10) }
    var currentStepIndex by rememberSaveable { mutableIntStateOf(0) }
    var planVersion by remember { mutableIntStateOf(0) }
    var adjustSheetExIdx by remember { mutableStateOf<Int?>(null) }
    var cancelExerciseConfirmIdx by remember { mutableStateOf<Int?>(null) }
    var timerSecondsRemaining by rememberSaveable { mutableIntStateOf(0) }
    var workoutStartTimeMs    by rememberSaveable { mutableStateOf(0L) }
    var workoutEndTimeMs      by rememberSaveable { mutableStateOf(0L) }
    // Wall-clock end times for timers — survives background correctly.
    var stepEndTimeMs   by rememberSaveable { mutableStateOf(0L) }
    var graceEndTimeMs  by rememberSaveable { mutableStateOf(0L) }
    var pausedAtMs      by rememberSaveable { mutableStateOf(0L) }

    // Derived workout step sequence, rebuilt whenever entries or rest settings change.
    val workoutSteps = buildWorkoutSteps(routineEntries, routineRestBetweenExercisesSeconds)
    val currentStep = workoutSteps.getOrNull(currentStepIndex)
    val currentWorkStep = currentStep as? WorkoutStep.Work
    val upcomingExerciseIndex = currentExerciseIndex(workoutSteps, currentStepIndex)
    val canDeferCurrentExercise =
        canDeferExercise(workoutSteps, currentStepIndex, upcomingExerciseIndex) &&
            upcomingExerciseIndex in 0 until routineEntries.lastIndex

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences("reps_prefs", android.content.Context.MODE_PRIVATE) }
    val gson = remember { Gson() }

    // Audio tone for timer completion (used when background audio is off).
    val toneGen = remember { android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 90) }

    DisposableEffect(Unit) {
        onDispose {
            toneGen.release()
            context.startService(WorkoutAudioService.stopIntent(context))
        }
    }

    // Tracks whether the foreground audio service is currently running so we
    // don't repeatedly start/stop it on every recomposition.
    var audioServiceStarted by remember { mutableStateOf(false) }


    var hasSavedRoutine by remember { mutableStateOf(false) }
    val savedRoutines = remember { mutableStateListOf<SavedRoutine>() }
    val workoutHistory = remember { mutableStateListOf<WorkoutSession>() }
    var showHistorySheet by remember { mutableStateOf(false) }
    val historySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var workoutNote by rememberSaveable { mutableStateOf("") }
    var historySaved by remember { mutableStateOf(false) }
    val completedWorkSteps = remember { mutableStateListOf<WorkoutStep.Work>() }
    var editingRoutine by remember { mutableStateOf<SavedRoutine?>(null) }
    var showSaveSheet   by remember { mutableStateOf(false) }
    var showLoadSheet   by remember { mutableStateOf(false) }
    var showExerciseSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }
    var clearAfterSave     by remember { mutableStateOf(false) }
    val saveSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val loadSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val exerciseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Persisted user preferences loaded from SharedPreferences.
    var soundEnabled                 by rememberSaveable { mutableStateOf(true) }
    var backgroundAudioEnabled       by rememberSaveable { mutableStateOf(true) }
    var gracePeriodPref              by rememberSaveable { mutableIntStateOf(10) }
    var keepScreenOn                 by rememberSaveable { mutableStateOf(true) }
    var themePreference              by rememberSaveable { mutableStateOf("system") }
    var defaultRestBetweenExSecs     by rememberSaveable { mutableIntStateOf(0) }
    var restBetweenExCardExpanded    by rememberSaveable { mutableStateOf(false) }
    var countUp                      by rememberSaveable { mutableStateOf(false) }

    fun doClearAll() {
        routineEntries.clear()
        entryIds.clear()
        clearForm()
        editingRoutine = null
        if (defaultRestBetweenExSecs > 0 && defaultRestBetweenExSecs % 60 == 0) {
            routineRestBetweenExercisesAmount = (defaultRestBetweenExSecs / 60).toString()
            routineRestBetweenExercisesUnit   = RestUnit.MINUTES
        } else if (defaultRestBetweenExSecs > 0) {
            routineRestBetweenExercisesAmount = defaultRestBetweenExSecs.toString()
            routineRestBetweenExercisesUnit   = RestUnit.SECONDS
        } else {
            routineRestBetweenExercisesAmount = ""
            routineRestBetweenExercisesUnit   = RestUnit.SECONDS
        }
    }

    // Restore preferences and the routine library on first composition.
    // Migrates any legacy single-routine data to the current multi-routine format.
    LaunchedEffect(Unit) {
        soundEnabled    = prefs.getBoolean("pref_sound_enabled", true)
        backgroundAudioEnabled = prefs.getBoolean("pref_background_audio", true)
        gracePeriodPref = prefs.getInt("pref_grace_period", 10)
        keepScreenOn    = prefs.getBoolean("pref_keep_screen_on", true)
        themePreference = prefs.getString("pref_theme", "system") ?: "system"
        countUp         = prefs.getBoolean("pref_count_up", false)
        val defaultRest = prefs.getInt("pref_default_rest_between_exercises", 0)
        defaultRestBetweenExSecs = defaultRest
        // Seed the current routine's rest field from the global default.
        if (defaultRest > 0 && routineRestBetweenExercisesAmount.isBlank()) {
            if (defaultRest % 60 == 0) {
                routineRestBetweenExercisesAmount = (defaultRest / 60).toString()
                routineRestBetweenExercisesUnit   = RestUnit.MINUTES
            } else {
                routineRestBetweenExercisesAmount = defaultRest.toString()
                routineRestBetweenExercisesUnit   = RestUnit.SECONDS
            }
        }
        val listType = object : TypeToken<List<SavedRoutine>>() {}.type
        val listJson = prefs.getString("saved_routines", null)
        if (listJson != null) {
            try {
                val list: List<SavedRoutine> = gson.fromJson(listJson, listType) ?: emptyList()
                savedRoutines.addAll(list)
            } catch (_: Exception) {}
        } else {
            // Migrate from the legacy single-routine storage key.
            val oldJson = prefs.getString("saved_routine", null)
            if (oldJson != null) {
                try {
                    val old = gson.fromJson(oldJson, SavedRoutine::class.java)
                    val migrated = old.copy(
                        id = UUID.randomUUID().toString(),
                        name = "My Routine",
                        savedAt = System.currentTimeMillis()
                    )
                    savedRoutines.add(migrated)
                    prefs.edit().putString("saved_routines", gson.toJson(savedRoutines.toList())).apply()
                } catch (_: Exception) {}
            }
        }
        hasSavedRoutine = savedRoutines.isNotEmpty()

        val historyJson = prefs.getString("workout_history", null)
        if (historyJson != null) {
            try {
                val historyType = object : TypeToken<List<WorkoutSession>>() {}.type
                val history: List<WorkoutSession> = gson.fromJson(historyJson, historyType) ?: emptyList()
                workoutHistory.addAll(history)
            } catch (_: Exception) {}
        }
    }

    fun persistLibrary() {
        prefs.edit().putString("saved_routines", gson.toJson(savedRoutines.toList())).apply()
        hasSavedRoutine = savedRoutines.isNotEmpty()
    }

    fun persistHistory() {
        prefs.edit().putString("workout_history", gson.toJson(workoutHistory.toList())).apply()
    }

    fun saveRoutineToLibrary(name: String, intensity: String, muscleGroup: String) {
        val existing = editingRoutine
        val routine = SavedRoutine(
            id = existing?.id ?: UUID.randomUUID().toString(),
            name = name.trim().ifBlank { "Untitled Routine" },
            intensity = intensity,
            muscleGroup = muscleGroup,
            entries = routineEntries.toList(),
            restBetweenExercisesSeconds = routineRestBetweenExercisesSeconds,
            savedAt = System.currentTimeMillis()
        )
        if (existing != null) {
            val idx = savedRoutines.indexOfFirst { it.id == existing.id }
            if (idx >= 0) savedRoutines[idx] = routine else savedRoutines.add(0, routine)
            editingRoutine = null
        } else {
            savedRoutines.add(0, routine)
        }
        persistLibrary()
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                if (existing != null) "\"${routine.name}\" updated!" else "Routine saved to library!"
            )
        }
    }

    fun saveWorkoutToHistory(stats: WorkoutStats, durationSeconds: Int) {
        val session = WorkoutSession(
            id = UUID.randomUUID().toString(),
            completedAt = System.currentTimeMillis(),
            durationSeconds = durationSeconds,
            setsCompleted = stats.setsCompleted,
            repsCompleted = stats.repsCompleted,
            volumeLb = stats.volumeLb,
            volumeKg = stats.volumeKg,
            routineName = editingRoutine?.name,
            note = workoutNote.trim().ifBlank { null },
            exerciseNames = routineEntries.map { it.exerciseName }
        )
        workoutHistory.add(0, session)
        persistHistory()
        historySaved = true
        coroutineScope.launch { snackbarHostState.showSnackbar("Workout saved to history!") }
    }

    fun openRoutineForEditing(routine: SavedRoutine) {
        routineEntries.clear()
        entryIds.clear()
        routineEntries.addAll(routine.entries)
        entryIds.addAll(routine.entries.map { UUID.randomUUID().toString() })
        val restSec = routine.restBetweenExercisesSeconds
        if (restSec > 0 && restSec % 60 == 0) {
            routineRestBetweenExercisesAmount = (restSec / 60).toString()
            routineRestBetweenExercisesUnit = RestUnit.MINUTES
        } else {
            routineRestBetweenExercisesAmount = if (restSec > 0) restSec.toString() else ""
            routineRestBetweenExercisesUnit = RestUnit.SECONDS
        }
        editingRoutine = routine
    }

    fun loadRoutineFromLibrary(routine: SavedRoutine) {
        routineEntries.clear()
        entryIds.clear()
        routineEntries.addAll(routine.entries)
        entryIds.addAll(routine.entries.map { UUID.randomUUID().toString() })
        val restSec = routine.restBetweenExercisesSeconds
        if (restSec > 0 && restSec % 60 == 0) {
            routineRestBetweenExercisesAmount = (restSec / 60).toString()
            routineRestBetweenExercisesUnit = RestUnit.MINUTES
        } else {
            routineRestBetweenExercisesAmount = if (restSec > 0) restSec.toString() else ""
            routineRestBetweenExercisesUnit = RestUnit.SECONDS
        }
        coroutineScope.launch { snackbarHostState.showSnackbar("\"${routine.name ?: "Routine"}\" loaded!") }
    }

    fun deleteRoutineFromLibrary(id: String) {
        savedRoutines.removeAll { it.id == id }
        persistLibrary()
    }

    /**
     * Advances to the next step and seeds its countdown timer. Marks the workout
     * complete if there are no more steps.
     *
     * [anchorMs] chains timed steps off the previous step's scheduled end so the
     * workout catches up correctly after the app has been backgrounded, instead
     * of drifting from "now". Manual skips pass null to start the next step now.
     */
    fun advanceStep(anchorMs: Long? = null, recordCompletion: Boolean = true) {
        if (recordCompletion) {
            (workoutSteps.getOrNull(currentStepIndex) as? WorkoutStep.Work)
                ?.let { completedWorkSteps.add(it) }
        }
        val nextIdx = currentStepIndex + 1
        if (nextIdx >= workoutSteps.size) {
            workoutEndTimeMs = System.currentTimeMillis()
            workoutPhase = WorkoutPhase.COMPLETE
        } else {
            currentStepIndex = nextIdx
            val dur = when (val s = workoutSteps[nextIdx]) {
                is WorkoutStep.Work -> s.durationSeconds
                is WorkoutStep.Rest -> s.durationSeconds
            }
            timerSecondsRemaining = dur
            val base = anchorMs ?: System.currentTimeMillis()
            stepEndTimeMs = if (dur > 0) base + dur * 1000L else 0L
        }
    }

    /**
     * Publishes the current timed-cue schedule to the foreground audio service
     * (or stops it) so ticks and tones fire on time with the screen off.
     */
    fun updateWorkoutAudio() {
        val delegate = soundEnabled && backgroundAudioEnabled
        if (!delegate || workoutPhase != WorkoutPhase.RUNNING || isPaused) {
            if (audioServiceStarted) {
                context.startService(WorkoutAudioService.stopIntent(context))
                audioServiceStarted = false
            }
            return
        }
        val cues = buildAudioCueSchedule(
            steps = workoutSteps,
            fromIndex = currentStepIndex,
            fromEndTimeMs = stepEndTimeMs,
            nowMs = System.currentTimeMillis()
        )
        if (cues.isEmpty()) {
            // Nothing timed pending (e.g. waiting on a rep-based step) — let the
            // device sleep and release the foreground service.
            if (audioServiceStarted) {
                context.startService(WorkoutAudioService.stopIntent(context))
                audioServiceStarted = false
            }
            return
        }
        try {
            ContextCompat.startForegroundService(
                context,
                WorkoutAudioService.scheduleIntent(
                    context,
                    cues.map { it.atMillis }.toLongArray(),
                    cues.map { it.type.ordinal }.toIntArray()
                )
            )
            audioServiceStarted = true
        } catch (_: Exception) {
            // Foreground start can be refused if the app is not visible; the
            // in-app tone generator still covers the foreground case.
            audioServiceStarted = false
        }
    }

    /** Seeds the countdown timer for the step at [index] in [steps]. */
    fun seedTimer(steps: List<WorkoutStep>, index: Int) {
        val dur = when (val s = steps.getOrNull(index)) {
            is WorkoutStep.Work -> s.durationSeconds
            is WorkoutStep.Rest -> s.durationSeconds
            null -> 0
        }
        timerSecondsRemaining = dur
        stepEndTimeMs = if (dur > 0) System.currentTimeMillis() + dur * 1000L else 0L
    }

    /** Re-seeds the current step's countdown after the plan changes underneath it. */
    fun seedCurrentStepTimer() = seedTimer(workoutSteps, currentStepIndex)

    /** Moves an exercise (and its parallel id) within the routine, keeping edit state sane. */
    fun moveEntry(from: Int, to: Int) {
        if (from !in routineEntries.indices || to !in routineEntries.indices || from == to) return
        val entry = routineEntries.removeAt(from)
        val id = entryIds.removeAt(from)
        routineEntries.add(to, entry)
        entryIds.add(to, id)
        editingIndex = editingIndex?.let { idx ->
            when {
                idx == from -> to
                from < to && idx in (from + 1)..to -> idx - 1
                from > to && idx in to until from -> idx + 1
                else -> idx
            }
        }
        planVersion++
    }

    /** Applies [transform] to the exercise currently in play. */
    fun updateCurrentExercise(transform: (RoutineEntry) -> RoutineEntry) {
        val idx = adjustSheetExIdx ?: upcomingExerciseIndex
        if (idx !in routineEntries.indices) return
        routineEntries[idx] = transform(routineEntries[idx])
        planVersion++
    }

    /** Defers the current exercise to the end of the routine ("machine busy — do later"). */
    fun doLaterCurrentExercise() {
        val idx = upcomingExerciseIndex
        val canDefer = canDeferExercise(workoutSteps, currentStepIndex, idx)
        if (!canDefer || idx >= routineEntries.lastIndex) return
        moveEntry(idx, routineEntries.lastIndex)
        // The step at the current index may now belong to a different exercise.
        seedCurrentStepTimer()
    }

    /** Moves an upcoming exercise to run immediately after the current one ("do this next"). */
    fun doThisNext(exerciseIdx: Int) {
        val current = upcomingExerciseIndex
        if (current < 0 || exerciseIdx !in routineEntries.indices) return
        if (exerciseIdx <= current) return
        moveEntry(exerciseIdx, current + 1)
    }

    /** Removes an exercise from the workout entirely, resuming at the next exercise. */
    fun cancelExercise(entryIndex: Int) {
        if (entryIndex !in routineEntries.indices) return
        routineEntries.removeAt(entryIndex)
        if (entryIndex < entryIds.size) entryIds.removeAt(entryIndex)
        editingIndex = editingIndex?.let { idx ->
            when {
                idx == entryIndex -> null
                idx > entryIndex -> idx - 1
                else -> idx
            }
        }
        planVersion++
        val newSteps = buildWorkoutSteps(routineEntries, routineRestBetweenExercisesSeconds)
        // After removal the following exercise takes entryIndex's slot; resume there.
        val resume = firstStepOfExercise(newSteps, entryIndex)
        if (resume < 0) {
            workoutEndTimeMs = System.currentTimeMillis()
            workoutPhase = WorkoutPhase.COMPLETE
        } else {
            currentStepIndex = resume
            seedTimer(newSteps, resume)
        }
    }

    /** Toggles pause/resume, keeping the wall-clock end times accurate. */
    fun handlePauseToggle() {
        val now = System.currentTimeMillis()
        if (!isPaused) {
            pausedAtMs = now
            isPaused = true
        } else {
            val pausedDuration = now - pausedAtMs
            if (graceEndTimeMs > 0L) graceEndTimeMs += pausedDuration
            if (stepEndTimeMs  > 0L) stepEndTimeMs  += pausedDuration
            pausedAtMs = 0L
            isPaused = false
        }
    }

    // Apply keep-screen-on window flag based on user preference.
    val activity = LocalActivity.current
    LaunchedEffect(keepScreenOn) {
        val flags = android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        if (keepScreenOn) activity?.window?.addFlags(flags)
        else activity?.window?.clearFlags(flags)
    }

    // File picker for exporting the routine library to JSON.
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(gson.toJson(savedRoutines.toList()).toByteArray())
                }
                coroutineScope.launch { snackbarHostState.showSnackbar("Routines exported!") }
            } catch (_: Exception) {
                coroutineScope.launch { snackbarHostState.showSnackbar("Export failed.") }
            }
        }
    }

    // File picker for importing a previously exported routine library.
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val json = context.contentResolver.openInputStream(uri)
                    ?.use { stream -> stream.bufferedReader().readText() }
                val type = object : TypeToken<List<SavedRoutine>>() {}.type
                val imported: List<SavedRoutine> = gson.fromJson(json, type) ?: emptyList()
                var addedCount = 0
                imported.forEach { routine ->
                    val finalId = routine.id ?: UUID.randomUUID().toString()
                    val withId = routine.copy(id = finalId)
                    if (savedRoutines.none { r -> r.id == finalId }) {
                        savedRoutines.add(0, withId)
                        addedCount++
                    }
                }
                if (addedCount > 0) persistLibrary()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        if (addedCount > 0) "Imported $addedCount routine${if (addedCount != 1) "s" else ""}!"
                        else "No new routines found (all duplicates)."
                    )
                }
            } catch (_: Exception) {
                coroutineScope.launch { snackbarHostState.showSnackbar("Import failed — is this a valid Reps export?") }
            }
        }
    }

    // Counts down the grace period before the first workout step begins.
    // Uses wall-clock time so the countdown continues correctly when the app is backgrounded.
    LaunchedEffect(workoutPhase, isPaused) {
        if (workoutPhase != WorkoutPhase.GRACE || isPaused) return@LaunchedEffect
        while (true) {
            val remaining = kotlin.math.ceil(
                (graceEndTimeMs - System.currentTimeMillis()) / 1000.0
            ).toInt().coerceAtLeast(0)
            graceSecondsRemaining = remaining
            if (remaining == 0) {
                workoutPhase = WorkoutPhase.RUNNING
                workoutStartTimeMs = System.currentTimeMillis()
                workoutEndTimeMs   = 0L
                currentStepIndex = 0
                val dur = when (val s = workoutSteps.getOrNull(0)) {
                    is WorkoutStep.Work -> s.durationSeconds
                    is WorkoutStep.Rest -> s.durationSeconds
                    null -> 0
                }
                timerSecondsRemaining = dur
                stepEndTimeMs = if (dur > 0) System.currentTimeMillis() + dur * 1000L else 0L
                break
            }
            delay(100)
        }
    }

    // Per-step countdown timer; auto-advances to the next step on expiry.
    // Uses wall-clock time so the timer continues correctly when the app is backgrounded.
    LaunchedEffect(workoutPhase, isPaused, currentStepIndex, planVersion) {
        if (workoutPhase != WorkoutPhase.RUNNING || isPaused || stepEndTimeMs == 0L) return@LaunchedEffect
        while (true) {
            val remaining = kotlin.math.ceil(
                (stepEndTimeMs - System.currentTimeMillis()) / 1000.0
            ).toInt().coerceAtLeast(0)
            if (remaining != timerSecondsRemaining) timerSecondsRemaining = remaining
            if (remaining == 0) {
                val step = workoutSteps.getOrNull(currentStepIndex)
                val shouldDing = step is WorkoutStep.Rest ||
                    (step is WorkoutStep.Work && step.durationSeconds > 0)
                // Only beep if this step ended roughly on time — avoids a burst of
                // dings when catching up after being backgrounded.
                val onTime = System.currentTimeMillis() - stepEndTimeMs < 1_500L
                // When background audio is enabled the service owns all timed cues.
                val playHere = soundEnabled && !backgroundAudioEnabled && onTime
                if (shouldDing && playHere) {
                    toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP2, 600)
                }
                advanceStep(anchorMs = stepEndTimeMs)
                break
            }
            delay(100)
        }
    }

    // Publishes the timed-cue schedule to the foreground audio service whenever
    // the running state changes, so ticks/tones fire with the screen off.
    LaunchedEffect(
        workoutPhase, currentStepIndex, isPaused, stepEndTimeMs,
        soundEnabled, backgroundAudioEnabled, planVersion
    ) {
        updateWorkoutAudio()
    }

    val hapticFeedback = LocalHapticFeedback.current

    // Haptic pulse for the last 3 seconds of any timed step or rest.
    LaunchedEffect(timerSecondsRemaining) {
        if (workoutPhase == WorkoutPhase.RUNNING && !isPaused && timerSecondsRemaining in 1..3) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Drag-to-reorder state for the exercise list in the editing phase.
    val editingListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(editingListState) { from, to ->
        val preCount = (if (editingRoutine != null) 1 else 0) + 1
        val fromIdx = from.index - preCount
        val toIdx   = to.index   - preCount
        if (fromIdx in routineEntries.indices && toIdx in routineEntries.indices) {
            routineEntries.add(toIdx, routineEntries.removeAt(fromIdx))
            entryIds.add(toIdx, entryIds.removeAt(fromIdx))
            val ei = editingIndex
            if (ei != null) {
                editingIndex = when {
                    ei == fromIdx -> toIdx
                    fromIdx < toIdx && ei in (fromIdx + 1)..toIdx -> ei - 1
                    fromIdx > toIdx && ei in toIdx until fromIdx -> ei + 1
                    else -> ei
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (workoutPhase == WorkoutPhase.EDITING) {
                ExtendedFloatingActionButton(
                    onClick = { clearForm(); showExerciseSheet = true },
                    modifier = Modifier.semantics { testTag = "fab_add_exercise" },
                    icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                    text = { Text("Add Exercise") }
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reps",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    if (workoutPhase == WorkoutPhase.EDITING) {
                        IconButton(
                            onClick = { showHistorySheet = true },
                            enabled = workoutHistory.isNotEmpty()
                        ) {
                            Icon(Icons.Rounded.History, contentDescription = "Workout history")
                        }
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Rounded.Settings, contentDescription = "Routine settings")
                        }
                        IconButton(
                            onClick = { showSaveSheet = true },
                            enabled = routineEntries.isNotEmpty()
                        ) {
                            Icon(Icons.Rounded.Save, contentDescription = "Save routine")
                        }
                        IconButton(
                            onClick = { showLoadSheet = true },
                            enabled = savedRoutines.isNotEmpty()
                        ) {
                            Icon(Icons.Rounded.LibraryBooks, contentDescription = "Load routine")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (workoutPhase) {
            WorkoutPhase.EDITING -> {
                LazyColumn(
                    state = editingListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .semantics { testTag = "screen_editing" },
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
                ) {
                    // Banner shown when an existing saved routine is being edited.
                    if (editingRoutine != null) {
                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Editing saved routine",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            editingRoutine!!.name ?: "Untitled",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    TextButton(
                                        onClick = { editingRoutine = null },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    ) { Text("Discard") }
                                }
                            }
                        }
                    }

                    // Routine header — title and exercise count.
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Your Routine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            if (routineEntries.isNotEmpty()) {
                                Text(
                                    "${routineEntries.size} exercise${if (routineEntries.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Empty state when no exercises have been added.
                    if (routineEntries.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.FitnessCenter,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                                Text(
                                    "No exercises yet",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.semantics { testTag = "empty_state_text" }
                                )
                                Text(
                                    "Tap + Add Exercise to get started",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }

                    // Exercise list with drag-to-reorder and per-item context menus.
                    itemsIndexed(
                        routineEntries,
                        key = { index, _ -> entryIds.getOrElse(index) { "$index" } }
                    ) { index, entry ->
                        val isBeingEdited = editingIndex == index
                        ReorderableItem(
                            reorderableLazyListState,
                            key = entryIds.getOrElse(index) { "$index" }
                        ) { isDragging ->
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics {
                                        contentDescription = "Exercise ${index + 1}: ${entry.exerciseName}"
                                    }
                                    .longPressDraggableHandle(
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = if (isDragging)
                                    CardDefaults.outlinedCardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                else if (isBeingEdited)
                                    CardDefaults.outlinedCardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                    )
                                else CardDefaults.outlinedCardColors(),
                                border = if (isDragging)
                                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                else if (isBeingEdited)
                                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
                                else CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 6.dp, top = 10.dp, bottom = 10.dp, end = 0.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // 2×3 dot grid used as a drag handle visual affordance.
                                    val dragHandleColor = if (isDragging)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    Canvas(modifier = Modifier.size(20.dp)) {
                                        val dotRadius = 2.2f * density
                                        val colGap = 5f * density
                                        val rowGap = 4.5f * density
                                        val left = (size.width - colGap) / 2f
                                        val top  = (size.height - rowGap * 2f) / 2f
                                        repeat(3) { row ->
                                            repeat(2) { col ->
                                                drawCircle(
                                                    color = dragHandleColor,
                                                    radius = dotRadius,
                                                    center = Offset(
                                                        x = left + col * colGap,
                                                        y = top  + row * rowGap
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    // Numbered position badge.
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isBeingEdited) MaterialTheme.colorScheme.secondary
                                                else MaterialTheme.colorScheme.primaryContainer
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${index + 1}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBeingEdited) MaterialTheme.colorScheme.onSecondary
                                                    else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                    // Exercise name, notes, and summary chips.
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Text(
                                            entry.exerciseName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (entry.details.isNotBlank()) {
                                            Text(
                                                entry.details,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        val meta = buildList {
                                            if (entry.sets.isNotBlank()) {
                                                val setsInt = entry.sets.toIntOrNull() ?: 1
                                                if (entry.isPerSide) {
                                                    add("${setsInt * 2} sets (${setsInt}/side)")
                                                } else {
                                                    add("${setsInt} set${if (setsInt != 1) "s" else ""}")
                                                }
                                            }
                                            if (entry.repsOrDuration.isNotBlank()) add(formatRepsLabel(entry.repsOrDuration))
                                            entry.weight?.takeIf { it.isNotBlank() }?.let { add(it) }
                                            if (entry.warmupSets > 0) add("${entry.warmupSets} warmup")
                                            if (entry.restBetweenSetsSeconds > 0) add("${formatDuration(entry.restBetweenSetsSeconds)} rest")
                                        }.joinToString(" · ")
                                        if (meta.isNotBlank()) {
                                            Text(
                                                meta,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    // Three-dot overflow menu.
                                    Box {
                                        IconButton(
                                            onClick = { exerciseMenuOpenIndex = index },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                Icons.Rounded.MoreVert,
                                                contentDescription = "More options",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = exerciseMenuOpenIndex == index,
                                            onDismissRequest = { exerciseMenuOpenIndex = null }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Edit") },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Rounded.Edit,
                                                        contentDescription = null
                                                    )
                                                },
                                                onClick = {
                                                    exerciseMenuOpenIndex = null
                                                    loadEntryIntoForm(index)
                                                    showExerciseSheet = true
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "Delete",
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Rounded.DeleteOutline,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                },
                                                onClick = {
                                                    exerciseMenuOpenIndex = null
                                                    exerciseDeleteConfirmIndex = index
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Collapsible card for configuring rest time between exercises.
                    if (routineEntries.size > 1) {
                        item {
                            val restSummary = if (routineRestBetweenExercisesSeconds > 0)
                                formatDuration(routineRestBetweenExercisesSeconds)
                            else "None"
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                     ) {
                                        // Tappable header that toggles the collapsed/expanded state.
                                        Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { restBetweenExCardExpanded = !restBetweenExCardExpanded },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Rest between exercises",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (!restBetweenExCardExpanded) {
                                                Text(
                                                    restSummary,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        Icon(
                                            if (restBetweenExCardExpanded) Icons.Rounded.KeyboardArrowUp
                                            else Icons.Rounded.KeyboardArrowDown,
                                            contentDescription = if (restBetweenExCardExpanded) "Collapse" else "Expand",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    if (restBetweenExCardExpanded) {
                                        RestDurationInput(
                                            modifier = Modifier.fillMaxWidth(),
                                            label = "Duration",
                                            amount = routineRestBetweenExercisesAmount,
                                            onAmountChange = { routineRestBetweenExercisesAmount = it.filter(Char::isDigit) },
                                            unit = routineRestBetweenExercisesUnit,
                                            onUnitChange = { routineRestBetweenExercisesUnit = it }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Start Workout and Clear All buttons.
                    if (routineEntries.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(4.dp))
                            Button(
                                    onClick = {
                                        workoutPhase = WorkoutPhase.GRACE
                                        isPaused = false
                                        graceSecondsRemaining = gracePeriodPref
                                        currentStepIndex = 0
                                        timerSecondsRemaining = 0
                                        graceEndTimeMs = System.currentTimeMillis() + gracePeriodPref * 1000L
                                        stepEndTimeMs = 0L
                                        pausedAtMs = 0L
                                        completedWorkSteps.clear()
                                        workoutNote = ""
                                        historySaved = false
                                    },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics { testTag = "btn_start_workout" },
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.size(8.dp))
                                Text(
                                    "Start Workout",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            TextButton(
                                onClick = { showClearAllDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Clear All",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            WorkoutPhase.GRACE -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .semantics { testTag = "screen_grace" },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isPaused) "Paused" else "Get ready!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = if (isPaused) "–" else "$graceSecondsRemaining",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isPaused) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { testTag = "grace_countdown" }
                    )
                    if (!isPaused) {
                        Text(
                            text = "seconds until start",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.weight(1f))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Today's workout",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    routineEntries.forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Column {
                                Text(
                                    entry.exerciseName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val subtitle = buildList {
                                    val isTimed = parseWorkDurationSeconds(entry.repsOrDuration) != null
                                    if (entry.sets.isNotBlank() && !(isTimed && entry.sets == "1")) add("${entry.sets} sets")
                                    if (entry.repsOrDuration.isNotBlank()) add(formatRepsLabel(entry.repsOrDuration))
                                    entry.weight?.takeIf { it.isNotBlank() }?.let { add(it) }
                                    if (entry.warmupSets > 0) add("${entry.warmupSets} warmup")
                                    if (entry.details.isNotBlank()) add(entry.details)
                                }.joinToString(" · ")
                                if (subtitle.isNotBlank()) {
                                    Text(
                                        subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { handlePauseToggle() },
                            modifier = Modifier
                                .weight(1f)
                                .semantics { testTag = "btn_pause_continue" },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isPaused) "Continue" else "Pause")
                        }
                        OutlinedButton(
                            onClick = { workoutPhase = WorkoutPhase.EDITING },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }

            WorkoutPhase.RUNNING -> {
                val listState = rememberLazyListState()
                LaunchedEffect(currentStepIndex) {
                    listState.animateScrollToItem(maxOf(0, currentStepIndex - 1))
                }
                val isRepBased = currentStep is WorkoutStep.Work &&
                    (currentStep as WorkoutStep.Work).durationSeconds == 0
                val isRestStep = currentStep is WorkoutStep.Rest
                val stepProgress = if (workoutSteps.isNotEmpty())
                    currentStepIndex.toFloat() / workoutSteps.size.toFloat() else 0f

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .semantics { testTag = "screen_running" }) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Progress bar and step label.
                        LinearProgressIndicator(
                            progress = { stepProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isRestStep) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isPaused) "PAUSED" else if (isRestStep) "REST" else "WORKING",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isPaused -> MaterialTheme.colorScheme.onSurfaceVariant
                                    isRestStep -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.semantics { testTag = "running_status_label" }
                            )
                            Text(
                                "Step ${currentStepIndex + 1} of ${workoutSteps.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Scrollable workout step timeline.
                        val exerciseStartIndices = buildSet {
                            val seen = mutableSetOf<Int>()
                            workoutSteps.forEachIndexed { i, s ->
                                if (s is WorkoutStep.Work && seen.add(s.exerciseIndex)) add(i)
                            }
                        }
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 140.dp
                            )
                        ) {
                            itemsIndexed(workoutSteps) { index, step ->
                                WorkoutTimelineItem(
                                    step = step,
                                    stepIndex = index,
                                    currentIndex = currentStepIndex,
                                    totalSteps = workoutSteps.size,
                                    timerSeconds = timerSecondsRemaining,
                                    isPaused = isPaused,
                                    countUp = countUp,
                                    onAdjust = {
                                        if (upcomingExerciseIndex in routineEntries.indices) {
                                            adjustSheetExIdx = upcomingExerciseIndex
                                        }
                                    },
                                    onMarkComplete = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        advanceStep()
                                    },
                                    onSkip = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        advanceStep(recordCompletion = false)
                                    },
                                    onCancelExercise = {
                                        if (upcomingExerciseIndex in routineEntries.indices) {
                                            cancelExerciseConfirmIdx = upcomingExerciseIndex
                                        }
                                    },
                                    showDoNext = step is WorkoutStep.Work &&
                                        index in exerciseStartIndices &&
                                        step.exerciseIndex != upcomingExerciseIndex,
                                    onDoNext = {
                                        (step as? WorkoutStep.Work)?.let { doThisNext(it.exerciseIndex) }
                                    }
                                )
                            }
                        }
                    }
                    // Bottom controls overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    0f to MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                    0.3f to MaterialTheme.colorScheme.background
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { handlePauseToggle() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { testTag = "btn_pause_continue" },
                            shape = RoundedCornerShape(12.dp)
                        ) { Text(if (isPaused) "Continue" else "Pause") }
                        TextButton(
                            onClick = { workoutPhase = WorkoutPhase.EDITING },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { testTag = "btn_end_workout" }
                        ) {
                            Text(
                                "End Workout",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            WorkoutPhase.COMPLETE -> {
                val elapsedSecs = if (workoutStartTimeMs > 0L && workoutEndTimeMs > workoutStartTimeMs)
                    ((workoutEndTimeMs - workoutStartTimeMs) / 1000L).toInt() else 0
                val stats = computeWorkoutStats(completedWorkSteps.toList())
                val volumeText = buildList {
                    if (stats.volumeLb > 0) add("${formatVolume(stats.volumeLb)} lb")
                    if (stats.volumeKg > 0) add("${formatVolume(stats.volumeKg)} kg")
                }.joinToString(" + ")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .semantics { testTag = "screen_complete" },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Done.",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.semantics { testTag = "complete_heading" }
                    )
                    Spacer(Modifier.height(6.dp))
                    if (elapsedSecs > 0) {
                        Text(
                            text = formatDuration(elapsedSecs),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            label = "Duration",
                            value = if (elapsedSecs > 0) formatDuration(elapsedSecs) else "—",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Sets done",
                            value = stats.setsCompleted.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            label = "Reps done",
                            value = if (stats.repsCompleted > 0) stats.repsCompleted.toString() else "—",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Weight lifted",
                            value = volumeText.ifBlank { "—" },
                            modifier = Modifier
                                .weight(1f)
                                .semantics { testTag = "stat_volume" }
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(
                        value = workoutNote,
                        onValueChange = { workoutNote = it },
                        label = { Text("Note (optional)") },
                        placeholder = { Text("How did it feel?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { testTag = "field_workout_note" },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { saveWorkoutToHistory(stats, elapsedSecs) },
                        enabled = !historySaved,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { testTag = "btn_save_history" },
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text(
                            if (historySaved) "Saved to history" else "Save to history",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))

                    routineEntries.forEachIndexed { _, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.exerciseName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val summary = buildList {
                                    val setsInt = entry.sets.toIntOrNull()
                                    if (setsInt != null && setsInt > 0) {
                                        if (entry.isPerSide) add("${setsInt * 2} sets (${setsInt}/side)")
                                        else add("$setsInt set${if (setsInt != 1) "s" else ""}")
                                    }
                                    if (entry.repsOrDuration.isNotBlank()) add(formatRepsLabel(entry.repsOrDuration))
                                }.joinToString(" × ").let { base ->
                                    buildList {
                                        if (base.isNotBlank()) add(base)
                                        entry.weight?.takeIf { it.isNotBlank() }?.let { add(it) }
                                        if (entry.warmupSets > 0) add("${entry.warmupSets} warmup")
                                    }.joinToString(" · ")
                                }
                                if (summary.isNotBlank()) {
                                    Text(
                                        summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { workoutPhase = WorkoutPhase.EDITING },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { testTag = "btn_back_to_routine" },
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            "Back to routine",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // ── Clear All confirmation dialog ─────────────────────────────────────────
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            icon = {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Clear routine?") },
            text = {
                Text(
                    "Your current routine has ${routineEntries.size} exercise${if (routineEntries.size != 1) "s" else ""}. " +
                    "Would you like to save it to the library before clearing?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearAllDialog = false
                        clearAfterSave = true
                        showSaveSheet = true
                    }
                ) {
                    Icon(Icons.Rounded.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("Save first")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            showClearAllDialog = false
                            doClearAll()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Clear anyway") }
                    OutlinedButton(onClick = { showClearAllDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // ── Delete exercise confirmation dialog ───────────────────────────────────
    if (exerciseDeleteConfirmIndex != null) {
        val idx = exerciseDeleteConfirmIndex!!
        val entryName = routineEntries.getOrNull(idx)?.exerciseName ?: "this exercise"
        AlertDialog(
            onDismissRequest = { exerciseDeleteConfirmIndex = null },
            icon = {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Remove exercise?") },
            text = { Text("\"$entryName\" will be removed from your routine.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingIndex == idx) {
                            clearForm()
                            coroutineScope.launch {
                                exerciseSheetState.hide()
                                showExerciseSheet = false
                            }
                        }
                        routineEntries.removeAt(idx)
                        if (idx < entryIds.size) entryIds.removeAt(idx)
                        exerciseDeleteConfirmIndex = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text("Remove") }
            },
            dismissButton = {
                OutlinedButton(onClick = { exerciseDeleteConfirmIndex = null }) { Text("Cancel") }
            }
        )
    }

    // ── Exercise form sheet ───────────────────────────────────────────────────
    if (showExerciseSheet) {
        val isEditing = editingIndex != null
        ModalBottomSheet(
            onDismissRequest = {
                clearForm()
                showExerciseSheet = false
            },
            sheetState = exerciseSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .semantics { testTag = "exercise_form_sheet" },
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (isEditing) Icons.Rounded.Edit else Icons.Rounded.FitnessCenter,
                        contentDescription = null,
                        tint = if (isEditing) MaterialTheme.colorScheme.secondary
                               else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        if (isEditing) "Editing exercise ${editingIndex!! + 1}"
                        else "Add an exercise",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Exercise type selector.
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ExerciseType.entries.forEachIndexed { i, type ->
                        SegmentedButton(
                            selected = exerciseType == type,
                            onClick = {
                                if (exerciseType != type) {
                                    exerciseType = type
                                    sets = ""
                                    repsOrDuration = ""
                                    durationAmount = ""
                                    durationUnit = RestUnit.MINUTES
                                    details = ""
                                    // Default rest between sets to 90s; cardio has no sets, so leave it blank.
                                    restBetweenSetsAmount = if (type == ExerciseType.CARDIO) "" else "90"
                                    isPerSide = false
                                    weightAmount = ""
                                    weightUnit = WeightUnit.LB
                                    warmupEnabled = false
                                    warmupSets = "1"
                                    warmupReps = ""
                                    warmupWeightAmount = ""
                                    warmupWeightUnit = WeightUnit.LB
                                    errorMessage = null
                                }
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = i,
                                count = ExerciseType.entries.size
                            )
                        ) {
                            Text(
                                when (type) {
                                    ExerciseType.STRENGTH -> "Strength"
                                    ExerciseType.TIMED    -> "Timed"
                                    ExerciseType.CARDIO   -> "Cardio"
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise") },
                    placeholder = {
                        Text(
                            when (exerciseType) {
                                ExerciseType.STRENGTH -> "e.g. Bodyweight Squats, Bench Press"
                                ExerciseType.TIMED    -> "e.g. Plank, Kettlebell Marches, Battle Ropes"
                                ExerciseType.CARDIO   -> "e.g. Treadmill, Bike, Rowing Machine"
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { testTag = "field_exercise_name" },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                when (exerciseType) {
                    ExerciseType.TIMED -> {
                        // Sets × Duration row with unit dropdown.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = sets,
                                onValueChange = { sets = it },
                                label = { Text(if (isPerSide) "Sets (per side)" else "Sets") },
                                placeholder = { Text("3") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Text(
                                "×",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = durationAmount,
                                onValueChange = { durationAmount = it.filter(Char::isDigit) },
                                label = { Text("Duration") },
                                placeholder = { Text("45") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            var timedUnitExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = timedUnitExpanded,
                                onExpandedChange = { timedUnitExpanded = !timedUnitExpanded },
                                modifier = Modifier.weight(1.4f)
                            ) {
                                OutlinedTextField(
                                    value = durationUnit.label,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Unit") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(timedUnitExpanded) },
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = timedUnitExpanded,
                                    onDismissRequest = { timedUnitExpanded = false }
                                ) {
                                    RestUnit.entries.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt.label) },
                                            onClick = { durationUnit = opt; timedUnitExpanded = false }
                                        )
                                    }
                                }
                            }
                        }
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("Notes (optional)") },
                            placeholder = { Text("e.g. per side, slow and controlled") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        // Per-side toggle for timed sets (e.g. single-leg holds).
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isPerSide = !isPerSide }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Switch(
                                checked = isPerSide,
                                onCheckedChange = { isPerSide = it },
                                modifier = Modifier.height(28.dp)
                            )
                            Column {
                                Text(
                                    "Each side (Left & Right)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (isPerSide) {
                                    val setsInt = sets.toIntOrNull() ?: 0
                                    Text(
                                        if (setsInt > 0) "${setsInt * 2} total sets · Left + Right per round"
                                        else "Workout will alternate Left and Right",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        "Enable for exercises like single-leg holds",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        WeightInput(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Weight (optional)",
                            amount = weightAmount,
                            onAmountChange = { weightAmount = it },
                            unit = weightUnit,
                            onUnitChange = { weightUnit = it },
                            amountTestTag = "field_weight"
                        )
                        RestDurationInput(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Rest between sets",
                            amount = restBetweenSetsAmount,
                            onAmountChange = { restBetweenSetsAmount = it.filter(Char::isDigit) },
                            unit = restBetweenSetsUnit,
                            onUnitChange = { restBetweenSetsUnit = it }
                        )
                    }
                    ExerciseType.CARDIO -> {
                        RestDurationInput(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Duration",
                            amount = durationAmount,
                            onAmountChange = { durationAmount = it.filter(Char::isDigit) },
                            unit = durationUnit,
                            onUnitChange = { durationUnit = it },
                            amountTestTag = "field_cardio_duration"
                        )
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("Settings (optional)") },
                            placeholder = { Text("e.g. 3 mph · incline 6, level 8") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        RestDurationInput(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Rest after",
                            amount = restBetweenSetsAmount,
                            onAmountChange = { restBetweenSetsAmount = it.filter(Char::isDigit) },
                            unit = restBetweenSetsUnit,
                            onUnitChange = { restBetweenSetsUnit = it }
                        )
                    }
                    ExerciseType.STRENGTH -> {
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text("Notes (optional)") },
                            placeholder = { Text("e.g. slow tempo, pause at bottom") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = sets,
                                onValueChange = { sets = it },
                                label = { Text(if (isPerSide) "Sets (per side)" else "Sets") },
                                placeholder = { Text("3") },
                                modifier = Modifier.weight(0.35f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = repsOrDuration,
                                onValueChange = { repsOrDuration = sanitizeRepsInput(it) },
                                label = { Text("Reps") },
                                placeholder = { Text("e.g. 10 or 10-14") },
                                modifier = Modifier
                                    .weight(0.65f)
                                    .semantics { testTag = "field_reps" },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        WeightInput(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Weight (optional)",
                            amount = weightAmount,
                            onAmountChange = { weightAmount = it },
                            unit = weightUnit,
                            onUnitChange = { weightUnit = it },
                            amountTestTag = "field_weight"
                        )
                        // Per-side toggle for strength exercises.
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isPerSide = !isPerSide }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Switch(
                                checked = isPerSide,
                                onCheckedChange = { isPerSide = it },
                                modifier = Modifier.height(28.dp)
                            )
                            Column {
                                Text(
                                    "Each side (Left & Right)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (isPerSide) {
                                    val setsInt = sets.toIntOrNull() ?: 0
                                    Text(
                                        if (setsInt > 0) "${setsInt * 2} total sets · Left + Right per round"
                                        else "Workout will alternate Left and Right",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        "Enable for exercises like lunges, single-arm rows",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        // Optional warmup sets performed before the working sets.
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { warmupEnabled = !warmupEnabled }
                                .padding(vertical = 4.dp)
                                .semantics { testTag = "field_warmup_toggle" },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Switch(
                                checked = warmupEnabled,
                                onCheckedChange = { warmupEnabled = it },
                                modifier = Modifier.height(28.dp)
                            )
                            Column {
                                Text(
                                    "Add warmup sets",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (warmupEnabled) {
                                    Text(
                                        "Lighter sets before your working sets",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        "Warm up before your working sets",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        if (warmupEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = warmupSets,
                                    onValueChange = { warmupSets = it.filter(Char::isDigit) },
                                    label = { Text("Warmup sets") },
                                    placeholder = { Text("1") },
                                    modifier = Modifier
                                        .weight(0.35f)
                                        .semantics { testTag = "field_warmup_sets" },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = warmupReps,
                                    onValueChange = { warmupReps = sanitizeRepsInput(it) },
                                    label = { Text("Warmup reps") },
                                    placeholder = { Text(repsOrDuration.ifBlank { "10" }) },
                                    modifier = Modifier
                                        .weight(0.65f)
                                        .semantics { testTag = "field_warmup_reps" },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            WeightInput(
                                modifier = Modifier.fillMaxWidth(),
                                label = "Warmup weight (optional)",
                                amount = warmupWeightAmount,
                                onAmountChange = { warmupWeightAmount = it },
                                unit = warmupWeightUnit,
                                onUnitChange = { warmupWeightUnit = it },
                                amountTestTag = "field_warmup_weight"
                            )
                        }
                        RestDurationInput(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Rest between sets",
                            amount = restBetweenSetsAmount,
                            onAmountChange = { restBetweenSetsAmount = it.filter(Char::isDigit) },
                            unit = restBetweenSetsUnit,
                            onUnitChange = { restBetweenSetsUnit = it },
                            amountTestTag = "field_strength_rest"
                        )
                    }
                }
                if (errorMessage != null) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                FilledTonalButton(
                    onClick = {
                        errorMessage = when (exerciseType) {
                            ExerciseType.STRENGTH -> validateRoutineInput(exerciseName, sets)
                                ?: validateReps(repsOrDuration)
                                ?: validateWeight(weightAmount)
                                ?: when {
                                    warmupEnabled && (warmupSets.toIntOrNull() == null || warmupSets.toInt() <= 0) ->
                                        "Warmup sets must be a positive number."
                                    warmupEnabled -> validateReps(warmupReps)?.let { "Warmup reps: $it" }
                                        ?: validateWeight(warmupWeightAmount)?.let { "Warmup weight: $it" }
                                    else -> null
                                }
                            ExerciseType.TIMED -> when {
                                exerciseName.isBlank() -> "Exercise name is required."
                                durationAmount.isBlank() -> "Duration is required."
                                durationAmount.toIntOrNull() == null || durationAmount.toInt() <= 0 -> "Duration must be a positive number."
                                sets.isNotBlank() && (sets.toIntOrNull() == null || sets.toInt() <= 0) -> "Sets must be a positive number."
                                else -> validateWeight(weightAmount)
                            }
                            ExerciseType.CARDIO -> when {
                                exerciseName.isBlank() -> "Exercise name is required."
                                durationAmount.isBlank() -> "Duration is required."
                                durationAmount.toIntOrNull() == null || durationAmount.toInt() <= 0 -> "Duration must be a positive number."
                                else -> null
                            }
                        }
                        val parsedSetRest = parseRestSeconds(restBetweenSetsAmount, restBetweenSetsUnit).also {
                            if (it == null) errorMessage = "Rest must be a valid number."
                        }
                        if (errorMessage == null) {
                            val isStrength = exerciseType == ExerciseType.STRENGTH
                            val supportsWeight = exerciseType != ExerciseType.CARDIO
                            val composedDuration = when (exerciseType) {
                                ExerciseType.TIMED, ExerciseType.CARDIO ->
                                    "$durationAmount ${if (durationUnit == RestUnit.MINUTES) "min" else "sec"}"
                                ExerciseType.STRENGTH -> repsOrDuration.trim()
                            }
                            val newEntry = RoutineEntry(
                                exerciseName = exerciseName.trim(),
                                details = details.trim(),
                                sets = when (exerciseType) {
                                    ExerciseType.TIMED   -> sets.trim().ifBlank { "1" }
                                    ExerciseType.CARDIO  -> "1"
                                    ExerciseType.STRENGTH -> sets.trim()
                                },
                                repsOrDuration = composedDuration,
                                restBetweenSetsSeconds = parsedSetRest ?: 0,
                                type = exerciseType.name,
                                isPerSide = when (exerciseType) {
                                    ExerciseType.CARDIO -> false
                                    else -> isPerSide
                                },
                                weight = if (supportsWeight) formatWeight(weightAmount, weightUnit) else null,
                                warmupSets = if (isStrength && warmupEnabled)
                                    (warmupSets.toIntOrNull() ?: 1).coerceAtLeast(1) else 0,
                                warmupReps = if (isStrength && warmupEnabled)
                                    warmupReps.trim().ifBlank { null } else null,
                                warmupWeight = if (isStrength && warmupEnabled)
                                    formatWeight(warmupWeightAmount, warmupWeightUnit) else null
                            )
                            val idx = editingIndex
                            if (idx != null) routineEntries[idx] = newEntry
                            else {
                                routineEntries.add(newEntry)
                                entryIds.add(UUID.randomUUID().toString())
                            }
                            clearForm()
                            coroutineScope.launch {
                                exerciseSheetState.hide()
                                showExerciseSheet = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { testTag = "btn_submit_exercise" },
                    shape = RoundedCornerShape(12.dp),
                    colors = if (isEditing)
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    else ButtonDefaults.filledTonalButtonColors()
                ) {
                    Icon(
                        if (isEditing) Icons.Rounded.Check else Icons.Rounded.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(if (isEditing) "Update Exercise" else "Add Exercise")
                }
                if (isEditing) {
                    TextButton(
                        onClick = {
                            clearForm()
                            coroutineScope.launch {
                                exerciseSheetState.hide()
                                showExerciseSheet = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.size(4.dp))
                        Text("Cancel edit")
                    }
                }
            }
        }
    }

    // ── Cancel exercise confirmation (mid-workout) ────────────────────────────
    cancelExerciseConfirmIdx?.let { idx ->
        val name = routineEntries.getOrNull(idx)?.exerciseName ?: "This exercise"
        AlertDialog(
            onDismissRequest = { cancelExerciseConfirmIdx = null },
            icon = {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Cancel exercise?") },
            text = { Text("\"$name\" will be removed from this workout.") },
            confirmButton = {
                Button(
                    onClick = {
                        cancelExerciseConfirmIdx = null
                        cancelExercise(idx)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.semantics { testTag = "btn_confirm_cancel_exercise" }
                ) { Text("Cancel exercise") }
            },
            dismissButton = {
                OutlinedButton(onClick = { cancelExerciseConfirmIdx = null }) { Text("Keep") }
            }
        )
    }

    // ── Mid-workout adjust sheet ──────────────────────────────────────────────
    val adjustIdx = adjustSheetExIdx
    if (adjustIdx != null && adjustIdx in routineEntries.indices) {
        val entry = routineEntries[adjustIdx]
        val isStrengthEntry = entry.type == "STRENGTH"
        val supportsWeightEntry = entry.type != "CARDIO"
        val entryUnit = weightUnitOf(entry.weight)
        val minSets = if (currentWorkStep?.exerciseIndex == adjustIdx) currentWorkStep.setNumber else 1
        ModalBottomSheet(
            onDismissRequest = { adjustSheetExIdx = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .semantics { testTag = "adjust_sheet" },
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Adjust ${entry.exerciseName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Changes apply to this set and the ones that follow.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StepperRow(
                    label = "Sets",
                    value = entry.sets.ifBlank { "1" },
                    decrementEnabled = (entry.sets.toIntOrNull() ?: 1) > minSets,
                    onDecrement = {
                        updateCurrentExercise { e ->
                            val n = (e.sets.toIntOrNull() ?: 1) - 1
                            e.copy(sets = n.coerceAtLeast(minSets).toString())
                        }
                    },
                    onIncrement = {
                        updateCurrentExercise { e ->
                            val n = (e.sets.toIntOrNull() ?: 1) + 1
                            e.copy(sets = n.coerceAtMost(50).toString())
                        }
                    },
                    testTagPrefix = "adjust_sets"
                )
                if (isStrengthEntry) {
                    StepperRow(
                        label = "Reps",
                        value = entry.repsOrDuration.ifBlank { "—" },
                        onDecrement = {
                            updateCurrentExercise { e -> e.copy(repsOrDuration = shiftReps(e.repsOrDuration, -1)) }
                        },
                        onIncrement = {
                            updateCurrentExercise { e -> e.copy(repsOrDuration = shiftReps(e.repsOrDuration, 1)) }
                        },
                        testTagPrefix = "adjust_reps"
                    )
                }
                if (supportsWeightEntry) {
                    val step = if (entryUnit == WeightUnit.KG) 2.5 else 5.0
                    StepperRow(
                        label = "Weight (${entryUnit.label})",
                        value = entry.weight ?: "—",
                        onDecrement = {
                            updateCurrentExercise { e -> e.copy(weight = shiftWeight(e.weight, -step, entryUnit)) }
                        },
                        onIncrement = {
                            updateCurrentExercise { e -> e.copy(weight = shiftWeight(e.weight, step, entryUnit)) }
                        },
                        testTagPrefix = "adjust_weight"
                    )
                }
                if (adjustIdx == upcomingExerciseIndex) {
                    HorizontalDivider()
                    Text(
                        "Reorder",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (canDeferCurrentExercise) {
                        TextButton(
                            onClick = {
                                adjustSheetExIdx = null
                                doLaterCurrentExercise()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { testTag = "btn_do_later" }
                        ) {
                            Icon(
                                Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.size(4.dp))
                            Text("Do this exercise later")
                        }
                    }
                    for (i in (upcomingExerciseIndex + 1) until routineEntries.size) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                routineEntries[i].exerciseName,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { doThisNext(i) },
                                modifier = Modifier.semantics { testTag = "btn_reorder_next" }
                            ) {
                                Text("Do next")
                            }
                        }
                    }
                }
                Button(
                    onClick = { adjustSheetExIdx = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { testTag = "adjust_done" },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Done") }
            }
        }
    }

    // ── Settings sheet ────────────────────────────────────────────────────────
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = settingsSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "App Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // ── Workout settings ──────────────────────────────────────────
                SettingsSectionHeader("Workout")
                Spacer(Modifier.height(4.dp))

                SettingRow(
                    icon = Icons.Rounded.Notifications,
                    title = "Sound effects",
                    subtitle = "Play a ding when a timer ends"
                ) {
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            prefs.edit().putBoolean("pref_sound_enabled", it).apply()
                        }
                    )
                }

                SettingRow(
                    icon = Icons.Rounded.PhoneAndroid,
                    title = "Play sounds with screen off",
                    subtitle = "Ticks and tones keep playing in the background during a workout"
                ) {
                    Switch(
                        checked = backgroundAudioEnabled,
                        onCheckedChange = {
                            backgroundAudioEnabled = it
                            prefs.edit().putBoolean("pref_background_audio", it).apply()
                        },
                        modifier = Modifier.semantics { testTag = "switch_background_audio" }
                    )
                }

                // Grace period selector.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Grace period", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Countdown seconds before workout starts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    val gracePeriodOptions = listOf(3, 5, 10, 15)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        gracePeriodOptions.forEachIndexed { i, secs ->
                            SegmentedButton(
                                selected = gracePeriodPref == secs,
                                onClick = {
                                    gracePeriodPref = secs
                                    prefs.edit().putInt("pref_grace_period", secs).apply()
                                },
                                shape = SegmentedButtonDefaults.itemShape(i, gracePeriodOptions.size)
                            ) { Text("${secs}s") }
                        }
                    }
                }

                SettingRow(
                    icon = Icons.Rounded.PhoneAndroid,
                    title = "Keep screen on",
                    subtitle = "Prevent phone sleep while app is open"
                ) {
                    Switch(
                        checked = keepScreenOn,
                        onCheckedChange = {
                            keepScreenOn = it
                            prefs.edit().putBoolean("pref_keep_screen_on", it).apply()
                        }
                    )
                }

                // Timer direction selector.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Timer direction", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Display timers counting down or up",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = !countUp,
                            onClick = {
                                countUp = false
                                prefs.edit().putBoolean("pref_count_up", false).apply()
                            },
                            shape = SegmentedButtonDefaults.itemShape(0, 2)
                        ) { Text("Count down") }
                        SegmentedButton(
                            selected = countUp,
                            onClick = {
                                countUp = true
                                prefs.edit().putBoolean("pref_count_up", true).apply()
                            },
                            shape = SegmentedButtonDefaults.itemShape(1, 2)
                        ) { Text("Count up") }
                    }
                }

                Spacer(Modifier.height(8.dp))
                // ── Routine defaults ──────────────────────────────────────────
                SettingsSectionHeader("Routine Defaults")
                Spacer(Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Restore,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Rest between exercises", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Default for new routines",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    val restOptions = listOf(0, 30, 60, 90, 120)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        restOptions.forEachIndexed { i, secs ->
                            SegmentedButton(
                                selected = defaultRestBetweenExSecs == secs,
                                onClick = {
                                    defaultRestBetweenExSecs = secs
                                    prefs.edit().putInt("pref_default_rest_between_exercises", secs).apply()
                                },
                                shape = SegmentedButtonDefaults.itemShape(i, restOptions.size)
                            ) {
                                Text(when (secs) {
                                    0    -> "None"
                                    60   -> "1 min"
                                    120  -> "2 min"
                                    else -> "${secs}s"
                                })
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                // ── Appearance ────────────────────────────────────────────────
                SettingsSectionHeader("Appearance")
                Spacer(Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (themePreference == "dark") Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Theme", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Override system dark/light mode",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    data class ThemeOption(val key: String, val label: String)
                    val themeOptions = listOf(
                        ThemeOption("system", "System"),
                        ThemeOption("light",  "Light"),
                        ThemeOption("dark",   "Dark")
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        themeOptions.forEachIndexed { i, opt ->
                            SegmentedButton(
                                selected = themePreference == opt.key,
                                onClick = {
                                    themePreference = opt.key
                                    prefs.edit().putString("pref_theme", opt.key).apply()
                                    onThemeChange(opt.key)
                                },
                                shape = SegmentedButtonDefaults.itemShape(i, themeOptions.size)
                            ) { Text(opt.label) }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                // ── Data management ───────────────────────────────────────────
                SettingsSectionHeader("Data")
                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val sdf = java.text.SimpleDateFormat("yyyyMMdd_HHmm", java.util.Locale.US)
                            exportLauncher.launch("reps_routines_${sdf.format(java.util.Date())}.json")
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = savedRoutines.isNotEmpty()
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Export")
                    }
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "text/*")) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("Import")
                    }
                }
                Text(
                    "Import merges routines from a previously exported Reps JSON file.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            settingsSheetState.hide()
                            showSettingsSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Done") }
            }
        }
    }

    // ── Save sheet ────────────────────────────────────────────────────────────
    if (showSaveSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSaveSheet = false },
            sheetState = saveSheetState
        ) {
            SaveRoutineSheet(
                prefilledName = editingRoutine?.name ?: "",
                prefilledIntensity = editingRoutine?.intensity ?: "Moderate",
                prefilledMuscleGroups = editingRoutine?.muscleGroup
                    ?.split(", ")?.filter { it.isNotBlank() }?.toSet()
                    ?: setOf("Full Body"),
                isUpdating = editingRoutine != null,
                onSave = { name, intensity, muscleGroup ->
                    saveRoutineToLibrary(name, intensity, muscleGroup)
                    if (clearAfterSave) {
                        doClearAll()
                        clearAfterSave = false
                    }
                    coroutineScope.launch {
                        saveSheetState.hide()
                        showSaveSheet = false
                    }
                },
                onDismiss = {
                    clearAfterSave = false   // user cancelled — don't clear
                    coroutineScope.launch {
                        saveSheetState.hide()
                        showSaveSheet = false
                    }
                }
            )
        }
    }

    // ── Load sheet ────────────────────────────────────────────────────────────
    if (showLoadSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLoadSheet = false },
            sheetState = loadSheetState
        ) {
            LoadRoutineSheet(
                routines = savedRoutines.toList(),
                onLoad = { routine ->
                    loadRoutineFromLibrary(routine)
                    coroutineScope.launch {
                        loadSheetState.hide()
                        showLoadSheet = false
                    }
                },
                onDelete = { id -> deleteRoutineFromLibrary(id) },
                onEdit = { routine ->
                    openRoutineForEditing(routine)
                    coroutineScope.launch {
                        loadSheetState.hide()
                        showLoadSheet = false
                    }
                }
            )
        }
    }

    // ── Workout history sheet ─────────────────────────────────────────────────
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            sheetState = historySheetState
        ) {
            WorkoutHistorySheet(sessions = workoutHistory.toList())
        }
    }
}

@Composable
private fun WorkoutHistorySheet(sessions: List<WorkoutSession>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Workout History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${sessions.size} workout${if (sessions.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (sessions.isEmpty()) {
            item {
                Text(
                    "No workouts saved yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 24.dp)
                )
            }
        } else {
            items(sessions, key = { it.id ?: it.hashCode().toString() }) { session ->
                WorkoutHistoryCard(session)
            }
        }
    }
}

@Composable
private fun WorkoutHistoryCard(session: WorkoutSession) {
    val dateStr = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
        .format(Date(if (session.completedAt > 0L) session.completedAt else System.currentTimeMillis()))
    val volume = buildList {
        if (session.volumeLb > 0) add("${formatVolume(session.volumeLb)} lb")
        if (session.volumeKg > 0) add("${formatVolume(session.volumeKg)} kg")
    }.joinToString(" + ")
    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        session.routineName?.takeIf { it.isNotBlank() } ?: "Workout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                buildList {
                    if (session.durationSeconds > 0) add(formatDuration(session.durationSeconds))
                    if (session.setsCompleted > 0) add("${session.setsCompleted} set${if (session.setsCompleted != 1) "s" else ""}")
                    if (session.repsCompleted > 0) add("${session.repsCompleted} reps")
                    if (volume.isNotBlank()) add(volume)
                }.joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (!session.note.isNullOrBlank()) {
                Text(
                    session.note,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { testTag = "history_note" }
                )
            }
        }
    }
}


// ── Routine Library Sheets ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SaveRoutineSheet(
    prefilledName: String = "",
    prefilledIntensity: String = "Moderate",
    prefilledMuscleGroups: Set<String> = setOf("Full Body"),
    isUpdating: Boolean = false,
    onSave: (name: String, intensity: String, muscleGroup: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(prefilledName) }
    var selectedIntensity by remember { mutableStateOf(prefilledIntensity) }
    var selectedMuscleGroups by remember { mutableStateOf(prefilledMuscleGroups) }
    var muscleGroupDialogOpen by remember { mutableStateOf(false) }

    // Multi-select dialog
    if (muscleGroupDialogOpen) {
        AlertDialog(
            onDismissRequest = { muscleGroupDialogOpen = false },
            title = { Text("Muscle Groups") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    muscleGroupOptions.forEach { group ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMuscleGroups = if (group in selectedMuscleGroups)
                                        selectedMuscleGroups - group
                                    else
                                        selectedMuscleGroups + group
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = group in selectedMuscleGroups,
                                onCheckedChange = { checked ->
                                    selectedMuscleGroups = if (checked)
                                        selectedMuscleGroups + group
                                    else
                                        selectedMuscleGroups - group
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(group, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { muscleGroupDialogOpen = false }) { Text("Done") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            if (isUpdating) "Update Routine" else "Save Routine",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Routine name") },
            placeholder = { Text("e.g. Morning Legs, Push Day") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Intensity",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                intensityOptions.forEachIndexed { i, opt ->
                    SegmentedButton(
                        selected = selectedIntensity == opt,
                        onClick = { selectedIntensity = opt },
                        shape = SegmentedButtonDefaults.itemShape(i, intensityOptions.size)
                    ) { Text(opt) }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Muscle Groups",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (selectedMuscleGroups.isEmpty()) "None selected"
                            else selectedMuscleGroups.joinToString(", "),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                // Invisible overlay that intercepts taps and opens the selection dialog.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { muscleGroupDialogOpen = true }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Cancel") }
            Button(
                onClick = { onSave(name, selectedIntensity, selectedMuscleGroups.joinToString(", ")) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) { Text(if (isUpdating) "Update" else "Save") }
        }
    }
}

@Composable
private fun LoadRoutineSheet(
    routines: List<SavedRoutine>,
    onLoad: (SavedRoutine) -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (SavedRoutine) -> Unit
) {
    var selectedFilters by remember { mutableStateOf(emptySet<String>()) }

    fun groupsOf(routine: SavedRoutine): List<String> =
        routine.muscleGroup?.split(", ")?.filter { it.isNotBlank() } ?: emptyList()

    // Only offer filters for muscle groups actually present in the library,
    // ordered head-to-toe (then any unknown legacy groups, alphabetically).
    val availableGroups = remember(routines) {
        val present = routines.flatMap { groupsOf(it) }.toSet()
        muscleGroupOptions.filter { it in present } +
            present.filter { it !in muscleGroupOptions }.sorted()
    }
    val filtered = if (selectedFilters.isEmpty()) routines
        else routines.filter { groupsOf(it).any { g -> g in selectedFilters } }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Routine Library",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    if (selectedFilters.isEmpty()) "${routines.size} saved"
                    else "${filtered.size} of ${routines.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (availableGroups.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filter by muscle group",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedFilters.isNotEmpty()) {
                            TextButton(
                                onClick = { selectedFilters = emptySet() },
                                modifier = Modifier.semantics { testTag = "btn_clear_filters" }
                            ) { Text("Clear") }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableGroups.forEach { group ->
                            FilterChip(
                                selected = group in selectedFilters,
                                onClick = {
                                    selectedFilters = if (group in selectedFilters)
                                        selectedFilters - group
                                    else selectedFilters + group
                                },
                                label = { Text(group) },
                                modifier = Modifier.semantics { testTag = "filter_$group" }
                            )
                        }
                    }
                }
            }
        }
        if (routines.isEmpty()) {
            item {
                Text(
                    "No saved routines yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 24.dp)
                )
            }
        } else if (filtered.isEmpty()) {
            item {
                Text(
                    "No saved routines match this filter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 24.dp)
                )
            }
        } else {
            items(filtered, key = { it.id ?: it.hashCode().toString() }) { routine ->
                RoutineLibraryCard(
                    routine = routine,
                    onLoad = { onLoad(routine) },
                    onDelete = { routine.id?.let { onDelete(it) } },
                    onEdit = { onEdit(routine) }
                )
            }
        }
    }
}

@Composable
private fun RoutineLibraryCard(
    routine: SavedRoutine,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dateStr = routine.savedAt?.let {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""

    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete routine?") },
            text = {
                Text(
                    "\"${routine.name ?: "Untitled Routine"}\" will be permanently deleted and cannot be recovered."
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        routine.name ?: "Untitled Routine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        buildString {
                            append("${routine.entries.size} exercise${if (routine.entries.size != 1) "s" else ""}")
                            if (dateStr.isNotBlank()) append(" · $dateStr")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete routine",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                routine.muscleGroup
                    ?.split(", ")
                    ?.filter { it.isNotBlank() }
                    ?.forEach { group ->
                        SuggestionChip(onClick = {}, label = { Text(group) })
                    }
                if (!routine.intensity.isNullOrBlank()) {
                    SuggestionChip(onClick = {}, label = { Text(routine.intensity) })
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    border = BorderStroke(
                        1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                Button(
                    onClick = onLoad,
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Load Routine") }
            }
        }
    }
}

@Composable
private fun WorkoutTimelineItem(
    step: WorkoutStep,
    stepIndex: Int,
    currentIndex: Int,
    totalSteps: Int,
    timerSeconds: Int,
    isPaused: Boolean,
    countUp: Boolean = false,
    onAdjust: () -> Unit = {},
    onMarkComplete: () -> Unit = {},
    onSkip: () -> Unit = {},
    onCancelExercise: () -> Unit = {},
    showDoNext: Boolean = false,
    onDoNext: () -> Unit = {}
) {
    val isPast    = stepIndex < currentIndex
    val isCurrent = stepIndex == currentIndex
    val distance  = (stepIndex - currentIndex).coerceAtLeast(0)
    val isRest    = step is WorkoutStep.Rest

    val itemAlpha = when {
        isPast    -> 0.38f
        isCurrent -> 1.0f
        distance == 1 -> 0.72f
        distance == 2 -> 0.55f
        else          -> 0.40f
    }

    val accentColor = if (isRest) MaterialTheme.colorScheme.tertiary
                      else MaterialTheme.colorScheme.primary
    val lineColor   = MaterialTheme.colorScheme.outlineVariant
    val bulletSize  = if (isCurrent) 22.dp else 14.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .alpha(itemAlpha)
    ) {
        // Step indicator column: vertical connector line + bullet.
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        ) {
            if (stepIndex > 0) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .align(Alignment.TopCenter)
                        .background(lineColor)
                )
            }
            if (stepIndex < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .align(Alignment.BottomCenter)
                        .background(lineColor)
                )
            }
            when {
                isPast -> Box(
                    modifier = Modifier
                        .size(bulletSize)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Check, null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(8.dp)
                    )
                }
                isCurrent -> Box(
                    modifier = Modifier
                        .size(bulletSize)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                else -> Box(
                    modifier = Modifier
                        .size(bulletSize)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.5.dp, lineColor, CircleShape)
                )
            }
        }

        // Step content: expanded card for the current step, compact label otherwise.
        if (isCurrent) {
            when (step) {
                is WorkoutStep.Work -> ElevatedCard(
                    modifier = Modifier.weight(1f).padding(start = 8.dp, top = 4.dp, bottom = 14.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (step.isWarmup) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    "WARMUP",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                                )
                            }
                        }
                        if (step.totalSets > 1) {
                            Text(
                                if (step.isWarmup) "Warmup ${step.setNumber} of ${step.totalSets}"
                                else "Set ${step.setNumber} of ${step.totalSets}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                step.exerciseName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { testTag = "current_exercise_name" }
                            )
                            IconButton(
                                onClick = onAdjust,
                                modifier = Modifier
                                    .size(36.dp)
                                    .semantics { testTag = "btn_adjust" }
                            ) {
                                Icon(
                                    Icons.Rounded.Settings,
                                    contentDescription = "Set settings",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        // Side badge
                        if (step.side.isNotBlank()) {
                            val sideColor = if (step.side == "Left")
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.tertiary
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = sideColor,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    "${step.side.uppercase()} SIDE",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (step.side == "Left")
                                        MaterialTheme.colorScheme.onSecondary
                                    else
                                        MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                                )
                            }
                        }
                        if (step.details.isNotBlank()) {
                            Text(
                                step.details,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        if (step.durationSeconds > 0) {
                            val displaySeconds = if (countUp) step.durationSeconds - timerSeconds else timerSeconds
                            val timerLabel = if (countUp) "elapsed / ${formatCountdown(step.durationSeconds)}" else "remaining"
                            Text(
                                formatCountdown(displaySeconds),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isPaused)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.45f)
                                else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.semantics {
                                    liveRegion = LiveRegionMode.Polite
                                    contentDescription = "${formatCountdown(displaySeconds)} $timerLabel"
                                }
                            )
                            Text(
                                timerLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        } else {
                            if (step.repsOrDurationLabel.isNotBlank()) {
                                Text(
                                    formatRepsLabel(step.repsOrDurationLabel),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        if (step.weight.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "WEIGHT",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                            )
                            Text(
                                step.weight,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        // Card-level actions for the active step.
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TextButton(
                                onClick = onMarkComplete,
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier.semantics { testTag = "btn_done" }
                            ) {
                                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.size(4.dp))
                                Text("Mark Complete")
                            }
                            TextButton(
                                onClick = onSkip,
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                modifier = Modifier.semantics { testTag = "btn_skip" }
                            ) {
                                Text("Skip")
                            }
                            TextButton(
                                onClick = onCancelExercise,
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.semantics { testTag = "btn_cancel_exercise" }
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
                is WorkoutStep.Rest -> ElevatedCard(
                    modifier = Modifier.weight(1f).padding(start = 8.dp, top = 4.dp, bottom = 14.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Rest",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.65f)
                        )
                        val restDisplaySeconds = if (countUp) step.durationSeconds - timerSeconds else timerSeconds
                        val restTimerLabel = if (countUp) "elapsed / ${formatCountdown(step.durationSeconds)}" else "remaining"
                        Text(
                            formatCountdown(restDisplaySeconds),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPaused)
                                MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.45f)
                            else MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                                contentDescription = "Rest: ${formatCountdown(restDisplaySeconds)} $restTimerLabel"
                            }
                        )
                        if (step.upNextName.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "UP NEXT",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                                letterSpacing = TextUnit(1.5f, TextUnitType.Sp)
                            )
                            Text(
                                step.upNextName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        TextButton(
                            onClick = onSkip,
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier.semantics { testTag = "btn_skip" }
                        ) {
                            Text("Skip Rest")
                        }
                    }
                }
            }
        } else {
            // Compact past / future row
            val label = when (step) {
                is WorkoutStep.Work -> buildString {
                    if (step.isWarmup) append("Warmup · ")
                    append(step.exerciseName)
                    if (step.totalSets > 1) append(" · Set ${step.setNumber}/${step.totalSets}")
                    if (step.side.isNotBlank()) append(" · ${step.side}")
                    if (step.repsOrDurationLabel.isNotBlank()) append(" · ${formatRepsLabel(step.repsOrDurationLabel)}")
                    if (step.weight.isNotBlank()) append(" · ${step.weight}")
                }
                is WorkoutStep.Rest -> buildString {
                    append("Rest · ${formatDuration(step.durationSeconds)}")
                    if (step.upNextName.isNotBlank()) append(" · then ${step.upNextName}")
                }
            }
            Text(
                label,
                style = if (!isPast && distance == 1) MaterialTheme.typography.bodyMedium
                        else MaterialTheme.typography.bodySmall,
                color = if (isRest && !isPast) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 4.dp)
            )
            if (showDoNext && !isPast && !isCurrent) {
                TextButton(
                    onClick = onDoNext,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .semantics { testTag = "btn_do_next" }
                ) {
                    Text("Do next", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(label: String) {
    Text(
        label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
    HorizontalDivider()
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    title: String,
    subtitle: String? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(14.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailing()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RestDurationInput(
    modifier: Modifier = Modifier,
    label: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    unit: RestUnit,
    onUnitChange: (RestUnit) -> Unit,
    amountTestTag: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                placeholder = { Text("0") },
                modifier = Modifier.weight(0.35f).then(
                    if (amountTestTag.isNotEmpty()) Modifier.semantics { testTag = amountTestTag } else Modifier
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(0.65f)
            ) {
                OutlinedTextField(
                    value = unit.label,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    RestUnit.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                onUnitChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightInput(
    modifier: Modifier = Modifier,
    label: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    unit: WeightUnit,
    onUnitChange: (WeightUnit) -> Unit,
    amountTestTag: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { onAmountChange(sanitizeWeightInput(it)) },
                placeholder = { Text("0") },
                modifier = Modifier.weight(0.35f).then(
                    if (amountTestTag.isNotEmpty()) Modifier.semantics { testTag = amountTestTag } else Modifier
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(0.65f)
            ) {
                OutlinedTextField(
                    value = unit.label,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    WeightUnit.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                onUnitChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    decrementEnabled: Boolean = true,
    incrementEnabled: Boolean = true,
    testTagPrefix: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        OutlinedIconButton(
            onClick = onDecrement,
            enabled = decrementEnabled,
            modifier = if (testTagPrefix.isNotEmpty())
                Modifier.semantics { testTag = "${testTagPrefix}_dec" } else Modifier
        ) {
            Icon(Icons.Rounded.Remove, contentDescription = "Decrease $label")
        }
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 64.dp)
        )
        OutlinedIconButton(
            onClick = onIncrement,
            enabled = incrementEnabled,
            modifier = if (testTagPrefix.isNotEmpty())
                Modifier.semantics { testTag = "${testTagPrefix}_inc" } else Modifier
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Increase $label")
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier, shape = RoundedCornerShape(14.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RoutineTrackerScreenPreview() {
    RepsTheme {
        RoutineTrackerScreen()
    }
}
