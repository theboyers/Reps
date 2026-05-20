package net.theboyers.reps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

// ── validateRoutineInput ──────────────────────────────────────────────────────

class ValidateRoutineInputTest {

    @Test fun blankName_returnsError() =
        assertNotNull(validateRoutineInput("", "3"))

    @Test fun blankName_correctMessage() =
        assertEquals("Exercise name is required.", validateRoutineInput("", "3"))

    @Test fun nonNumericSets_returnsError() =
        assertEquals("Sets must be a number.", validateRoutineInput("Squats", "three"))

    @Test fun zeroSets_returnsError() =
        assertEquals("Sets must be at least 1.", validateRoutineInput("Squats", "0"))

    @Test fun negativeSets_returnsError() =
        assertEquals("Sets must be at least 1.", validateRoutineInput("Squats", "-1"))

    @Test fun blankSets_isValid() =
        assertNull(validateRoutineInput("Squats", ""))

    @Test fun validNameAndSets_returnsNull() =
        assertNull(validateRoutineInput("Bench Press", "4"))

    @Test fun nameWithSets1_returnsNull() =
        assertNull(validateRoutineInput("Pull-ups", "1"))

    @Test fun nameWithLargeSets_returnsNull() =
        assertNull(validateRoutineInput("Run", "100"))
}

// ── parseRestSeconds ──────────────────────────────────────────────────────────

class ParseRestSecondsTest {

    @Test fun blank_returnsZero() =
        assertEquals(0, parseRestSeconds("", RestUnit.SECONDS))

    @Test fun zeroSeconds_returnsZero() =
        assertEquals(0, parseRestSeconds("0", RestUnit.SECONDS))

    @Test fun thirtySeconds_returns30() =
        assertEquals(30, parseRestSeconds("30", RestUnit.SECONDS))

    @Test fun twoMinutes_returns120() =
        assertEquals(120, parseRestSeconds("2", RestUnit.MINUTES))

    @Test fun ninetyMinutes_returns5400() =
        assertEquals(5400, parseRestSeconds("90", RestUnit.MINUTES))

    @Test fun nonNumeric_returnsNull() =
        assertNull(parseRestSeconds("abc", RestUnit.SECONDS))

    @Test fun negativeValue_returnsNull() =
        assertNull(parseRestSeconds("-1", RestUnit.SECONDS))

    @Test fun blankWithMinutes_returnsZero() =
        assertEquals(0, parseRestSeconds("", RestUnit.MINUTES))
}

// ── parseWorkDurationSeconds ──────────────────────────────────────────────────

class ParseWorkDurationSecondsTest {

    @Test fun blank_returnsNull() =
        assertNull(parseWorkDurationSeconds(""))

    @Test fun repsOnly_returnsNull() =
        assertNull(parseWorkDurationSeconds("10"))

    @Test fun repsRange_returnsNull() =
        assertNull(parseWorkDurationSeconds("8-12"))

    @Test fun secondsWithUnit_parsesCorrectly() =
        assertEquals(45, parseWorkDurationSeconds("45 sec"))

    @Test fun secondsShorthand_parsesCorrectly() =
        assertEquals(30, parseWorkDurationSeconds("30s"))

    @Test fun minutesWithUnit_parsesCorrectly() =
        assertEquals(120, parseWorkDurationSeconds("2 min"))

    @Test fun minutesSingular_parsesCorrectly() =
        assertEquals(60, parseWorkDurationSeconds("1 min"))

    @Test fun largeSeconds_parsesCorrectly() =
        assertEquals(90, parseWorkDurationSeconds("90 sec"))

    @Test fun twentyMinutes_parsesCorrectly() =
        assertEquals(1200, parseWorkDurationSeconds("20 min"))

    @Test fun caseInsensitive_parsesCorrectly() =
        assertEquals(45, parseWorkDurationSeconds("45 SEC"))
}

// ── formatDuration ────────────────────────────────────────────────────────────

class FormatDurationTest {

    @Test fun zero_returnsNone() =
        assertEquals("None", formatDuration(0))

    @Test fun negative_returnsNone() =
        assertEquals("None", formatDuration(-10))

    @Test fun thirtySeconds_returnsSeconds() =
        assertEquals("30 sec", formatDuration(30))

    @Test fun exactlyOneMinute_returnsMinOnly() =
        assertEquals("1 min", formatDuration(60))

    @Test fun exactlyTwoMinutes_returnsMinOnly() =
        assertEquals("2 min", formatDuration(120))

    @Test fun ninetySeconds_returnsMixed() =
        assertEquals("1 min 30 sec", formatDuration(90))

    @Test fun oneSecond_returnsSec() =
        assertEquals("1 sec", formatDuration(1))

    @Test fun fiveMinutes_returnsMinOnly() =
        assertEquals("5 min", formatDuration(300))
}

// ── formatCountdown ───────────────────────────────────────────────────────────

class FormatCountdownTest {

    @Test fun zero_returnsZeroString() =
        assertEquals("0", formatCountdown(0))

    @Test fun negative_returnsZeroString() =
        assertEquals("0", formatCountdown(-5))

    @Test fun underOneMinute_returnsSeconds() =
        assertEquals("30", formatCountdown(30))

    @Test fun exactlyOneMinute_returnsMMSS() =
        assertEquals("1:00", formatCountdown(60))

    @Test fun ninetySeconds_returnsMMSS() =
        assertEquals("1:30", formatCountdown(90))

    @Test fun twoMinutesFive_returnsMMSS() =
        assertEquals("2:05", formatCountdown(125))

    @Test fun singleDigitSeconds_padded() =
        assertEquals("1:05", formatCountdown(65))

    @Test fun oneSecond_returnsOne() =
        assertEquals("1", formatCountdown(1))
}
