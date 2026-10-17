package net.theboyers.reps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// ── validateWeight ────────────────────────────────────────────────────────────

class ValidateWeightTest {

    @Test fun blank_isValid() =
        assertNull(validateWeight(""))

    @Test fun whitespace_isValid() =
        assertNull(validateWeight("   "))

    @Test fun nonNumeric_returnsError() =
        assertEquals("Weight must be a number.", validateWeight("heavy"))

    @Test fun zero_isValid() =
        assertNull(validateWeight("0"))

    @Test fun negative_returnsError() =
        assertEquals("Weight cannot be negative.", validateWeight("-10"))

    @Test fun integer_isValid() =
        assertNull(validateWeight("135"))

    @Test fun decimal_isValid() =
        assertNull(validateWeight("2.5"))

    @Test fun partialDecimal_isValid() =
        assertNull(validateWeight("2."))
}

// ── formatWeight ──────────────────────────────────────────────────────────────

class FormatWeightTest {

    @Test fun blank_returnsNull() =
        assertNull(formatWeight("", WeightUnit.LB))

    @Test fun nonNumeric_returnsNull() =
        assertNull(formatWeight("heavy", WeightUnit.LB))

    @Test fun zero_returnsNull() =
        assertNull(formatWeight("0", WeightUnit.LB))

    @Test fun negative_returnsNull() =
        assertNull(formatWeight("-5", WeightUnit.KG))

    @Test fun wholePounds_stripsDecimal() =
        assertEquals("135 lb", formatWeight("135", WeightUnit.LB))

    @Test fun wholePoundsFromDecimal_stripsTrailingZero() =
        assertEquals("135 lb", formatWeight("135.0", WeightUnit.LB))

    @Test fun decimalKilograms_kept() =
        assertEquals("2.5 kg", formatWeight("2.5", WeightUnit.KG))

    @Test fun wholeKilograms_kept() =
        assertEquals("60 kg", formatWeight("60", WeightUnit.KG))

    @Test fun surroundingWhitespace_trimmed() =
        assertEquals("45 lb", formatWeight(" 45 ", WeightUnit.LB))
}

// ── weightAmountOf / weightUnitOf ─────────────────────────────────────────────

class WeightParseTest {

    @Test fun amount_null_isBlank() =
        assertEquals("", weightAmountOf(null))

    @Test fun amount_blank_isBlank() =
        assertEquals("", weightAmountOf("  "))

    @Test fun amount_pounds() =
        assertEquals("135", weightAmountOf("135 lb"))

    @Test fun amount_kilograms() =
        assertEquals("2.5", weightAmountOf("2.5 kg"))

    @Test fun amount_roundTrips() =
        assertEquals("135", weightAmountOf(formatWeight("135", WeightUnit.LB)))

    @Test fun unit_null_defaultsToPounds() =
        assertEquals(WeightUnit.LB, weightUnitOf(null))

    @Test fun unit_pounds() =
        assertEquals(WeightUnit.LB, weightUnitOf("135 lb"))

    @Test fun unit_kilograms() =
        assertEquals(WeightUnit.KG, weightUnitOf("60 kg"))

    @Test fun unit_uppercaseKilograms() =
        assertEquals(WeightUnit.KG, weightUnitOf("60 KG"))
}

// ── sanitizeWeightInput ───────────────────────────────────────────────────────

class SanitizeWeightInputTest {
    @Test fun blank_isBlank() =
        assertEquals("", sanitizeWeightInput(""))

    @Test fun letters_removed() =
        assertEquals("12", sanitizeWeightInput("1a2"))

    @Test fun onlyLetters_becomesBlank() =
        assertEquals("", sanitizeWeightInput("abc"))

    @Test fun keepsSingleDecimalPoint() =
        assertEquals("2.5", sanitizeWeightInput("2.5"))

    @Test fun extraDecimalPoints_removed() =
        assertEquals("1.23", sanitizeWeightInput("1.2.3"))

    @Test fun leadingDecimalPoint_kept() =
        assertEquals(".5", sanitizeWeightInput(".5"))

    @Test fun digits_kept() =
        assertEquals("135", sanitizeWeightInput("135"))
}

// ── formatRepsLabel ───────────────────────────────────────────────────────────

class FormatRepsLabelTest {

    @Test fun blank_returnsBlank() =
        assertEquals("", formatRepsLabel(""))

    @Test fun whitespace_returnsBlank() =
        assertEquals("", formatRepsLabel("   "))

    @Test fun singleRep_usesSingular() =
        assertEquals("1 rep", formatRepsLabel("1"))

    @Test fun bareCount_addsReps() =
        assertEquals("12 reps", formatRepsLabel("12"))

    @Test fun range_addsReps() =
        assertEquals("8-12 reps", formatRepsLabel("8-12"))

    @Test fun perSide_unchanged() =
        assertEquals("10/side", formatRepsLabel("10/side"))

    @Test fun duration_unchanged() =
        assertEquals("45 sec", formatRepsLabel("45 sec"))

    @Test fun word_unchanged() =
        assertEquals("AMRAP", formatRepsLabel("AMRAP"))

    @Test fun surroundingWhitespace_trimmed() =
        assertEquals("12 reps", formatRepsLabel(" 12 "))
}

// ── sanitizeRepsInput ─────────────────────────────────────────────────────────

class SanitizeRepsInputTest {

    @Test fun blank_isBlank() =
        assertEquals("", sanitizeRepsInput(""))

    @Test fun bareNumber_kept() =
        assertEquals("10", sanitizeRepsInput("10"))

    @Test fun range_kept() =
        assertEquals("10-14", sanitizeRepsInput("10-14"))

    @Test fun trailingHyphen_keptWhileTyping() =
        assertEquals("10-", sanitizeRepsInput("10-"))

    @Test fun letters_removed() =
        assertEquals("10", sanitizeRepsInput("1a0"))

    @Test fun perSideText_removed() =
        assertEquals("10", sanitizeRepsInput("10/leg"))

    @Test fun leadingHyphen_removed() =
        assertEquals("5", sanitizeRepsInput("-5"))

    @Test fun secondRange_truncated() =
        assertEquals("10-14", sanitizeRepsInput("10-14-20"))

    @Test fun spacesStripped() =
        assertEquals("20-14", sanitizeRepsInput("20 - 14"))
}

// ── validateReps ──────────────────────────────────────────────────────────────

class ValidateRepsTest {

    @Test fun blank_isValid() =
        assertNull(validateReps(""))

    @Test fun bareNumber_isValid() =
        assertNull(validateReps("10"))

    @Test fun range_isValid() =
        assertNull(validateReps("10-14"))

    @Test fun zero_isValid() =
        assertNull(validateReps("0"))

    @Test fun trailingHyphen_isInvalid() =
        assertEquals(
            "Reps must be a number or a range (e.g. 10 or 10-14).",
            validateReps("10-")
        )

    @Test fun leadingHyphen_isInvalid() =
        assertEquals(
            "Reps must be a number or a range (e.g. 10 or 10-14).",
            validateReps("-10")
        )

    @Test fun doubleRange_isInvalid() =
        assertEquals(
            "Reps must be a number or a range (e.g. 10 or 10-14).",
            validateReps("10-14-20")
        )

    @Test fun text_isInvalid() =
        assertEquals(
            "Reps must be a number or a range (e.g. 10 or 10-14).",
            validateReps("AMRAP")
        )
}
