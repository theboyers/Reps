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

    // Cleared before the activity starts so every test sees empty prefs
    // without needing a mid-test recreate().
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
                    // Use a 3-second grace period so startGraceAndWaitForRunning()
                    // needs only ~3 s instead of the default 10 s, keeping the test
                    // reliable even when the device is under load.
                    .putInt("pref_grace_period", 3)
                    .commit()
            }
        })
        .around(_composeTestRule)

    // ── GRACE phase ───────────────────────────────────────────────────────────

    @Test
    fun startWorkout_transitionsToGracePhase() {
        addTimedExercise("Plank")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        // Grace is now an in-card countdown on the live routine screen.
        composeTestRule.onNodeWithTag("grace_countdown").assertIsDisplayed()
        composeTestRule.onNodeWithTag("running_status_label").assertTextEquals("READY")
    }

    @Test
    fun gracePhase_showsGetReadyHeading() {
        addTimedExercise("Plank")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.onNodeWithText("Get ready!").assertIsDisplayed()
    }

    @Test
    fun gracePhase_showsCountdownNumber() {
        addTimedExercise("Plank")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.onNodeWithTag("grace_countdown").assertIsDisplayed()
    }

    @Test
    fun gracePhase_pauseButton_pausesCountdown() {
        addTimedExercise("Plank")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        composeTestRule.onNodeWithTag("btn_pause_continue").performClick()

        composeTestRule.onNodeWithText("Paused").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_pause_continue")
            .assertTextEquals("Continue")
    }

    @Test
    fun gracePhase_cancelButton_returnsToEditing() {
        addTimedExercise("Plank")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
    }

    @Test
    fun gracePhase_listShowsExercisesInWorkout() {
        addTimedExercise("Plank")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        // The routine list is the workout screen — the exercise is shown live.
        composeTestRule.onAllNodesWithText("Plank")
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun repBasedFirstExercise_skipsGraceAndStartsImmediately() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.onNodeWithTag("screen_running").assertIsDisplayed()
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
        // Confirmation is required before leaving the workout.
        composeTestRule.onNodeWithText("End workout?").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_confirm_end_workout").performClick()

        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
    }

    @Test
    fun runningPhase_endWorkout_keepGoing_staysInWorkout() {
        startWorkoutAndSkipGrace("Squats")

        composeTestRule.onNodeWithTag("btn_end_workout").performClick()
        composeTestRule.onNodeWithText("Keep going").performClick()

        composeTestRule.onNodeWithTag("screen_running").assertIsDisplayed()
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
            .performScrollTo()
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
        composeTestRule.onNodeWithTag("btn_back_to_routine").performScrollTo().performClick()

        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithTag("fab_add_exercise").performClick()
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
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
    }

    private fun addExerciseWithRest(name: String, sets: String, restSecs: String) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction()))
            .performTextInput(sets)
        // The rest field defaults to 90s, so clear it before entering the test value.
        composeTestRule.onNodeWithTag("field_strength_rest").performScrollTo().performTextClearance()
        composeTestRule.onNodeWithTag("field_strength_rest").performTextInput(restSecs)
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
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
    /** Adds a Timed exercise (so the workout starts with a countdown / grace period). */
    private fun addTimedExercise(name: String) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithText("Timed").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput("3")
        composeTestRule.onNode(hasText("Duration").and(hasSetTextAction())).performTextInput("30")
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    private fun startGraceAndWaitForRunning() {
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()

        // Grace period is set to 3 s in @Before; allow 10 s total for the transition.
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("screen_running").fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Adds one exercise then waits for the workout to reach the RUNNING phase. */
    private fun startWorkoutAndSkipGrace(name: String, sets: String = "1") {
        addStrengthExercise(name, sets)
        startGraceAndWaitForRunning()
    }
}


