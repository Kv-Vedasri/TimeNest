package com.example.domain.model

data class UserPreferences(
  val hasCompletedOnboarding: Boolean = false,
  val themeMode: ThemeMode = ThemeMode.DARK,
  val accentPreset: AccentPreset = AccentPreset.SOLAR_AMBER,
  val use24HourFormat: Boolean = false,
  val showSeconds: Boolean = true,
  val clockStyle: ClockStyle = ClockStyle.DIGITAL,
  val clockFont: ClockFont = ClockFont.OUTFIT,
  val clockBackdrop: ClockBackdrop = ClockBackdrop.AMBIENT,
  val keepScreenOnNightstand: Boolean = true,
  val hourlyChimeEnabled: Boolean = false,
  val wakeTargetHour: Int = 7,
  val wakeTargetMinute: Int = 0,
  val wakeTargetEnabled: Boolean = true,
  val dailyFocusGoalMinutes: Int = 60,
  val dashboardCardOrder: List<DashboardCardType> = DashboardCardType.defaultOrder,
  val hiddenDashboardCards: Set<DashboardCardType> = emptySet(),
)
