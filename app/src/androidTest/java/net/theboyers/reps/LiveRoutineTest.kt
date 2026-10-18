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
 * UI tests for the live-routine running view: exercises complete on the routine
 * cards themselves, and rests count down inline on the active card.
 */
@RunWith(AndroidJUnit4::class)
class LiveRoutineTest {

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
    fun completedExercise_isMarkedDoneAndNextBecomesCurrent() {
        addStrength("Squats", sets = "1")
        addStrength("Lunges", sets = "1")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Squats")
        composeTestRule.onNodeWithTag("btn_done").performClick()

        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Lunges")
        composeTestRule.onNodeWithContentDescription("Complete").assertExists()
    }

    @Test
    fun restBetweenSets_showsInlineCountdownOnTheCard() {
        addStrengthWithRest("Squats", sets = "2", restSecs = "10")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Skip Rest").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("inline_rest_label").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_skip").assertIsDisplayed()
    }

    @Test
    fun restCard_hasTimerAdjustButtons() {
        addStrengthWithRest("Squats", sets = "2", restSecs = "30")
        startWorkoutAndWaitForRunning()

        composeTestRule.onNodeWithTag("btn_done").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("btn_skip").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("btn_minus_10").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_plus_10").assertIsDisplayed()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithTag("fab_add_exercise").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("exercise_form_sheet").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun addStrength(name: String, sets: String = "1") {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput(sets)
        composeTestRule.onNodeWithTag("btn_submit_exercise").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    private fun addStrengthWithRest(name: String, sets: String, restSecs: String) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput(sets)
        composeTestRule.onNodeWithTag("field_strength_rest").performScrollTo().performTextClearance()
        composeTestRule.onNodeWithTag("field_strength_rest").performTextInput(restSecs)
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
