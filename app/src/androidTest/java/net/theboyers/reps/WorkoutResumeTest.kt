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
 * Tests process-death recovery: when a workout was in progress, launching the
 * app offers to resume it, and the working routine is restored either way.
 */
@RunWith(AndroidJUnit4::class)
class WorkoutResumeTest {

    private val _composeTestRule = createAndroidComposeRule<MainActivity>()
    val composeTestRule get() = _composeTestRule

    @get:Rule
    val chain: RuleChain = RuleChain
        .outerRule(object : ExternalResource() {
            override fun before() {
                // Seed a saved draft (as if the app was killed mid-workout).
                val draft = """
                    {"entries":[{"exerciseName":"Squats","details":"","sets":"1",
                    "repsOrDuration":"10","restBetweenSetsSeconds":0,"type":"STRENGTH","isPerSide":false}],
                    "restBetweenExercisesSeconds":0,"phase":"RUNNING","currentStepIndex":0,
                    "workoutStartTimeMs":0,"isPaused":false,"completedSets":[]}
                """.trimIndent().replace("\n", "")
                InstrumentationRegistry.getInstrumentation().targetContext
                    .getSharedPreferences("reps_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .putInt("pref_grace_period", 3)
                    .putString("workout_draft", draft)
                    .commit()
            }
        })
        .around(_composeTestRule)

    @Test
    fun unfinishedWorkout_promptsToResume_thenResumes() {
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Resume workout?").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("btn_confirm_resume").performClick()

        composeTestRule.onNodeWithTag("screen_running").assertIsDisplayed()
        composeTestRule.onNodeWithTag("current_exercise_name").assertTextEquals("Squats")
    }

    @Test
    fun unfinishedWorkout_discard_returnsToEditingWithRoutineIntact() {
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Resume workout?").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("btn_discard_resume").performClick()

        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Squats").assertIsDisplayed()
    }
}
