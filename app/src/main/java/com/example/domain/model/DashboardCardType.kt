package com.example.domain.model

enum class DashboardCardType(val title: String, val description: String) {
  QUICK_ACTIONS("Quick Shortcuts", "Fast launch for Timer, Stopwatch & Focus"),
  QUICK_TIMER("Quick Timer", "One-tap countdown presets right on Home"),
  UPCOMING_SCHEDULE("Sleep & Wake Target", "Configured wake target & bedtime preview"),
  WORLD_CLOCK("World Clock Preview", "Live time across your favorite time zones"),
  PRODUCTIVITY_SUMMARY("Today's Rhythm", "Actual focus minutes, timers & daily goal progress");

  companion object {
    val defaultOrder: List<DashboardCardType> = entries.toList()

    fun parseOrder(raw: String?): List<DashboardCardType> {
      if (raw.isNullOrBlank()) return defaultOrder
      val parsed =
        raw.split(",").mapNotNull { token -> entries.firstOrNull { it.name == token.trim() } }
      val missing = defaultOrder.filterNot { it in parsed }
      return parsed + missing
    }

    fun parseHidden(raw: String?): Set<DashboardCardType> {
      if (raw.isNullOrBlank()) return emptySet()
      return raw
        .split(",")
        .mapNotNull { token -> entries.firstOrNull { it.name == token.trim() } }
        .toSet()
    }
  }
}
