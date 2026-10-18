package net.theboyers.reps

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * UI tests for the strength-exercise weight field and warmup sets:
 * entering weight (lb/kg), adding warmup sets, validation, editing, and
 * how weight/warmup surface in the running workout.
 *
 * Run with: ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class StrengthWeightTest {

    private val _composeTestRule = createAndroidComposeRule<MainActivity>()
    val composeTestRule get() = _composeTestRule

    @get:Rule
    val chain: RuleChain = RuleChain
        .outerRule(object : ExternalResource() {
            override fun before() {
                InstrumentationRegistry.getInstrumentation().targetContext
                    .getSharedPreferences("reps_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    // Short grace period keeps the running-phase test fast.
                    .putInt("pref_grace_period", 3)
                    .commit()
            }
        })
        .around(_composeTestRule)

    // ── weight entry ──────────────────────────────────────────────────────────

    @Test
    fun addStrengthWithWeight_showsWeightInList() {
        addStrength(weight = "135")
        composeTestRule.onNodeWithText("135 lb", substring = true).assertIsDisplayed()
    }

    @Test
    fun addStrengthWithKilograms_showsKilograms() {
        addStrength(weight = "60", unit = WeightUnit.KG)
        composeTestRule.onNodeWithText("60 kg", substring = true).assertIsDisplayed()
    }

    @Test
    fun addStrengthWithoutWeight_showsNoWeight() {
        addStrength()
        composeTestRule.onNodeWithText(" lb", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText(" kg", substring = true).assertDoesNotExist()
    }

    @Test
    fun decimalWeight_isAccepted() {
        addStrength(weight = "2.5", unit = WeightUnit.KG)
        composeTestRule.onNodeWithText("2.5 kg", substring = true).assertIsDisplayed()
    }

    @Test
    fun decimalOnlyWeight_showsValidationError() {
        addStrength(weight = ".", submit = false)
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Weight must be a number.").assertIsDisplayed()
    }

    // ── warmup entry ──────────────────────────────────────────────────────────

    @Test
    fun warmupToggle_revealsWarmupFields() {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_warmup_sets").assertDoesNotExist()
        composeTestRule.onNodeWithTag("field_warmup_toggle").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("field_warmup_sets").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("field_warmup_reps").assertIsDisplayed()
        composeTestRule.onNodeWithTag("field_warmup_weight").assertIsDisplayed()
    }

    @Test
    fun addStrengthWithWarmup_showsWarmupInList() {
        addStrength(warmup = true)
        composeTestRule.onNodeWithText("1 warmup", substring = true).assertIsDisplayed()
    }

    @Test
    fun twoWarmupSets_showCountInList() {
        addStrength(warmup = true, warmupSets = "2")
        composeTestRule.onNodeWithText("2 warmup", substring = true).assertIsDisplayed()
    }

    @Test
    fun zeroWarmupSets_showsValidationError() {
        addStrength(warmup = true, warmupSets = "0", submit = false)
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Warmup sets must be a positive number.").assertIsDisplayed()
    }

    // ── editing ───────────────────────────────────────────────────────────────

    @Test
    fun editStrengthExercise_repopulatesWeightAndWarmup() {
        addStrength(weight = "135", warmup = true)

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Edit").performClick()

        composeTestRule.onNodeWithTag("field_weight").performScrollTo().assertTextContains("135")
        composeTestRule.onNodeWithTag("field_warmup_sets").performScrollTo().assertIsDisplayed()
    }

    // ── running workout ───────────────────────────────────────────────────────

    @Test
    fun runningWorkout_warmupStep_showsWarmupBadge() {
        addStrength(sets = "3", weight = "135", warmup = true)
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithText("WARMUP").assertIsDisplayed()
    }

    @Test
    fun runningWorkout_showsWeight() {
        addStrength(sets = "3", weight = "135")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("weight_value").assertTextEquals("135")
        composeTestRule.onNodeWithTag("weight_unit").assertTextEquals("lb")
    }

    @Test
    fun runningWorkout_showsEffortTag() {
        addStrength(sets = "1", tag = "AMRAP")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("current_set_tag").assertTextEquals("AMRAP")
    }

    @Test
    fun runningWorkout_showsRepsStat() {
        addStrength(sets = "3", reps = "12")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("set_value").assertTextEquals("12")
        composeTestRule.onNodeWithTag("set_label").assertTextEquals("reps")
    }

    @Test
    fun runningWorkout_warmupDone_advancesToWorkingSet() {
        addStrength(sets = "3", weight = "135", warmup = true)
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        // A rest step follows the warmup (default 90s); skip it to reach the first working set.
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("btn_skip").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("btn_skip").performClick()

        composeTestRule.onNodeWithText("Set 1 of 3").assertIsDisplayed()
    }

    // ── timed exercises with weight ─────────────────────────────────────────────

    @Test
    fun addTimedWithWeight_showsWeightInList() {
        addTimed(weight = "25", unit = WeightUnit.KG)
        composeTestRule.onNodeWithText("25 kg", substring = true).assertIsDisplayed()
    }

    @Test
    fun runningWorkout_timedExercise_showsWeight() {
        addTimed(weight = "25")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("weight_value").assertTextEquals("25")
        composeTestRule.onNodeWithTag("weight_unit").assertTextEquals("lb")
    }

    @Test
    fun zeroWeight_isHidden() {
        addTimed(weight = "0")
        composeTestRule.onNodeWithText("Plank").assertIsDisplayed()
        composeTestRule.onNodeWithText("0 lb", substring = true).assertDoesNotExist()
    }

    @Test
    fun runningWorkout_timedExercise_showsTimerAdjustButtons() {
        addTimed(duration = "45")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_minus_10").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_plus_10").assertIsDisplayed()
    }

    @Test
    fun restBetweenSets_differentWeight_showsChangePrompt() {
        addStrength(sets = "1", weight = "60", warmup = true, warmupWeight = "40")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("weight_change_prompt").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Set to 60 lb").assertIsDisplayed()
    }

    @Test
    fun restBetweenSets_sameWeight_hasNoChangePrompt() {
        addStrength(sets = "1", weight = "60", warmup = true, warmupWeight = "60")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("btn_skip").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("weight_change_prompt").assertDoesNotExist()
    }

    @Test
    fun recentlyUsedExercise_prefillsSetsAndReps() {
        // Record Squats as used with 5 sets × 8 reps.
        addStrength(name = "Squats", sets = "5", reps = "8")
        // Adding it again should prefill from the last use.
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput("Squats")

        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).assertTextContains("5")
        composeTestRule.onNodeWithTag("field_reps").assertTextContains("8")
    }

    // ── reps format ─────────────────────────────────────────────────────────────

    @Test
    fun repsRange_isAccepted() {
        addStrength(reps = "10-14")
        composeTestRule.onNodeWithText("10-14 reps", substring = true).assertIsDisplayed()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithTag("fab_add_exercise").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("exercise_form_sheet").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun addTimed(
        name: String = "Plank",
        sets: String = "3",
        duration: String = "45",
        weight: String? = null,
        unit: WeightUnit = WeightUnit.LB
    ) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithText("Timed").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput(sets)
        composeTestRule.onNode(hasText("Duration").and(hasSetTextAction())).performTextInput(duration)
        if (weight != null) {
            composeTestRule.onNodeWithTag("field_weight").performScrollTo().performTextInput(weight)
        }
        if (unit == WeightUnit.KG) {
            composeTestRule.onNode(hasText("lb")).performScrollTo().performClick()
            composeTestRule.onNode(hasText("kg")).performClick()
        }
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
    }

    private fun addStrength(
        name: String = "Bench Press",
        sets: String = "3",
        reps: String? = null,
        weight: String? = null,
        unit: WeightUnit = WeightUnit.LB,
        warmup: Boolean = false,
        warmupSets: String = "1",
        warmupWeight: String? = null,
        tag: String? = null,
        submit: Boolean = true
    ) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput(sets)
        if (reps != null) {
            composeTestRule.onNode(hasText("Reps").and(hasSetTextAction())).performTextInput(reps)
        }
        if (weight != null) {
            composeTestRule.onNodeWithTag("field_weight").performScrollTo().performTextInput(weight)
        }
        if (unit == WeightUnit.KG) {
            // Open the unit dropdown and pick kilograms.
            composeTestRule.onNode(hasText("lb")).performScrollTo().performClick()
            composeTestRule.onNode(hasText("kg")).performClick()
        }
        if (tag != null) {
            composeTestRule.onNodeWithTag("field_rep_tag").performScrollTo().performClick()
            composeTestRule.onNodeWithText(tag).performClick()
        }
        if (warmup) {
            composeTestRule.onNodeWithTag("field_warmup_toggle").performScrollTo().performClick()
            if (warmupSets != "1") {
                composeTestRule.onNodeWithTag("field_warmup_sets").performScrollTo().performTextClearance()
                composeTestRule.onNodeWithTag("field_warmup_sets").performTextInput(warmupSets)
            }
            if (warmupWeight != null) {
                composeTestRule.onNodeWithTag("field_warmup_weight").performScrollTo().performTextInput(warmupWeight)
            }
        }
        if (submit) {
            composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
        }
    }

    private fun startWorkoutAndWaitForRunning() {
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("screen_running").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
