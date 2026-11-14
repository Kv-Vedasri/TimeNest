package com.example.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AccentPreset
import com.example.domain.model.ClockBackdrop
import com.example.domain.model.ClockFont
import com.example.domain.model.ClockStyle
import com.example.domain.model.DashboardCardType
import com.example.domain.model.ThemeMode
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timenest_prefs")

class UserPreferencesDataSource(private val context: Context) {

  private object Keys {
    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val ACCENT_PRESET = stringPreferencesKey("accent_preset")
    val USE_24H = booleanPreferencesKey("use_24h")
    val SHOW_SECONDS = booleanPreferencesKey("show_seconds")
    val CLOCK_STYLE = stringPreferencesKey("clock_style")
    val CLOCK_FONT = stringPreferencesKey("clock_font")
    val CLOCK_BACKDROP = stringPreferencesKey("clock_backdrop")
    val KEEP_SCREEN_ON_NIGHTSTAND = booleanPreferencesKey("keep_screen_on_nightstand")
    val HOURLY_CHIME = booleanPreferencesKey("hourly_chime")
    val WAKE_HOUR = intPreferencesKey("wake_hour")
    val WAKE_MINUTE = intPreferencesKey("wake_minute")
    val WAKE_ENABLED = booleanPreferencesKey("wake_enabled")
    val DAILY_FOCUS_GOAL = intPreferencesKey("daily_focus_goal")
    val DASHBOARD_ORDER = stringPreferencesKey("dashboard_order")
    val HIDDEN_CARDS = stringPreferencesKey("hidden_cards")
  }

  val preferencesFlow: Flow<UserPreferences> =
    context.dataStore.data.map { prefs ->
      UserPreferences(
        hasCompletedOnboarding = prefs[Keys.ONBOARDING_DONE] ?: false,
        themeMode = parseEnum(prefs[Keys.THEME_MODE], ThemeMode.DARK),
        accentPreset = parseEnum(prefs[Keys.ACCENT_PRESET], AccentPreset.SOLAR_AMBER),
        use24HourFormat = prefs[Keys.USE_24H] ?: false,
        showSeconds = prefs[Keys.SHOW_SECONDS] ?: true,
        clockStyle = parseEnum(prefs[Keys.CLOCK_STYLE], ClockStyle.DIGITAL),
        clockFont = parseEnum(prefs[Keys.CLOCK_FONT], ClockFont.OUTFIT),
        clockBackdrop = parseEnum(prefs[Keys.CLOCK_BACKDROP], ClockBackdrop.AMBIENT),
        keepScreenOnNightstand = prefs[Keys.KEEP_SCREEN_ON_NIGHTSTAND] ?: true,
        hourlyChimeEnabled = prefs[Keys.HOURLY_CHIME] ?: false,
        wakeTargetHour = prefs[Keys.WAKE_HOUR] ?: 7,
        wakeTargetMinute = prefs[Keys.WAKE_MINUTE] ?: 0,
        wakeTargetEnabled = prefs[Keys.WAKE_ENABLED] ?: true,
        dailyFocusGoalMinutes = prefs[Keys.DAILY_FOCUS_GOAL] ?: 60,
        dashboardCardOrder = DashboardCardType.parseOrder(prefs[Keys.DASHBOARD_ORDER]),
        hiddenDashboardCards = DashboardCardType.parseHidden(prefs[Keys.HIDDEN_CARDS]),
      )
    }

  suspend fun setOnboardingCompleted(completed: Boolean) {
    context.dataStore.edit { it[Keys.ONBOARDING_DONE] = completed }
  }

  suspend fun setThemeMode(mode: ThemeMode) {
    context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
  }

  suspend fun setAccentPreset(accent: AccentPreset) {
    context.dataStore.edit { it[Keys.ACCENT_PRESET] = accent.name }
  }

  suspend fun setUse24HourFormat(use24h: Boolean) {
    context.dataStore.edit { it[Keys.USE_24H] = use24h }
  }

  suspend fun setShowSeconds(show: Boolean) {
    context.dataStore.edit { it[Keys.SHOW_SECONDS] = show }
  }

  suspend fun setClockStyle(style: ClockStyle) {
    context.dataStore.edit { it[Keys.CLOCK_STYLE] = style.name }
  }

  suspend fun setClockFont(font: ClockFont) {
    context.dataStore.edit { it[Keys.CLOCK_FONT] = font.name }
  }

  suspend fun setClockBackdrop(backdrop: ClockBackdrop) {
    context.dataStore.edit { it[Keys.CLOCK_BACKDROP] = backdrop.name }
  }

  suspend fun setKeepScreenOnNightstand(keepOn: Boolean) {
    context.dataStore.edit { it[Keys.KEEP_SCREEN_ON_NIGHTSTAND] = keepOn }
  }

  suspend fun setHourlyChimeEnabled(enabled: Boolean) {
    context.dataStore.edit { it[Keys.HOURLY_CHIME] = enabled }
  }

  suspend fun setWakeTarget(hour: Int, minute: Int, enabled: Boolean) {
    context.dataStore.edit {
      it[Keys.WAKE_HOUR] = hour.coerceIn(0, 23)
      it[Keys.WAKE_MINUTE] = minute.coerceIn(0, 59)
      it[Keys.WAKE_ENABLED] = enabled
    }
  }

  suspend fun setDailyFocusGoalMinutes(minutes: Int) {
    context.dataStore.edit { it[Keys.DAILY_FOCUS_GOAL] = minutes.coerceIn(10, 480) }
  }

  suspend fun setDashboardLayout(order: List<DashboardCardType>, hidden: Set<DashboardCardType>) {
    context.dataStore.edit {
      it[Keys.DASHBOARD_ORDER] = order.joinToString(",") { card -> card.name }
      it[Keys.HIDDEN_CARDS] = hidden.joinToString(",") { card -> card.name }
    }
  }

  private inline fun <reified T : Enum<T>> parseEnum(raw: String?, fallback: T): T {
    if (raw.isNullOrBlank()) return fallback
    return enumValues<T>().firstOrNull { it.name == raw } ?: fallback
  }
}
