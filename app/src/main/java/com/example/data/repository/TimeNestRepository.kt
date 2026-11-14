package com.example.data.repository

import com.example.data.local.dao.ActivityRecordDao
import com.example.data.local.dao.WorldCityDao
import com.example.data.local.entity.ActivityRecordEntity
import com.example.data.local.entity.WorldCityEntity
import com.example.data.local.prefs.UserPreferencesDataSource
import com.example.domain.model.AccentPreset
import com.example.domain.model.ClockBackdrop
import com.example.domain.model.ClockFont
import com.example.domain.model.ClockStyle
import com.example.domain.model.DashboardCardType
import com.example.domain.model.ThemeMode
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

data class TodayProductivitySummary(
  val focusMinutesToday: Int = 0,
  val completedFocusCount: Int = 0,
  val completedTimersCount: Int = 0,
)

class TimeNestRepository(
  private val prefsDataSource: UserPreferencesDataSource,
  private val activityRecordDao: ActivityRecordDao,
  private val worldCityDao: WorldCityDao,
) {

  val preferencesFlow: Flow<UserPreferences> = prefsDataSource.preferencesFlow

  val tickerFlow: Flow<Long> = flow {
    while (true) {
      val now = System.currentTimeMillis()
      emit(now)
      val nextDelay = (1000L - (now % 1000L)).coerceIn(50L, 1000L)
      delay(nextDelay)
    }
  }

  val worldCitiesFlow: Flow<List<WorldCityEntity>> = worldCityDao.observeAllCities()

  fun observeTodaySummary(): Flow<TodayProductivitySummary> {
    val startOfDay = TimeFormatter.startOfTodayMillis()
    return activityRecordDao.observeRecordsSince(startOfDay).map { records ->
      val focusRecords = records.filter { it.type == "FOCUS" }
      val timerRecords = records.filter { it.type == "TIMER" }
      val focusSeconds = focusRecords.sumOf { it.durationSeconds }
      TodayProductivitySummary(
        focusMinutesToday = focusSeconds / 60,
        completedFocusCount = focusRecords.size,
        completedTimersCount = timerRecords.size,
      )
    }
  }

  fun observeBestReactionMs(): Flow<Int?> = activityRecordDao.observeBestReactionMs()

  fun observeRecentReactionScores(): Flow<List<ActivityRecordEntity>> =
    activityRecordDao.observeRecentReactionScores()

  suspend fun ensureDefaultWorldCities() {
    if (worldCityDao.getCount() == 0) {
      worldCityDao.insertAll(
        listOf(
          WorldCityEntity(
            zoneId = "Europe/London",
            cityName = "London",
            regionLabel = "Europe",
            sortOrder = 0,
          ),
          WorldCityEntity(
            zoneId = "America/New_York",
            cityName = "New York",
            regionLabel = "America",
            sortOrder = 1,
          ),
          WorldCityEntity(
            zoneId = "Asia/Tokyo",
            cityName = "Tokyo",
            regionLabel = "Asia",
            sortOrder = 2,
          ),
        )
      )
    }
  }

  suspend fun addWorldCity(zoneId: String, cityName: String, regionLabel: String) {
    worldCityDao.insert(
      WorldCityEntity(
        zoneId = zoneId,
        cityName = cityName,
        regionLabel = regionLabel,
        sortOrder = worldCityDao.getCount(),
      )
    )
  }

  suspend fun removeWorldCity(id: Long) {
    worldCityDao.deleteById(id)
  }

  suspend fun logFocusSession(durationSeconds: Int, label: String = "Focus Session") {
    activityRecordDao.insertRecord(
      ActivityRecordEntity(type = "FOCUS", durationSeconds = durationSeconds, label = label)
    )
  }

  suspend fun logTimerCompletion(durationSeconds: Int, label: String = "Quick Timer") {
    activityRecordDao.insertRecord(
      ActivityRecordEntity(type = "TIMER", durationSeconds = durationSeconds, label = label)
    )
  }

  suspend fun logReactionScore(reactionMs: Int) {
    activityRecordDao.insertRecord(
      ActivityRecordEntity(
        type = "REACTION_GAME",
        durationSeconds = 0,
        scoreValue = reactionMs,
        label = "Reaction Speed",
      )
    )
  }

  suspend fun setOnboardingCompleted(completed: Boolean) =
    prefsDataSource.setOnboardingCompleted(completed)

  suspend fun setThemeMode(mode: ThemeMode) = prefsDataSource.setThemeMode(mode)

  suspend fun setAccentPreset(accent: AccentPreset) = prefsDataSource.setAccentPreset(accent)

  suspend fun setUse24HourFormat(use24h: Boolean) = prefsDataSource.setUse24HourFormat(use24h)

  suspend fun setShowSeconds(show: Boolean) = prefsDataSource.setShowSeconds(show)

  suspend fun setClockStyle(style: ClockStyle) = prefsDataSource.setClockStyle(style)

  suspend fun setClockFont(font: ClockFont) = prefsDataSource.setClockFont(font)

  suspend fun setClockBackdrop(backdrop: ClockBackdrop) = prefsDataSource.setClockBackdrop(backdrop)

  suspend fun setKeepScreenOnNightstand(keepOn: Boolean) =
    prefsDataSource.setKeepScreenOnNightstand(keepOn)

  suspend fun setHourlyChimeEnabled(enabled: Boolean) =
    prefsDataSource.setHourlyChimeEnabled(enabled)

  suspend fun setWakeTarget(hour: Int, minute: Int, enabled: Boolean) =
    prefsDataSource.setWakeTarget(hour, minute, enabled)

  suspend fun setDailyFocusGoalMinutes(minutes: Int) =
    prefsDataSource.setDailyFocusGoalMinutes(minutes)

  suspend fun setDashboardLayout(order: List<DashboardCardType>, hidden: Set<DashboardCardType>) =
    prefsDataSource.setDashboardLayout(order, hidden)
}
