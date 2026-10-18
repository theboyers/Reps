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
 * UI tests for the end-of-workout summary, the workout note, and saving to
 * history (then viewing it).
 */
@RunWith(AndroidJUnit4::class)
class WorkoutSummaryTest {

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

    @Test
    fun completeScreen_showsStatsNoteAndSaveButton() {
        completeWorkout(weight = "135", reps = "10")

        composeTestRule.onNodeWithTag("stat_volume").assertIsDisplayed()
        composeTestRule.onNodeWithTag("field_workout_note").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_save_history").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun saveToHistory_appearsInHistoryWithNote() {
        completeWorkout(weight = "135", reps = "10")

        composeTestRule.onNodeWithTag("field_workout_note").performScrollTo()
            .performTextInput("Machine was busy, good session")
        composeTestRule.onNodeWithTag("btn_save_history").performScrollTo().performClick()

        composeTestRule.onNodeWithTag("btn_back_to_routine").performScrollTo().performClick()

        composeTestRule.onNodeWithContentDescription("Workout history").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Workout History").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("history_note")
            .assertTextEquals("Machine was busy, good session")
    }

    @Test
    fun historyEntry_canBeDeleted() {
        saveWorkoutWithNote("Rest day notes")
        openHistory()

        composeTestRule.onNodeWithTag("btn_delete_history_entry").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()

        composeTestRule.onNodeWithText("No workouts saved yet").assertIsDisplayed()
    }

    @Test
    fun history_canBeCleared() {
        saveWorkoutWithNote("Rest day notes")
        openHistory()

        composeTestRule.onNodeWithTag("btn_clear_history").performClick()
        composeTestRule.onNodeWithTag("btn_confirm_clear_history").performClick()

        composeTestRule.onNodeWithText("No workouts saved yet").assertIsDisplayed()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun saveWorkoutWithNote(note: String) {
        completeWorkout(weight = "135", reps = "10")
        composeTestRule.onNodeWithTag("field_workout_note").performScrollTo().performTextInput(note)
        composeTestRule.onNodeWithTag("btn_save_history").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("btn_back_to_routine").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    private fun openHistory() {
        composeTestRule.onNodeWithContentDescription("Workout history").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Workout History").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithTag("fab_add_exercise").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("exercise_form_sheet").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun addStrength(
        name: String = "Squats",
        sets: String = "1",
        reps: String? = null,
        weight: String? = null
    ) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput(sets)
        if (reps != null) composeTestRule.onNodeWithTag("field_reps").performTextInput(reps)
        if (weight != null) {
            composeTestRule.onNodeWithTag("field_weight").performScrollTo().performTextInput(weight)
        }
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    private fun completeWorkout(weight: String? = null, reps: String? = null) {
        addStrength(weight = weight, reps = reps)
        composeTestRule.onNodeWithTag("btn_start_workout").performClick()
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithTag("screen_running").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("btn_done").performScrollTo().performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("screen_complete").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
