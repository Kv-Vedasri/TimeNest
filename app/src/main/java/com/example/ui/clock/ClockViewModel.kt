package com.example.ui.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.TimeFormatter
import com.example.data.repository.TimeNestRepository
import com.example.domain.model.ClockBackdrop
import com.example.domain.model.ClockFont
import com.example.domain.model.ClockStyle
import com.example.domain.model.TimeSnapshot
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClockUiState(
  val timeSnapshot: TimeSnapshot = TimeFormatter.buildLocalSnapshot(System.currentTimeMillis(), false),
  val preferences: UserPreferences = UserPreferences(),
  val isNightstandMode: Boolean = false,
  val nightstandDimmed: Boolean = false,
)

class ClockViewModel(private val repository: TimeNestRepository) : ViewModel() {

  private val isNightstand = MutableStateFlow(false)
  private val nightstandDimmed = MutableStateFlow(false)

  val uiState: StateFlow<ClockUiState> =
    combine(repository.tickerFlow, repository.preferencesFlow, isNightstand, nightstandDimmed) {
        epoch,
        prefs,
        nightstand,
        dimmed ->
        ClockUiState(
          timeSnapshot =
            TimeFormatter.buildLocalSnapshot(
              epochMillis = epoch,
              use24Hour = prefs.use24HourFormat,
              timeZoneId = prefs.displayTimeZoneId,
            ),
          preferences = prefs,
          isNightstandMode = nightstand,
          nightstandDimmed = dimmed,
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ClockUiState(),
      )

  fun setClockStyle(style: ClockStyle) {
    viewModelScope.launch { repository.setClockStyle(style) }
  }

  fun setClockFont(font: ClockFont) {
    viewModelScope.launch { repository.setClockFont(font) }
  }

  fun setClockBackdrop(backdrop: ClockBackdrop) {
    viewModelScope.launch { repository.setClockBackdrop(backdrop) }
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

  fun setKeepScreenOnNightstand(keepOn: Boolean) {
    viewModelScope.launch { repository.setKeepScreenOnNightstand(keepOn) }
  }

  fun setHourlyChimeEnabled(enabled: Boolean) {
    viewModelScope.launch { repository.setHourlyChimeEnabled(enabled) }
  }

  fun enterNightstand() {
    isNightstand.value = true
    nightstandDimmed.value = false
  }

  fun exitNightstand() {
    isNightstand.value = false
    nightstandDimmed.value = false
  }

  fun toggleNightstandDim() {
    nightstandDimmed.value = !nightstandDimmed.value
  }

  class Factory(private val repository: TimeNestRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ClockViewModel(repository) as T
  }
}
