package com.example.domain.model

data class TimeSnapshot(
  val epochMillis: Long = 0L,
  val hour24: Int = 0,
  val hourDisplay: String = "12",
  val minuteDisplay: String = "00",
  val secondDisplay: String = "00",
  val minute: Int = 0,
  val second: Int = 0,
  val amPm: String = "AM",
  val dayOfWeek: String = "Monday",
  val dateFormatted: String = "Jan 1, 2026",
  val timeZoneName: String = "Local",
  val isDaytime: Boolean = true,
  val dayPhaseLabel: String = "Morning",
  val dayProgressFraction: Float = 0.5f,
)
