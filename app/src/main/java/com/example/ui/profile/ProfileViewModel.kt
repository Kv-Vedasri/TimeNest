package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.TimeNestRepository
import com.example.data.repository.TodayProductivitySummary
import com.example.domain.model.AccentPreset
import com.example.domain.model.ClockFont
import com.example.domain.model.DashboardCardType
import com.example.domain.model.ThemeMode
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
  val preferences: UserPreferences = UserPreferences(),
  val todaySummary: TodayProductivitySummary = TodayProductivitySummary(),
  val bestReactionMs: Int? = null,
)

class ProfileViewModel(private val repository: TimeNestRepository) : ViewModel() {

  val uiState: StateFlow<ProfileUiState> =
    combine(
        repository.preferencesFlow,
        repository.observeTodaySummary(),
        repository.observeBestReactionMs(),
      ) { prefs, summary, best ->
        ProfileUiState(preferences = prefs, todaySummary = summary, bestReactionMs = best)
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(),
      )

  fun setThemeMode(mode: ThemeMode) {
    viewModelScope.launch { repository.setThemeMode(mode) }
  }

  fun setAccentPreset(accent: AccentPreset) {
    viewModelScope.launch { repository.setAccentPreset(accent) }
  }

  fun setUse24HourFormat(use24h: Boolean) {
    viewModelScope.launch { repository.setUse24HourFormat(use24h) }
  }

  fun setShowSeconds(show: Boolean) {
    viewModelScope.launch { repository.setShowSeconds(show) }
  }

  fun setDisplayTimeZoneId(zoneId: String) {
    viewModelScope.launch { repository.setDisplayTimeZoneId(zoneId) }
  }

  fun setClockFont(font: ClockFont) {
    viewModelScope.launch { repository.setClockFont(font) }
  }

  fun setKeepScreenOnNightstand(keepOn: Boolean) {
    viewModelScope.launch { repository.setKeepScreenOnNightstand(keepOn) }
  }

  fun setDailyFocusGoal(minutes: Int) {
    viewModelScope.launch { repository.setDailyFocusGoalMinutes(minutes) }
  }

  fun resetDashboardLayout() {
    viewModelScope.launch {
      repository.setDashboardLayout(DashboardCardType.defaultOrder, emptySet())
    }
  }

  fun replayOnboarding() {
    viewModelScope.launch { repository.setOnboardingCompleted(false) }
  }

  class Factory(private val repository: TimeNestRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(repository) as T
  }
}
