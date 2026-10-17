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
 * UI tests for the "Play sounds with screen off" setting that controls the
 * foreground audio service.
 */
@RunWith(AndroidJUnit4::class)
class AudioSettingsTest {

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

    @Test
    fun settings_showBackgroundAudioToggle() {
        openSettings()
        composeTestRule.onNodeWithText("Play sounds with screen off")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun backgroundAudioToggle_isOnByDefault() {
        openSettings()
        composeTestRule.onNodeWithTag("switch_background_audio")
            .performScrollTo()
            .assertIsOn()
    }

    @Test
    fun backgroundAudioToggle_canBeToggledOff() {
        openSettings()
        composeTestRule.onNodeWithTag("switch_background_audio")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("switch_background_audio").assertIsOff()
    }

    private fun openSettings() {
        composeTestRule.onNodeWithContentDescription("Routine settings").performClick()
        composeTestRule.waitUntil(3_000) {
            composeTestRule.onAllNodesWithText("App Settings").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
