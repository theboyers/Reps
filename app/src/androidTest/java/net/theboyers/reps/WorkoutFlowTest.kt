package net.theboyers.reps

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests covering the full workout lifecycle:
 *   EDITING → GRACE → RUNNING → COMPLETE → back to EDITING
 *
 * For timer-driven transitions the tests use waitUntil{} or
 * advance the Compose clock so they remain fast and deterministic.
 *
 * Run with: ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class WorkoutFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearSharedPreferences() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("reps_prefs", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    // ── GRACE phase ───────────────────────────────────────────────────────────

    @Test
    fun startWorkout_transitionsToGracePhase() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.onNodeWithTag("screen_grace").assertIsDisplayed()
    }

    @Test
    fun gracePhase_showsGetReadyHeading() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.onNodeWithText("Get ready!").assertIsDisplayed()
    }

    @Test
    fun gracePhase_showsCountdownNumber() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.onNodeWithTag("grace_countdown").assertIsDisplayed()
    }

    @Test
    fun gracePhase_pauseButton_pausesCountdown() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        composeTestRule.onNodeWithTag("btn_pause_continue").performClick()

        composeTestRule.onNodeWithText("Paused").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_pause_continue")
            .assertTextEquals("Continue")
    }

    @Test
    fun gracePhase_cancelButton_returnsToEditing() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
    }

    @Test
    fun gracePhase_listShowsExercisesInWorkout() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        composeTestRule.onNodeWithText("Today's workout").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Squats")
            .onFirst()
            .assertIsDisplayed()
    }

    // ── RUNNING phase — rep-based step ────────────────────────────────────────

    @Test
    fun runningPhase_repBasedStep_showsDoneButton() {
        startWorkoutAndSkipGrace("Squats", sets = "2")

        composeTestRule.onNodeWithTag("btn_done").assertIsDisplayed()
    }

    @Test
    fun runningPhase_showsWorkingLabel() {
        startWorkoutAndSkipGrace("Squats")

        composeTestRule.onNodeWithTag("running_status_label")
            .assertTextEquals("WORKING")
    }

    @Test
    fun runningPhase_exerciseNameVisibleInTimeline() {
        startWorkoutAndSkipGrace("Pull-ups")

        composeTestRule.onNodeWithText("Pull-ups").assertIsDisplayed()
    }

    @Test
    fun runningPhase_showsStepProgress() {
        startWorkoutAndSkipGrace("Squats")
        composeTestRule.onNodeWithText("Step 1 of", substring = true).assertIsDisplayed()
    }

    @Test
    fun runningPhase_doneButton_advancesToNextStep() {
        addStrengthExercise("Squats", sets = "2")
        startGraceAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        // Should now be on step 2
        composeTestRule.onNodeWithText("Step 2 of", substring = true).assertIsDisplayed()
    }

    @Test
    fun runningPhase_pauseButton_showsPausedLabel() {
        startWorkoutAndSkipGrace("Squats")

        composeTestRule.onNodeWithTag("btn_pause_continue").performClick()

        composeTestRule.onNodeWithTag("running_status_label")
            .assertTextEquals("PAUSED")
    }

    @Test
    fun runningPhase_continueButton_resumesWorkout() {
        startWorkoutAndSkipGrace("Squats")

        composeTestRule.onNodeWithTag("btn_pause_continue").performClick()
        composeTestRule.onNodeWithTag("btn_pause_continue").performClick()

        composeTestRule.onNodeWithTag("running_status_label")
            .assertTextEquals("WORKING")
    }

    @Test
    fun runningPhase_endWorkout_returnsToEditing() {
        startWorkoutAndSkipGrace("Squats")

        composeTestRule.onNodeWithTag("btn_end_workout").performClick()

        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
    }

    // ── RUNNING phase — rest step ─────────────────────────────────────────────

    @Test
    fun runningPhase_restStep_showsSkipRestButton() {
        // Add an exercise with 5-second rest between sets so we can click Done
        // and land on a rest step
        addExerciseWithRest("Squats", sets = "2", restSecs = "5")
        startGraceAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("btn_skip").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Skip Rest").assertIsDisplayed()
    }

    @Test
    fun runningPhase_skipRest_advancesPastRestStep() {
        addExerciseWithRest("Squats", sets = "2", restSecs = "5")
        startGraceAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("btn_skip").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("btn_skip").performClick()

        // Should now be back on a work step (Done button visible)
        composeTestRule.onNodeWithTag("btn_done").assertIsDisplayed()
    }

    @Test
    fun runningPhase_restStep_showsRestLabel() {
        addExerciseWithRest("Squats", sets = "2", restSecs = "5")
        startGraceAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("btn_skip").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("running_status_label").assertTextContains("REST")
    }

    // ── COMPLETE phase ────────────────────────────────────────────────────────

    @Test
    fun completePhase_showsDoneHeading() {
        addStrengthExercise("Squats", sets = "1")
        startGraceAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        composeTestRule.waitUntil(4_000) {
            composeTestRule.onAllNodesWithTag("screen_complete").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("complete_heading").assertTextEquals("Done.")
    }

    @Test
    fun completePhase_showsExerciseName() {
        addStrengthExercise("Mountain Climbers", sets = "1")
        startGraceAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        composeTestRule.waitUntil(4_000) {
            composeTestRule.onAllNodesWithTag("screen_complete").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Mountain Climbers")
            .assertIsDisplayed()
    }

    @Test
    fun completePhase_backToRoutine_returnsToEditing() {
        addStrengthExercise("Squats", sets = "1")
        startGraceAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()

        composeTestRule.waitUntil(4_000) {
            composeTestRule.onAllNodesWithTag("screen_complete").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("btn_back_to_routine").performClick()

        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithText("Add Exercise").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("exercise_form_sheet").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun addStrengthExercise(name: String, sets: String = "") {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        if (sets.isNotBlank()) {
            composeTestRule.onNode(hasText("Sets").and(hasSetTextAction()))
                .performTextInput(sets)
        }
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()
    }

    private fun addExerciseWithRest(name: String, sets: String, restSecs: String) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction()))
            .performTextInput(sets)
        // Rest field has placeholder "0"
        composeTestRule.onAllNodes(hasText("0").and(hasSetTextAction()))
            .onFirst()
            .performTextInput(restSecs)
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()
    }

    /**
     * Starts the grace period, pauses immediately so the countdown won't expire
     * during test setup, then waits for the RUNNING screen by skipping grace via
     * the pause/cancel trick: we cancel grace and re-enter with a 0-second workaround.
     *
     * Simpler approach: use the existing grace→running auto-advance.
     * With the default 10s grace period the test must wait 10 s. To avoid that,
     * the Settings sheet can be opened to set grace to 3 s, but that takes extra
     * steps. Instead we rely on waitUntil with a sufficient timeout.
     */
    private fun startGraceAndWaitForRunning() {
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        // Wait up to 15 s for the RUNNING phase to appear (grace period ≤ 10 s + buffer)
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithTag("screen_running").fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Adds one exercise then waits for the workout to reach the RUNNING phase. */
    private fun startWorkoutAndSkipGrace(name: String, sets: String = "1") {
        addStrengthExercise(name, sets)
        startGraceAndWaitForRunning()
    }
}


