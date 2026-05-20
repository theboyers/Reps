package net.theboyers.reps

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import net.theboyers.reps.ui.theme.RepsTheme

/** Entry point. Reads the saved theme preference and hands it to [RoutineTrackerScreen]. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("reps_prefs", Context.MODE_PRIVATE)
        setContent {
            var themePreference by remember {
                mutableStateOf(prefs.getString("pref_theme", "system") ?: "system")
            }
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themePreference) {
                "dark"  -> true
                "light" -> false
                else    -> systemDark
            }
            RepsTheme(darkTheme = darkTheme, dynamicColor = false) {
                RoutineTrackerScreen(onThemeChange = { themePreference = it })
            }
        }
    }
}
