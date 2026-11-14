package com.example.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ActivityRecordEntity
import com.example.data.repository.TimeNestRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ReactionPhase {
  IDLE,
  WAITING,
  READY,
  TOO_SOON,
  RESULT,
}

data class GamesUiState(
  val phase: ReactionPhase = ReactionPhase.IDLE,
  val lastReactionMs: Int? = null,
  val bestReactionMs: Int? = null,
  val recentScores: List<ActivityRecordEntity> = emptyList(),
)

class GamesViewModel(private val repository: TimeNestRepository) : ViewModel() {

  private val phase = MutableStateFlow(ReactionPhase.IDLE)
  private val lastMs = MutableStateFlow<Int?>(null)
  private var goTimeNanos: Long = 0L
  private var waitJob: Job? = null

  val uiState: StateFlow<GamesUiState> =
    combine(
        phase,
        lastMs,
        repository.observeBestReactionMs(),
        repository.observeRecentReactionScores(),
      ) { p, last, best, recent ->
        GamesUiState(
          phase = p,
          lastReactionMs = last,
          bestReactionMs = best,
          recentScores = recent,
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GamesUiState(),
      )

  fun onArenaTap() {
    when (phase.value) {
      ReactionPhase.IDLE,
      ReactionPhase.TOO_SOON,
      ReactionPhase.RESULT -> startRound()
      ReactionPhase.WAITING -> {
        waitJob?.cancel()
        phase.value = ReactionPhase.TOO_SOON
      }
      ReactionPhase.READY -> {
        val elapsedMs = ((System.nanoTime() - goTimeNanos) / 1_000_000L).toInt().coerceAtLeast(1)
        lastMs.value = elapsedMs
        phase.value = ReactionPhase.RESULT
        viewModelScope.launch { repository.logReactionScore(elapsedMs) }
      }
    }
  }

  private fun startRound() {
    waitJob?.cancel()
    phase.value = ReactionPhase.WAITING
    val delayMs = Random.nextLong(1400L, 3800L)
    waitJob =
      viewModelScope.launch {
        delay(delayMs)
        goTimeNanos = System.nanoTime()
        phase.value = ReactionPhase.READY
      }
  }

  class Factory(private val repository: TimeNestRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = GamesViewModel(repository) as T
  }
}
