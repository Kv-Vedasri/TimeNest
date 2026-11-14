package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.UserPreferences
import com.example.ui.navigation.TimeNestShell
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.theme.TimeNestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val repository = (application as TimeNestApp).container.repository

    setContent {
      val prefsOrNull: UserPreferences? by
        repository.preferencesFlow.collectAsStateWithLifecycle(initialValue = null)
      val scope = rememberCoroutineScope()

      val activePrefs = prefsOrNull ?: UserPreferences()

      TimeNestTheme(
        themeMode = activePrefs.themeMode,
        accentPreset = activePrefs.accentPreset,
      ) {
        if (prefsOrNull == null) {
          Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        } else if (!activePrefs.hasCompletedOnboarding) {
          OnboardingScreen(
            preferences = activePrefs,
            onSelectTheme = { mode -> scope.launch { repository.setThemeMode(mode) } },
            onToggle24Hour = { use24h -> scope.launch { repository.setUse24HourFormat(use24h) } },
            onComplete = { scope.launch { repository.setOnboardingCompleted(true) } },
          )
        } else {
          TimeNestShell(repository = repository)
        }
      }
    }
  }
}
