package net.theboyers.reps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoutineValidationTest {

    @Test
    fun validateRoutineInput_returnsErrorWhenNameMissing() {
        val result = validateRoutineInput(exerciseName = "", sets = "3")

        assertEquals("Exercise name is required.", result)
    }

    @Test
    fun validateRoutineInput_returnsErrorWhenSetsNotNumeric() {
        val result = validateRoutineInput(exerciseName = "Squats", sets = "three")

        assertEquals("Sets must be a number.", result)
    }

    @Test
    fun validateRoutineInput_returnsErrorWhenSetsLessThanOne() {
        val result = validateRoutineInput(exerciseName = "Lunges", sets = "0")

        assertEquals("Sets must be at least 1.", result)
    }

    @Test
    fun validateRoutineInput_returnsNullWhenInputValid() {
        val result = validateRoutineInput(exerciseName = "Treadmill", sets = "4")

        assertNull(result)
    }

    @Test
    fun parseRestSeconds_parsesSecondsInput() {
        val seconds = parseRestSeconds("30", RestUnit.SECONDS)

        assertEquals(30, seconds ?: -1)
    }

    @Test
    fun parseRestSeconds_parsesMinutesInput() {
        val seconds = parseRestSeconds("2", RestUnit.MINUTES)

        assertEquals(120, seconds ?: -1)
    }

    @Test
    fun parseRestSeconds_returnsZeroWhenBlank() {
        val seconds = parseRestSeconds("", RestUnit.SECONDS)

        assertEquals(0, seconds ?: -1)
    }

    @Test
    fun parseRestSeconds_returnsNullForInvalidInput() {
        val seconds = parseRestSeconds("not-a-number", RestUnit.SECONDS)

        assertEquals(null, seconds)
    }

    @Test
    fun formatDuration_formatsMixedDuration() {
        val text = formatDuration(90)

        assertEquals("1 min 30 sec", text)
    }
}

