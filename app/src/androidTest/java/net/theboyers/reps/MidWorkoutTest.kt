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
 * UI tests for editing and re-ordering exercises *during* a workout:
 *  - the Adjust sheet (sets / reps / weight)
 *  - "Machine busy" (defer the current exercise to the end)
 *  - "Do next" (move an upcoming exercise up)
 */
@RunWith(AndroidJUnit4::class)
class MidWorkoutTest {

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
                    .putInt("pref_grace_period", 3)
                    .commit()
            }
        })
        .around(_composeTestRule)

    // ── adjust ────────────────────────────────────────────────────────────────

    @Test
    fun adjust_incrementReps_updatesCurrentSet() {
        addStrength("Squats", sets = "1", reps = "10")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_adjust").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("adjust_reps_inc").performClick()
        composeTestRule.onNodeWithTag("adjust_done").performClick()

        composeTestRule.onNodeWithTag("set_value").assertTextEquals("11")
    }

    @Test
    fun adjust_incrementSets_addsASetToTheCurrentExercise() {
        addStrength("Squats", sets = "1", reps = "10")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_adjust").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("adjust_sets_inc").performClick()
        composeTestRule.onNodeWithTag("adjust_done").performClick()

        composeTestRule.onNodeWithText("Set 1 of 2").assertIsDisplayed()
    }

    @Test
    fun adjust_weight_startsFromZeroAndIncrements() {
        addStrength("Bench Press", sets = "1", reps = "5", weight = "135")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_adjust").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("adjust_weight_inc").performClick()
        composeTestRule.onNodeWithTag("adjust_done").performClick()

        composeTestRule.onNodeWithTag("weight_value").assertTextEquals("140")
    }

    // ── reorder ───────────────────────────────────────────────────────────────

    @Test
    fun machineBusy_defersCurrentExerciseToTheEnd() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Squats")

        // "Machine busy" now lives in the set-settings (gear) sheet.
        composeTestRule.onNodeWithTag("btn_adjust").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("btn_do_later").performClick()

        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Lunges")
    }

    @Test
    fun doNext_movesAnUpcomingExerciseUp() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        addStrength("Deadlifts", sets = "1")
        startWorkoutAndWaitForRunning()

        // Open the options menu on the last upcoming exercise (Deadlifts).
        composeTestRule.onAllNodesWithTag("btn_exercise_options").onLast().performScrollTo().performClick()
        composeTestRule.onNodeWithTag("btn_do_next").performClick()

        // Finish the current exercise; Deadlifts should be next.
        composeTestRule.onNodeWithTag("btn_done").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Deadlifts")
    }

    @Test
    fun futureExercise_canBeRemovedFromItsOptionsMenu() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onAllNodesWithTag("btn_exercise_options").onFirst().performScrollTo().performClick()
        composeTestRule.onNodeWithTag("btn_remove_exercise").performClick()
        composeTestRule.onNodeWithText("Remove exercise?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_confirm_cancel_exercise").performClick()

        composeTestRule.onNodeWithText("Lunges").assertDoesNotExist()
        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Squats")
    }

    @Test
    fun removedExercise_canBeUndone() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onAllNodesWithTag("btn_exercise_options").onFirst().performScrollTo().performClick()
        composeTestRule.onNodeWithTag("btn_remove_exercise").performClick()
        composeTestRule.onNodeWithTag("btn_confirm_cancel_exercise").performClick()

        composeTestRule.onNodeWithText("Lunges").assertDoesNotExist()

        // The removal offers a one-tap Undo.
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Undo").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Undo").performClick()

        composeTestRule.onNodeWithText("Lunges").assertIsDisplayed()
        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Squats")
    }

    // ── cancel exercise ───────────────────────────────────────────────────────

    @Test
    fun cancelExercise_showsConfirmationAndDoesNotRemoveImmediately() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_cancel_exercise").performScrollTo().performClick()

        composeTestRule.onNodeWithText("Cancel exercise?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Squats")
    }

    @Test
    fun cancelExercise_confirmed_removesAndAdvances() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_cancel_exercise").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("btn_confirm_cancel_exercise").performClick()

        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Lunges")
    }

    @Test
    fun cancelExercise_keep_leavesExerciseInPlace() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_cancel_exercise").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Keep").performClick()

        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Squats")
    }

    @Test
    fun reorderFromGear_movesAnUpcomingExerciseNext() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        addStrength("Deadlifts", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_adjust").performScrollTo().performClick()
        // The last "Do next" in the sheet belongs to Deadlifts.
        composeTestRule.onAllNodesWithTag("btn_reorder_next").onLast().performClick()
        composeTestRule.onNodeWithTag("adjust_done").performClick()

        composeTestRule.onNodeWithTag("btn_done").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Deadlifts")
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithTag("fab_add_exercise").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("exercise_form_sheet").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun addStrength(
        name: String,
        sets: String = "1",
        reps: String? = null,
        weight: String? = null
    ) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput(sets)
        if (reps != null) {
            composeTestRule.onNodeWithTag("field_reps").performTextInput(reps)
        }
        if (weight != null) {
            composeTestRule.onNodeWithTag("field_weight").performScrollTo().performTextInput(weight)
        }
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    private fun startWorkoutAndWaitForRunning() {
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("screen_running").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
