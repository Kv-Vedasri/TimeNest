package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.WorldCityEntity
import com.example.data.repository.TimeFormatter
import com.example.data.repository.TimeNestRepository
import com.example.data.repository.TodayProductivitySummary
import com.example.data.repository.WorldZoneInfo
import com.example.domain.model.DashboardCardType
import com.example.domain.model.TimeSnapshot
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
  val timeSnapshot: TimeSnapshot = TimeFormatter.buildLocalSnapshot(System.currentTimeMillis(), false),
  val preferences: UserPreferences = UserPreferences(),
  val worldCities: List<Pair<WorldCityEntity, WorldZoneInfo>> = emptyList(),
  val todaySummary: TodayProductivitySummary = TodayProductivitySummary(),
  val quickTimerTotalSec: Int = 300,
  val quickTimerRemainingSec: Int = 300,
  val quickTimerRunning: Boolean = false,
  val quickTimerCompletedNotice: Boolean = false,
)

class HomeViewModel(private val repository: TimeNestRepository) : ViewModel() {

  private val quickTimerTotal = MutableStateFlow(300)
  private val quickTimerRemaining = MutableStateFlow(300)
  private val quickTimerRunning = MutableStateFlow(false)
  private val quickTimerDone = MutableStateFlow(false)
  private var timerJob: Job? = null

  init {
    viewModelScope.launch { repository.ensureDefaultWorldCities() }
  }

  private val quickTimerFlow =
    combine(quickTimerTotal, quickTimerRemaining, quickTimerRunning, quickTimerDone) {
      total,
      remaining,
      running,
      done ->
      Quadruple(total, remaining, running, done)
    }

  val uiState: StateFlow<HomeUiState> =
    combine(
        repository.tickerFlow,
        repository.preferencesFlow,
        repository.worldCitiesFlow,
        repository.observeTodaySummary(),
        quickTimerFlow,
      ) { epoch, prefs, cities, summary, timerState ->
        val snapshot =
          TimeFormatter.buildLocalSnapshot(
            epochMillis = epoch,
            use24Hour = prefs.use24HourFormat,
            timeZoneId = prefs.displayTimeZoneId,
          )
        val formattedCities =
          cities.map { entity ->
            entity to
              TimeFormatter.formatWorldZone(
                zoneId = entity.zoneId,
                cityName = entity.cityName,
                regionLabel = entity.regionLabel,
                epochMillis = epoch,
                use24Hour = prefs.use24HourFormat,
                baseTimeZoneId = prefs.displayTimeZoneId,
              )
          }
        HomeUiState(
          timeSnapshot = snapshot,
          preferences = prefs,
          worldCities = formattedCities,
          todaySummary = summary,
          quickTimerTotalSec = timerState.first,
          quickTimerRemainingSec = timerState.second,
          quickTimerRunning = timerState.third,
          quickTimerCompletedNotice = timerState.fourth,
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(),
      )

  fun startQuickTimerPreset(minutes: Int) {
    val secs = minutes * 60
    timerJob?.cancel()
    quickTimerTotal.value = secs
    quickTimerRemaining.value = secs
    quickTimerDone.value = false
    runTimerLoop()
  }

  fun toggleQuickTimer() {
    if (quickTimerRunning.value) {
      timerJob?.cancel()
      quickTimerRunning.value = false
    } else {
      if (quickTimerRemaining.value <= 0) {
        quickTimerRemaining.value = quickTimerTotal.value
      }
      quickTimerDone.value = false
      runTimerLoop()
    }
  }

  fun resetQuickTimer() {
    timerJob?.cancel()
    quickTimerRunning.value = false
    quickTimerDone.value = false
    quickTimerRemaining.value = quickTimerTotal.value
  }

  fun dismissQuickTimerDone() {
    quickTimerDone.value = false
  }

  private fun runTimerLoop() {
    timerJob?.cancel()
    quickTimerRunning.value = true
    timerJob =
      viewModelScope.launch {
        while (quickTimerRemaining.value > 0 && quickTimerRunning.value) {
          delay(1000L)
          if (!quickTimerRunning.value) break
          quickTimerRemaining.value = (quickTimerRemaining.value - 1).coerceAtLeast(0)
        }
        if (quickTimerRemaining.value == 0 && quickTimerRunning.value) {
          quickTimerRunning.value = false
          quickTimerDone.value = true
          repository.logTimerCompletion(quickTimerTotal.value, "Home Quick Timer")
        }
      }
  }

  fun toggle24HourFormat() {
    viewModelScope.launch {
      repository.setUse24HourFormat(!uiState.value.preferences.use24HourFormat)
    }
  }

  fun toggleDashboardCardVisibility(card: DashboardCardType) {
    viewModelScope.launch {
      val prefs = uiState.value.preferences
      val nextHidden = prefs.hiddenDashboardCards.toMutableSet()
      if (card in nextHidden) nextHidden.remove(card) else nextHidden.add(card)
      repository.setDashboardLayout(prefs.dashboardCardOrder, nextHidden)
    }
  }

  fun moveDashboardCard(card: DashboardCardType, moveUp: Boolean) {
    viewModelScope.launch {
      val prefs = uiState.value.preferences
      val list = prefs.dashboardCardOrder.toMutableList()
      val index = list.indexOf(card)
      if (index == -1) return@launch
      val target = if (moveUp) index - 1 else index + 1
      if (target !in list.indices) return@launch
      val swapped = list[target]
      list[target] = card
      list[index] = swapped
      repository.setDashboardLayout(list, prefs.hiddenDashboardCards)
    }
  }

  fun resetDashboardLayout() {
    viewModelScope.launch {
      repository.setDashboardLayout(DashboardCardType.defaultOrder, emptySet())
    }
  }

  fun addWorldCity(zoneId: String, cityName: String, regionLabel: String) {
    viewModelScope.launch { repository.addWorldCity(zoneId, cityName, regionLabel) }
  }

  fun removeWorldCity(id: Long) {
    viewModelScope.launch { repository.removeWorldCity(id) }
  }

  fun updateWakeTarget(hour: Int, minute: Int, enabled: Boolean) {
    viewModelScope.launch { repository.setWakeTarget(hour, minute, enabled) }
  }

  class Factory(private val repository: TimeNestRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
