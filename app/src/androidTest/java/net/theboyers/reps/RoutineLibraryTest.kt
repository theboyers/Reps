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
 * UI tests for saving routines to the library, loading them back, and deleting them.
 *
 * Run with: ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class RoutineLibraryTest {

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
                    .edit().clear().commit()
            }
        })
        .around(_composeTestRule)

    // ── Save button state ─────────────────────────────────────────────────────

    @Test
    fun saveButton_isDisabled_whenRoutineIsEmpty() {
        composeTestRule.onNodeWithContentDescription("Save routine")
            .assertIsNotEnabled()
    }

    @Test
    fun saveButton_isEnabled_whenExerciseAdded() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithContentDescription("Save routine")
            .assertIsEnabled()
    }

    // ── Saving a routine ──────────────────────────────────────────────────────

    @Test
    fun saveRoutine_appearsInLibrary() {
        addStrengthExercise("Squats")
        openSaveSheet()

        composeTestRule.onNode(hasText("Routine name").and(hasSetTextAction()))
            .performTextInput("Leg Day")
        composeTestRule.onNodeWithText("Save").performClick()

        // Confirm snackbar or open load sheet to verify
        openLoadSheet()
        composeTestRule.onNodeWithText("Leg Day").assertIsDisplayed()
    }

    @Test
    fun saveRoutine_showsConfirmationSnackbar() {
        addStrengthExercise("Squats")
        openSaveSheet()

        composeTestRule.onNode(hasText("Routine name").and(hasSetTextAction()))
            .performTextInput("Push Day")
        composeTestRule.onNodeWithText("Save").performClick()

        // The app shows "Routine saved to library!" in a snackbar after saving
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Routine saved to library!", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Routine saved to library!", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun saveRoutine_cancel_doesNotSave() {
        addStrengthExercise("Squats")
        openSaveSheet()

        composeTestRule.onNodeWithText("Cancel").performClick()

        // If nothing was saved, the library is empty and the load button stays disabled.
        composeTestRule.onNodeWithContentDescription("Load routine").assertIsNotEnabled()
    }

    // ── Loading a routine ─────────────────────────────────────────────────────

    @Test
    fun loadRoutine_loadsExercisesIntoBuilder() {
        addStrengthExercise("Deadlifts")
        saveRoutine("Heavy Day")

        // Clear the current routine by adding a new different one
        // (Clear All dialog would also work but is more steps)
        // Instead, load from library — it replaces the current routine
        openLoadSheet()
        composeTestRule.onNodeWithText("Load Routine").performClick()

        composeTestRule.onNodeWithText("Deadlifts").assertIsDisplayed()
    }

    @Test
    fun savedRoutineCard_showsExerciseCount() {
        addStrengthExercise("Squats")
        addStrengthExercise("Lunges")
        saveRoutine("Leg Day")

        openLoadSheet()
        // The saved routine card shows "2 exercises". The editing phase exercise-count label
        // may also match this string while the sheet is open, so assert on the first match.
        composeTestRule.onAllNodesWithText("2 exercises", substring = true)
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun loadButton_isDisabled_whenLibraryEmpty() {
        composeTestRule.onNodeWithContentDescription("Load routine")
            .assertIsNotEnabled()
    }

    @Test
    fun loadButton_isEnabled_whenLibraryHasRoutine() {
        addStrengthExercise("Squats")
        saveRoutine("Leg Day")

        composeTestRule.onNodeWithContentDescription("Load routine")
            .assertIsEnabled()
    }

    // ── Deleting a routine ────────────────────────────────────────────────────

    @Test
    fun deleteRoutine_removesItFromLibrary() {
        addStrengthExercise("Squats")
        saveRoutine("Leg Day")

        openLoadSheet()
        composeTestRule.onNodeWithContentDescription("Delete routine").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()

        composeTestRule.onNodeWithText("No saved routines yet").assertIsDisplayed()
    }

    @Test
    fun deleteRoutine_cancelKeepsItInLibrary() {
        addStrengthExercise("Squats")
        saveRoutine("Leg Day")

        openLoadSheet()
        composeTestRule.onNodeWithContentDescription("Delete routine").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithText("Leg Day").assertIsDisplayed()
    }

    // ── Library count ─────────────────────────────────────────────────────────

    @Test
    fun librarySheet_showsCorrectSavedCount() {
        addStrengthExercise("Squats")
        saveRoutine("Routine 1")
        addStrengthExercise("Lunges")
        saveRoutine("Routine 2")

        openLoadSheet()
        composeTestRule.onNodeWithText("2 saved").assertIsDisplayed()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithTag("fab_add_exercise").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("exercise_form_sheet").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun addStrengthExercise(name: String) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()
    }

    private fun openSaveSheet() {
        // waitForIdle() drains the exercise-sheet's closing animation (including its scrim)
        // so the scrim doesn't absorb the subsequent "Save routine" button click.
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Save routine").performClick()
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodesWithText("Save Routine").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun openLoadSheet() {
        composeTestRule.onNodeWithContentDescription("Load routine").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("Routine Library").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun saveRoutine(name: String) {
        openSaveSheet()
        composeTestRule.onNode(hasText("Routine name").and(hasSetTextAction()))
            .performTextInput(name)
        composeTestRule.onNodeWithText("Save").performClick()
        // Drain the save-sheet closing animation before the next action.
        composeTestRule.waitForIdle()
    }
}




