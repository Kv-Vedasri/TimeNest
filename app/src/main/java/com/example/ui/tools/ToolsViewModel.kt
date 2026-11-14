package com.example.ui.tools

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.TimeNestRepository
import com.example.data.repository.TodayProductivitySummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ToolTab(val label: String) {
  TIMER("Timer"),
  STOPWATCH("Stopwatch"),
  FOCUS("Focus"),
}

data class StopwatchLap(val index: Int, val lapMillis: Long, val totalMillis: Long)

data class ToolsUiState(
  val activeTab: ToolTab = ToolTab.TIMER,
  // Countdown Timer
  val timerLabel: String = "Countdown Timer",
  val timerTotalSeconds: Int = 300,
  val timerRemainingSeconds: Int = 300,
  val timerRunning: Boolean = false,
  val timerCompletedMessage: String? = null,
  // Stopwatch
  val stopwatchElapsedMillis: Long = 0L,
  val stopwatchRunning: Boolean = false,
  val stopwatchLaps: List<StopwatchLap> = emptyList(),
  // Focus Session
  val focusCategory: String = "Deep Work",
  val focusTotalSeconds: Int = 1500,
  val focusRemainingSeconds: Int = 1500,
  val focusRunning: Boolean = false,
  val focusCompletedMessage: String? = null,
  val todaySummary: TodayProductivitySummary = TodayProductivitySummary(),
)

class ToolsViewModel(private val repository: TimeNestRepository) : ViewModel() {

  private val _state = MutableStateFlow(ToolsUiState())
  val uiState: StateFlow<ToolsUiState> =
    combine(_state, repository.observeTodaySummary()) { state, summary ->
        state.copy(todaySummary = summary)
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ToolsUiState(),
      )

  private var timerJob: Job? = null
  private var stopwatchJob: Job? = null
  private var focusJob: Job? = null

  private var stopwatchBaseMs: Long = 0L
  private var stopwatchAccumulatedMs: Long = 0L
  private var lastLapTotalMs: Long = 0L

  fun selectTab(tab: ToolTab) {
    _state.update { it.copy(activeTab = tab) }
  }

  // --- Countdown Timer ---
  fun setTimerPreset(minutes: Int, label: String) {
    timerJob?.cancel()
    val secs = (minutes * 60).coerceAtLeast(10)
    _state.update {
      it.copy(
        timerLabel = label,
        timerTotalSeconds = secs,
        timerRemainingSeconds = secs,
        timerRunning = false,
        timerCompletedMessage = null,
      )
    }
  }

  fun adjustTimerMinutes(deltaMinutes: Int) {
    if (_state.value.timerRunning) return
    val currentMins = (_state.value.timerTotalSeconds / 60).coerceAtLeast(1)
    val nextMins = (currentMins + deltaMinutes).coerceIn(1, 180)
    val secs = nextMins * 60
    _state.update {
      it.copy(
        timerTotalSeconds = secs,
        timerRemainingSeconds = secs,
        timerCompletedMessage = null,
      )
    }
  }

  fun toggleTimer() {
    if (_state.value.timerRunning) {
      timerJob?.cancel()
      _state.update { it.copy(timerRunning = false) }
    } else {
      if (_state.value.timerRemainingSeconds <= 0) {
        _state.update { it.copy(timerRemainingSeconds = it.timerTotalSeconds) }
      }
      _state.update { it.copy(timerRunning = true, timerCompletedMessage = null) }
      timerJob?.cancel()
      timerJob =
        viewModelScope.launch {
          while (_state.value.timerRemainingSeconds > 0 && _state.value.timerRunning) {
            delay(1000L)
            if (!_state.value.timerRunning) break
            _state.update {
              it.copy(timerRemainingSeconds = (it.timerRemainingSeconds - 1).coerceAtLeast(0))
            }
          }
          if (_state.value.timerRemainingSeconds == 0 && _state.value.timerRunning) {
            val total = _state.value.timerTotalSeconds
            val label = _state.value.timerLabel
            _state.update {
              it.copy(
                timerRunning = false,
                timerCompletedMessage = "$label completed (${total / 60}m logged)",
              )
            }
            repository.logTimerCompletion(total, label)
          }
        }
    }
  }

  fun resetTimer() {
    timerJob?.cancel()
    _state.update {
      it.copy(
        timerRunning = false,
        timerRemainingSeconds = it.timerTotalSeconds,
        timerCompletedMessage = null,
      )
    }
  }

  // --- Stopwatch ---
  fun toggleStopwatch() {
    if (_state.value.stopwatchRunning) {
      stopwatchJob?.cancel()
      stopwatchAccumulatedMs = _state.value.stopwatchElapsedMillis
      _state.update { it.copy(stopwatchRunning = false) }
    } else {
      stopwatchBaseMs = SystemClock.elapsedRealtime()
      _state.update { it.copy(stopwatchRunning = true) }
      stopwatchJob?.cancel()
      stopwatchJob =
        viewModelScope.launch {
          while (_state.value.stopwatchRunning) {
            val elapsed = stopwatchAccumulatedMs + (SystemClock.elapsedRealtime() - stopwatchBaseMs)
            _state.update { it.copy(stopwatchElapsedMillis = elapsed) }
            delay(33L)
          }
        }
    }
  }

  fun recordLap() {
    val total = _state.value.stopwatchElapsedMillis
    if (total <= 0L) return
    val lapDelta = (total - lastLapTotalMs).coerceAtLeast(0L)
    lastLapTotalMs = total
    _state.update {
      val nextIndex = it.stopwatchLaps.size + 1
      it.copy(stopwatchLaps = listOf(StopwatchLap(nextIndex, lapDelta, total)) + it.stopwatchLaps)
    }
  }

  fun resetStopwatch() {
    stopwatchJob?.cancel()
    stopwatchAccumulatedMs = 0L
    lastLapTotalMs = 0L
    _state.update {
      it.copy(stopwatchElapsedMillis = 0L, stopwatchRunning = false, stopwatchLaps = emptyList())
    }
  }

  // --- Focus Session ---
  fun setFocusConfig(minutes: Int, category: String) {
    if (_state.value.focusRunning) return
    val secs = minutes * 60
    _state.update {
      it.copy(
        focusCategory = category,
        focusTotalSeconds = secs,
        focusRemainingSeconds = secs,
        focusCompletedMessage = null,
      )
    }
  }

  fun toggleFocus() {
    if (_state.value.focusRunning) {
      focusJob?.cancel()
      _state.update { it.copy(focusRunning = false) }
    } else {
      if (_state.value.focusRemainingSeconds <= 0) {
        _state.update { it.copy(focusRemainingSeconds = it.focusTotalSeconds) }
      }
      _state.update { it.copy(focusRunning = true, focusCompletedMessage = null) }
      focusJob?.cancel()
      focusJob =
        viewModelScope.launch {
          while (_state.value.focusRemainingSeconds > 0 && _state.value.focusRunning) {
            delay(1000L)
            if (!_state.value.focusRunning) break
            _state.update {
              it.copy(focusRemainingSeconds = (it.focusRemainingSeconds - 1).coerceAtLeast(0))
            }
          }
          if (_state.value.focusRemainingSeconds == 0 && _state.value.focusRunning) {
            val total = _state.value.focusTotalSeconds
            val category = _state.value.focusCategory
            _state.update {
              it.copy(
                focusRunning = false,
                focusCompletedMessage = "$category session finished (+${total / 60}m)",
              )
            }
            repository.logFocusSession(total, category)
          }
        }
    }
  }

  fun finishFocusEarlyAndLog() {
    val elapsed = (_state.value.focusTotalSeconds - _state.value.focusRemainingSeconds).coerceAtLeast(0)
    focusJob?.cancel()
    _state.update {
      it.copy(focusRunning = false, focusRemainingSeconds = it.focusTotalSeconds)
    }
    if (elapsed >= 60) {
      viewModelScope.launch {
        repository.logFocusSession(elapsed, _state.value.focusCategory)
        _state.update {
          it.copy(focusCompletedMessage = "Logged ${elapsed / 60}m of ${_state.value.focusCategory}")
        }
      }
    }
  }

  fun resetFocus() {
    focusJob?.cancel()
    _state.update {
      it.copy(
        focusRunning = false,
        focusRemainingSeconds = it.focusTotalSeconds,
        focusCompletedMessage = null,
      )
    }
  }

  class Factory(private val repository: TimeNestRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ToolsViewModel(repository) as T
  }
}
