package net.theboyers.reps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// ── currentExerciseIndex ──────────────────────────────────────────────────────

class CurrentExerciseIndexTest {

    private fun work(exIdx: Int, setNumber: Int = 1) =
        WorkoutStep.Work("Ex$exIdx", "", setNumber, 1, "10", 0, "", "", false, exIdx)

    private fun rest(upNext: String = "") = WorkoutStep.Rest(60, upNext)

    @Test
    fun onAWorkStep_returnsItsExercise() {
        val steps = listOf(work(0, 1), rest(), work(1, 1))
        assertEquals(0, currentExerciseIndex(steps, 0))
        assertEquals(1, currentExerciseIndex(steps, 2))
    }

    @Test
    fun onAnUpcomingRest_returnsTheNextExercise() {
        val steps = listOf(work(0, 1), rest("Ex1"), work(1, 1))
        assertEquals(1, currentExerciseIndex(steps, 1))
    }

    @Test
    fun onATrailingRest_fallsBackToThePreviousExercise() {
        val steps = listOf(work(0, 1), rest())
        assertEquals(0, currentExerciseIndex(steps, 1))
    }

    @Test
    fun outOfRange_returnsMinusOne() {
        assertEquals(-1, currentExerciseIndex(listOf(work(0, 1)), 5))
    }
}

// ── canDeferExercise ──────────────────────────────────────────────────────────

class CanDeferExerciseTest {

    private fun work(exIdx: Int, setNumber: Int = 1) =
        WorkoutStep.Work("Ex$exIdx", "", setNumber, 3, "10", 0, "", "", false, exIdx)

    private fun rest(upNext: String = "") = WorkoutStep.Rest(60, upNext)

    @Test
    fun firstSetOfExercise_canBeDeferred() {
        val steps = listOf(work(1, setNumber = 1))
        assertTrue(canDeferExercise(steps, 0, 1))
    }

    @Test
    fun midExerciseCannotBeDeferred() {
        val steps = listOf(work(1, setNumber = 2))
        assertFalse(canDeferExercise(steps, 0, 1))
    }

    @Test
    fun betweenExercisesRest_canBeDeferred() {
        val steps = listOf(rest("Ex1"))
        assertTrue(canDeferExercise(steps, 0, 1))
    }

    @Test
    fun betweenSetsRest_cannotBeDeferred() {
        val steps = listOf(rest(""))
        assertFalse(canDeferExercise(steps, 0, 1))
    }

    @Test
    fun differentExercise_cannotBeDeferred() {
        val steps = listOf(work(2, setNumber = 1))
        assertFalse(canDeferExercise(steps, 0, 1))
    }
}

// ── moveWithin ────────────────────────────────────────────────────────────────

class MoveWithinTest {

    @Test
    fun movesItemLater() {
        assertEquals(listOf("b", "c", "a"), moveWithin(listOf("a", "b", "c"), 0, 2))
    }

    @Test
    fun movesItemEarlier() {
        assertEquals(listOf("c", "a", "b"), moveWithin(listOf("a", "b", "c"), 2, 0))
    }

    @Test
    fun sameIndex_returnsSameList() {
        val list = listOf("a", "b")
        assertEquals(list, moveWithin(list, 1, 1))
    }

    @Test
    fun invalidIndex_returnsSameList() {
        val list = listOf("a", "b")
        assertEquals(list, moveWithin(list, 5, 0))
    }
}

// ── shiftReps ─────────────────────────────────────────────────────────────────
class ShiftRepsTest {

    @Test fun incrementsPlainNumber() = assertEquals("11", shiftReps("10", 1))
    @Test fun decrementsPlainNumber() = assertEquals("9", shiftReps("10", -1))
    @Test fun shiftsRange() = assertEquals("11-15", shiftReps("10-14", 1))
    @Test fun blankWithIncrement_returnsOne() = assertEquals("1", shiftReps("", 1))
    @Test fun blankWithDecrement_returnsBlank() = assertEquals("", shiftReps("", -1))
    @Test fun neverGoesNegative() = assertEquals("0", shiftReps("0", -1))
    @Test fun zeroDelta_unchanged() = assertEquals("8", shiftReps("8", 0))
}

// ── shiftWeight ───────────────────────────────────────────────────────────────

class ShiftWeightTest {

    @Test fun blankPlusStep_startsFromZero() =
        assertEquals("5 lb", shiftWeight(null, 5.0, WeightUnit.LB))

    @Test fun blankMinusStep_staysBlank() =
        assertNull(shiftWeight(null, -5.0, WeightUnit.LB))

    @Test fun incrementsPounds() =
        assertEquals("140 lb", shiftWeight("135 lb", 5.0, WeightUnit.LB))

    @Test fun decrementsKilograms() =
        assertEquals("57.5 kg", shiftWeight("60 kg", -2.5, WeightUnit.KG))

    @Test fun droppingToZero_returnsNull() =
        assertNull(shiftWeight("5 lb", -5.0, WeightUnit.LB))

    @Test fun keepsDecimalAmounts() =
        assertEquals("2.5 kg", shiftWeight(null, 2.5, WeightUnit.KG))
}

// ── firstStepOfExercise ───────────────────────────────────────────────────────

class FirstStepOfExerciseTest {

    private fun work(exIdx: Int) =
        WorkoutStep.Work("Ex$exIdx", "", 1, 1, "10", 0, "", "", false, exIdx)

    @Test fun returnsTheFirstWorkStepOfTheExercise() {
        val steps = listOf(work(0), work(1), work(2))
        assertEquals(1, firstStepOfExercise(steps, 1))
        assertEquals(2, firstStepOfExercise(steps, 2))
    }

    @Test fun returnsFirstWhenAnExerciseHasMultipleSteps() {
        val steps = listOf(work(0), work(1), work(1), work(2))
        assertEquals(1, firstStepOfExercise(steps, 1))
    }

    @Test fun missingExercise_returnsMinusOne() {
        assertEquals(-1, firstStepOfExercise(listOf(work(0), work(1)), 5))
    }
}
