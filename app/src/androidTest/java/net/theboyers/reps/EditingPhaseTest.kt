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
 * UI tests for the EDITING phase: adding, editing, and deleting exercises,
 * the empty state, and the Start Workout button becoming available.
 *
 * Run with: ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class EditingPhaseTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun clearSharedPreferences() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("reps_prefs", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    // ── initial state ─────────────────────────────────────────────────────────

    @Test
    fun appStartsInEditingPhase() {
        composeTestRule.onNodeWithTag("screen_editing").assertIsDisplayed()
    }

    @Test
    fun emptyStateTextIsVisible_whenNoExercises() {
        composeTestRule.onNodeWithTag("empty_state_text").assertIsDisplayed()
    }

    @Test
    fun startWorkoutButton_isNotVisible_whenNoExercises() {
        composeTestRule.onNodeWithTag("btn_start_workout").assertDoesNotExist()
    }

    @Test
    fun topBarShowsRepsTitle() {
        composeTestRule.onNodeWithText("Reps").assertIsDisplayed()
    }

    // ── adding a Strength exercise ────────────────────────────────────────────

    @Test
    fun addStrengthExercise_appearsInList() {
        addStrengthExercise("Pull-ups")
        composeTestRule.onNodeWithText("Pull-ups").assertIsDisplayed()
    }

    @Test
    fun addExercise_hidesEmptyState() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("empty_state_text").assertDoesNotExist()
    }

    @Test
    fun addExercise_showsStartWorkoutButton() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithTag("btn_start_workout").assertIsDisplayed()
    }

    @Test
    fun addTwoExercises_bothAppearInList() {
        addStrengthExercise("Squats")
        addStrengthExercise("Lunges")
        composeTestRule.onNodeWithText("Squats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lunges").assertIsDisplayed()
    }

    @Test
    fun exerciseCountLabel_updatesAfterAdd() {
        addStrengthExercise("Squats")
        composeTestRule.onNodeWithText("1 exercise").assertIsDisplayed()
        addStrengthExercise("Lunges")
        composeTestRule.onNodeWithText("2 exercises").assertIsDisplayed()
    }

    // ── adding a Timed exercise ───────────────────────────────────────────────

    @Test
    fun addTimedExercise_appearsInList() {
        openAddExerciseSheet()
        composeTestRule.onNodeWithText("Timed").performClick()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput("Plank")
        composeTestRule.onNode(hasText("Sets").and(hasSetTextAction())).performTextInput("3")
        composeTestRule.onNode(hasText("Duration").and(hasSetTextAction())).performTextInput("45")
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()

        composeTestRule.onNodeWithText("Plank").assertIsDisplayed()
    }

    // ── adding a Cardio exercise ──────────────────────────────────────────────

    @Test
    fun addCardioExercise_appearsInList() {
        openAddExerciseSheet()
        composeTestRule.onNodeWithText("Cardio").performClick()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput("Treadmill")
        composeTestRule.onNode(hasText("Duration").and(hasSetTextAction())).performTextInput("20")
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()

        composeTestRule.onNodeWithText("Treadmill").assertIsDisplayed()
    }

    // ── editing exercises ─────────────────────────────────────────────────────

    @Test
    fun editExercise_updatesNameInList() {
        addStrengthExercise("Squats")

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Edit").performClick()

        composeTestRule.onNodeWithTag("field_exercise_name").performTextClearance()
        composeTestRule.onNodeWithTag("field_exercise_name")
            .performTextInput("Romanian Deadlifts")

        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()

        composeTestRule.onNodeWithText("Romanian Deadlifts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Squats").assertDoesNotExist()
    }

    // ── deleting exercises ────────────────────────────────────────────────────

    @Test
    fun deleteExercise_removesItFromList() {
        addStrengthExercise("Squats")

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        composeTestRule.onNodeWithText("Remove").performClick()

        composeTestRule.onNodeWithText("Squats").assertDoesNotExist()
    }

    @Test
    fun deleteOnlyExercise_showsEmptyState() {
        addStrengthExercise("Squats")

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        composeTestRule.onNodeWithText("Remove").performClick()

        composeTestRule.onNodeWithTag("empty_state_text").assertIsDisplayed()
    }

    @Test
    fun deleteDialog_cancelKeepsExercise() {
        addStrengthExercise("Squats")

        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithText("Squats").assertIsDisplayed()
    }

    // ── exercise form validation ──────────────────────────────────────────────

    @Test
    fun submitWithBlankName_showsValidationError() {
        openAddExerciseSheet()
        // Do not type a name — submit immediately
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()
        composeTestRule.onNodeWithText("Exercise name is required.").assertIsDisplayed()
    }

    @Test
    fun submitTimedWithBlankDuration_showsValidationError() {
        openAddExerciseSheet()
        composeTestRule.onNodeWithText("Timed").performClick()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput("Plank")
        // Don't fill in duration
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()
        composeTestRule.onNodeWithText("Duration is required.").assertIsDisplayed()
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun openAddExerciseSheet() {
        composeTestRule.onNodeWithText("Add Exercise").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithTag("exercise_form_sheet").fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Adds a basic Strength exercise with just a name (sets may be blank — that's valid). */
    private fun addStrengthExercise(name: String) {
        openAddExerciseSheet()
        composeTestRule.onNodeWithTag("field_exercise_name").performTextInput(name)
        composeTestRule.onNodeWithTag("btn_submit_exercise").performClick()
    }
}

