package net.theboyers.reps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Tests for [buildWorkoutSteps] — the function that expands [RoutineEntry] objects
 * into the flat ordered sequence of [WorkoutStep]s that the running phase executes.
 */
class WorkoutStepBuilderTest {

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun strength(
        name: String,
        sets: Int = 3,
        reps: String = "10",
        restSecs: Int = 0,
        isPerSide: Boolean = false
    ) = RoutineEntry(name, "", sets.toString(), reps, restSecs, "STRENGTH", isPerSide)

    private fun timed(
        name: String,
        sets: Int = 3,
        durationSec: Int = 45,
        restSecs: Int = 0
    ) = RoutineEntry(name, "", sets.toString(), "${durationSec} sec", restSecs, "TIMED")

    private fun cardio(
        name: String,
        durationMin: Int = 20
    ) = RoutineEntry(name, "", "1", "${durationMin} min", 0, "CARDIO")

    private val List<WorkoutStep>.workSteps get() = filterIsInstance<WorkoutStep.Work>()
    private val List<WorkoutStep>.restSteps get() = filterIsInstance<WorkoutStep.Rest>()

    // ── empty input ───────────────────────────────────────────────────────────

    @Test fun emptyEntries_producesEmptyStepList() {
        val steps = buildWorkoutSteps(emptyList(), 0)
        assertTrue(steps.isEmpty())
    }

    // ── single strength exercise, no rest ────────────────────────────────────

    @Test fun singleExercise_noRest_producesOnlyWorkSteps() {
        val steps = buildWorkoutSteps(listOf(strength("Squats", sets = 3)), 0)
        assertEquals(3, steps.size)
        assertTrue(steps.all { it is WorkoutStep.Work })
    }

    @Test fun singleExercise_workStepsHaveCorrectSetNumbers() {
        val steps = buildWorkoutSteps(listOf(strength("Squats", sets = 3)), 0)
        val work = steps.workSteps
        assertEquals(1, work[0].setNumber)
        assertEquals(2, work[1].setNumber)
        assertEquals(3, work[2].setNumber)
    }

    @Test fun singleExercise_workStepsHaveCorrectTotalSets() {
        val steps = buildWorkoutSteps(listOf(strength("Squats", sets = 4)), 0)
        steps.workSteps.forEach { assertEquals(4, it.totalSets) }
    }

    @Test fun singleExercise_workStepsCarryName() {
        val steps = buildWorkoutSteps(listOf(strength("Bench Press", sets = 2)), 0)
        steps.workSteps.forEach { assertEquals("Bench Press", it.exerciseName) }
    }

    // ── rest between sets ─────────────────────────────────────────────────────

    @Test fun restBetweenSets_interleavesRestAfterEachSetExceptLast() {
        val steps = buildWorkoutSteps(listOf(strength("Squats", sets = 3, restSecs = 60)), 0)
        // 3 work + 2 rest (no rest after final set)
        assertEquals(5, steps.size)
        assertTrue(steps[0] is WorkoutStep.Work)
        assertTrue(steps[1] is WorkoutStep.Rest)
        assertTrue(steps[2] is WorkoutStep.Work)
        assertTrue(steps[3] is WorkoutStep.Rest)
        assertTrue(steps[4] is WorkoutStep.Work)
    }

    @Test fun restBetweenSets_hasCorrectDuration() {
        val steps = buildWorkoutSteps(listOf(strength("Squats", sets = 2, restSecs = 90)), 0)
        val rest = steps.restSteps.first()
        assertEquals(90, rest.durationSeconds)
    }

    @Test fun noRestAfterLastSet() {
        val steps = buildWorkoutSteps(listOf(strength("Squats", sets = 3, restSecs = 60)), 0)
        assertTrue(steps.last() is WorkoutStep.Work)
    }

    // ── rest between exercises ────────────────────────────────────────────────

    @Test fun restBetweenExercises_insertedBetweenExercises() {
        val steps = buildWorkoutSteps(
            listOf(strength("ExA", sets = 1), strength("ExB", sets = 1)),
            restBetweenExercisesSeconds = 90
        )
        // Work(ExA), Rest(90, upNext=ExB), Work(ExB)
        assertEquals(3, steps.size)
        assertTrue(steps[1] is WorkoutStep.Rest)
    }

    @Test fun restBetweenExercises_upNextNameIsCorrect() {
        val steps = buildWorkoutSteps(
            listOf(strength("Squats", sets = 1), strength("Lunges", sets = 1)),
            restBetweenExercisesSeconds = 60
        )
        val rest = steps[1] as WorkoutStep.Rest
        assertEquals("Lunges", rest.upNextName)
    }

    @Test fun noRestBetweenExercises_whenZero() {
        val steps = buildWorkoutSteps(
            listOf(strength("ExA", sets = 1), strength("ExB", sets = 1)),
            restBetweenExercisesSeconds = 0
        )
        assertEquals(2, steps.size)
        assertFalse(steps.any { it is WorkoutStep.Rest })
    }

    @Test fun noTrailingRestAfterLastExercise() {
        val steps = buildWorkoutSteps(
            listOf(strength("ExA", sets = 1), strength("ExB", sets = 1)),
            restBetweenExercisesSeconds = 60
        )
        assertTrue(steps.last() is WorkoutStep.Work)
    }

    // ── per-side exercises ───────────────────────────────────────────────────

    @Test fun perSideExercise_doublesStepCount() {
        val steps = buildWorkoutSteps(
            listOf(strength("Lunges", sets = 2, isPerSide = true)),
            0
        )
        // 2 sets × 2 sides = 4 work steps
        assertEquals(4, steps.workSteps.size)
    }

    @Test fun perSideExercise_alternatesLeftRight() {
        val steps = buildWorkoutSteps(
            listOf(strength("Lunges", sets = 2, isPerSide = true)),
            0
        )
        val sides = steps.workSteps.map { it.side }
        assertEquals(listOf("Left", "Right", "Left", "Right"), sides)
    }

    @Test fun perSideExercise_totalSetsIsDoubled() {
        val steps = buildWorkoutSteps(
            listOf(strength("Lunges", sets = 3, isPerSide = true)),
            0
        )
        steps.workSteps.forEach { assertEquals(6, it.totalSets) }
    }

    @Test fun nonPerSideExercise_sideIsBlank() {
        val steps = buildWorkoutSteps(listOf(strength("Squats", sets = 2)), 0)
        steps.workSteps.forEach { assertEquals("", it.side) }
    }

    // ── timed exercise ────────────────────────────────────────────────────────

    @Test fun timedExercise_durationSecondsIsPopulated() {
        val steps = buildWorkoutSteps(listOf(timed("Plank", sets = 3, durationSec = 45)), 0)
        steps.workSteps.forEach { assertEquals(45, it.durationSeconds) }
    }

    @Test fun timedExercise_withRest_interleaved() {
        val steps = buildWorkoutSteps(listOf(timed("Plank", sets = 3, durationSec = 30, restSecs = 20)), 0)
        assertEquals(5, steps.size) // 3 work + 2 rest
    }

    // ── cardio exercise ───────────────────────────────────────────────────────

    @Test fun cardioExercise_producesOneWorkStep() {
        val steps = buildWorkoutSteps(listOf(cardio("Treadmill", durationMin = 20)), 0)
        assertEquals(1, steps.size)
        assertTrue(steps[0] is WorkoutStep.Work)
    }

    @Test fun cardioExercise_durationConvertedToSeconds() {
        val steps = buildWorkoutSteps(listOf(cardio("Bike", durationMin = 10)), 0)
        assertEquals(600, steps.workSteps.first().durationSeconds)
    }

    // ── multi-exercise complex routine ───────────────────────────────────────

    @Test fun complexRoutine_correctTotalStepCount() {
        // ExA: 3 sets, 60s rest between sets  → 3 work + 2 rest = 5
        // Between: 90s rest                   → 1
        // ExB: 2 sets, 30s rest between sets  → 2 work + 1 rest = 3
        // Total = 9
        val steps = buildWorkoutSteps(
            listOf(
                strength("ExA", sets = 3, restSecs = 60),
                strength("ExB", sets = 2, restSecs = 30)
            ),
            restBetweenExercisesSeconds = 90
        )
        assertEquals(9, steps.size)
    }

    @Test fun complexRoutine_firstAndLastAreWorkSteps() {
        val steps = buildWorkoutSteps(
            listOf(strength("ExA", sets = 2), strength("ExB", sets = 2)),
            restBetweenExercisesSeconds = 60
        )
        assertTrue(steps.first() is WorkoutStep.Work)
        assertTrue(steps.last() is WorkoutStep.Work)
    }

    // ── step content ──────────────────────────────────────────────────────────

    @Test fun strengthStep_durationSecondsIsZero() {
        val steps = buildWorkoutSteps(listOf(strength("Pull-ups", sets = 1, reps = "8")), 0)
        assertEquals(0, steps.workSteps.first().durationSeconds)
    }

    @Test fun strengthStep_repsOrDurationLabelIsCorrect() {
        val steps = buildWorkoutSteps(listOf(strength("Pull-ups", sets = 1, reps = "8-12")), 0)
        assertEquals("8-12", steps.workSteps.first().repsOrDurationLabel)
    }
}

