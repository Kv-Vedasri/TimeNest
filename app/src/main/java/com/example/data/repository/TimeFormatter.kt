package com.example.data.repository

import com.example.domain.model.TimeSnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

data class WorldZoneInfo(
  val zoneId: String,
  val cityName: String,
  val regionLabel: String,
  val formattedTime: String,
  val offsetLabel: String,
  val isDaytime: Boolean,
  val dayRelation: String,
)

object TimeFormatter {

  fun buildLocalSnapshot(epochMillis: Long, use24Hour: Boolean): TimeSnapshot {
    val calendar = Calendar.getInstance().apply { timeInMillis = epochMillis }
    val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val hourDisplay =
      if (use24Hour) {
        String.format(Locale.US, "%02d", hour24)
      } else {
        val h12 = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
        String.format(Locale.US, "%02d", h12)
      }
    val amPm = if (hour24 < 12) "AM" else "PM"
    val locale = Locale.getDefault()
    val dayOfWeek = SimpleDateFormat("EEEE", locale).format(Date(epochMillis))
    val dateFormatted = SimpleDateFormat("MMM d, yyyy", locale).format(Date(epochMillis))
    val tz = calendar.timeZone
    val tzShort = tz.getDisplayName(tz.inDaylightTime(Date(epochMillis)), TimeZone.SHORT, locale)
    val isDaytime = hour24 in 6..18
    val dayPhaseLabel =
      when (hour24) {
        in 5..11 -> "Morning"
        in 12..16 -> "Afternoon"
        in 17..20 -> "Evening"
        else -> "Night"
      }
    val totalDaySeconds = hour24 * 3600 + minute * 60 + second
    val dayProgress = (totalDaySeconds / 86400f).coerceIn(0f, 1f)

    return TimeSnapshot(
      epochMillis = epochMillis,
      hour24 = hour24,
      hourDisplay = hourDisplay,
      minuteDisplay = String.format(Locale.US, "%02d", minute),
      secondDisplay = String.format(Locale.US, "%02d", second),
      minute = minute,
      second = second,
      amPm = amPm,
      dayOfWeek = dayOfWeek,
      dateFormatted = dateFormatted,
      timeZoneName = tzShort,
      isDaytime = isDaytime,
      dayPhaseLabel = dayPhaseLabel,
      dayProgressFraction = dayProgress,
    )
  }

  fun formatWorldZone(
    zoneId: String,
    cityName: String,
    regionLabel: String,
    epochMillis: Long,
    use24Hour: Boolean,
  ): WorldZoneInfo {
    val targetTz = TimeZone.getTimeZone(zoneId)
    val localTz = TimeZone.getDefault()
    val targetCal = Calendar.getInstance(targetTz).apply { timeInMillis = epochMillis }
    val localCal = Calendar.getInstance(localTz).apply { timeInMillis = epochMillis }

    val pattern = if (use24Hour) "HH:mm" else "h:mm a"
    val sdf = SimpleDateFormat(pattern, Locale.getDefault()).apply { timeZone = targetTz }
    val formattedTime = sdf.format(Date(epochMillis))

    val diffMillis = targetTz.getOffset(epochMillis) - localTz.getOffset(epochMillis)
    val diffHours = diffMillis / 3_600_000.0
    val offsetLabel =
      when {
        abs(diffHours) < 0.01 -> "Same time"
        diffHours % 1.0 == 0.0 -> {
          val h = diffHours.toInt()
          if (h > 0) "+${h}h" else "${h}h"
        }
        else -> {
          val sign = if (diffHours > 0) "+" else "-"
          val totalMin = abs((diffMillis / 60_000).toInt())
          "$sign${totalMin / 60}h ${totalMin % 60}m"
        }
      }

    val targetDay = targetCal.get(Calendar.DAY_OF_YEAR)
    val localDay = localCal.get(Calendar.DAY_OF_YEAR)
    val targetYear = targetCal.get(Calendar.YEAR)
    val localYear = localCal.get(Calendar.YEAR)
    val dayRelation =
      when {
        targetYear > localYear || (targetYear == localYear && targetDay > localDay) -> "Tomorrow"
        targetYear < localYear || (targetYear == localYear && targetDay < localDay) -> "Yesterday"
        else -> "Today"
      }

    val hour24 = targetCal.get(Calendar.HOUR_OF_DAY)
    return WorldZoneInfo(
      zoneId = zoneId,
      cityName = cityName,
      regionLabel = regionLabel,
      formattedTime = formattedTime,
      offsetLabel = offsetLabel,
      isDaytime = hour24 in 6..18,
      dayRelation = dayRelation,
    )
  }

  fun formatWakeTime(hour: Int, minute: Int, use24Hour: Boolean): String {
    if (use24Hour) {
      return String.format(Locale.US, "%02d:%02d", hour, minute)
    }
    val amPm = if (hour < 12) "AM" else "PM"
    val h12 =
      when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
      }
    return String.format(Locale.US, "%d:%02d %s", h12, minute, amPm)
  }

  fun startOfTodayMillis(nowMillis: Long = System.currentTimeMillis()): Long {
    return Calendar.getInstance()
      .apply {
        timeInMillis = nowMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }
      .timeInMillis
  }

  fun formatDurationClock(totalSeconds: Int): String {
    val hrs = totalSeconds / 3600
    val mins = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return if (hrs > 0) {
      String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    } else {
      String.format(Locale.US, "%02d:%02d", mins, secs)
    }
  }

  fun formatStopwatchMillis(millis: Long): String {
    val totalCentis = (millis / 10).coerceAtLeast(0)
    val centis = totalCentis % 100
    val totalSeconds = totalCentis / 100
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
      String.format(Locale.US, "%d:%02d:%02d.%02d", hours, minutes, seconds, centis)
    } else {
      String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centis)
    }
  }

  fun availableCityZones(): List<Triple<String, String, String>> {
    return TimeZone.getAvailableIDs()
      .asSequence()
      .filter { it.contains("/") && !it.startsWith("Etc/") && !it.startsWith("SystemV/") }
      .map { id ->
        val parts = id.split("/")
        val region = parts.first().replace("_", " ")
        val city = parts.last().replace("_", " ")
        Triple(id, city, region)
      }
      .distinctBy { it.first }
      .sortedBy { it.second }
      .toList()
  }
}
