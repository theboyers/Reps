package net.theboyers.reps

import org.junit.Assert.assertEquals
import org.junit.Test

// ── representativeReps ────────────────────────────────────────────────────────

class RepresentativeRepsTest {

    @Test fun plainNumber() = assertEquals(10, representativeReps("10"))
    @Test fun range_usesFirstNumber() = assertEquals(8, representativeReps("8-12"))
    @Test fun blank_returnsNull() = assertEquals(null, representativeReps(""))
    @Test fun textOnly_returnsNull() = assertEquals(null, representativeReps("AMRAP"))
    @Test fun durationLabel_returnsNumber() = assertEquals(45, representativeReps("45 sec"))
}

// ── computeWorkoutStats ───────────────────────────────────────────────────────

class ComputeWorkoutStatsTest {

    private fun rep(name: String = "Squats", reps: String = "10", weight: String? = null, warmup: Boolean = false) =
        WorkoutStep.Work(name, "", 1, 1, reps, 0, "", weight.orEmpty(), warmup)

    private fun timed(name: String = "Plank", seconds: Int = 45, weight: String? = null) =
        WorkoutStep.Work(name, "", 1, 1, "$seconds sec", seconds, "", weight.orEmpty())

    @Test fun empty_isAllZero() {
        val stats = computeWorkoutStats(emptyList())
        assertEquals(0, stats.setsCompleted)
        assertEquals(0, stats.repsCompleted)
        assertEquals(0.0, stats.volumeLb, 0.001)
        assertEquals(0.0, stats.volumeKg, 0.001)
    }

    @Test fun bodyweightSet_countsRepsButNoVolume() {
        val stats = computeWorkoutStats(listOf(rep(reps = "10")))
        assertEquals(1, stats.setsCompleted)
        assertEquals(10, stats.repsCompleted)
        assertEquals(0.0, stats.volumeLb, 0.001)
    }

    @Test fun weightAndReps_computeVolume() {
        val stats = computeWorkoutStats(listOf(rep(reps = "10", weight = "135 lb")))
        assertEquals(1, stats.setsCompleted)
        assertEquals(10, stats.repsCompleted)
        assertEquals(1350.0, stats.volumeLb, 0.001)
    }

    @Test fun multipleSets_accumulate() {
        val stats = computeWorkoutStats(
            listOf(rep(reps = "10", weight = "100 lb"), rep(reps = "8", weight = "100 lb"))
        )
        assertEquals(2, stats.setsCompleted)
        assertEquals(18, stats.repsCompleted)
        assertEquals(1800.0, stats.volumeLb, 0.001)
    }

    @Test fun kilogramsTrackedSeparately() {
        val stats = computeWorkoutStats(listOf(rep(reps = "10", weight = "60 kg")))
        assertEquals(600.0, stats.volumeKg, 0.001)
        assertEquals(0.0, stats.volumeLb, 0.001)
    }

    @Test fun timedSet_countsAsASetButNotRepsOrVolume() {
        val stats = computeWorkoutStats(listOf(timed(seconds = 45, weight = "25 lb")))
        assertEquals(1, stats.setsCompleted)
        assertEquals(0, stats.repsCompleted)
        assertEquals(0.0, stats.volumeLb, 0.001)
    }

    @Test fun rangeRepsUseFirstNumber() {
        val stats = computeWorkoutStats(listOf(rep(reps = "8-12", weight = "100 lb")))
        assertEquals(8, stats.repsCompleted)
        assertEquals(800.0, stats.volumeLb, 0.001)
    }
}

// ── formatVolume ──────────────────────────────────────────────────────────────

class FormatVolumeTest {

    @Test fun thousandsSeparator() = assertEquals("1,350", formatVolume(1350.0))
    @Test fun roundsToNearest() = assertEquals("1,000", formatVolume(999.6))
    @Test fun smallNumber() = assertEquals("80", formatVolume(80.0))
    @Test fun zero() = assertEquals("0", formatVolume(0.0))
}
