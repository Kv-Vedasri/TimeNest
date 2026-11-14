package com.example

import com.example.data.repository.TimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun `formatDurationClock formats minutes and hours accurately`() {
    assertEquals("05:00", TimeFormatter.formatDurationClock(300))
    assertEquals("00:45", TimeFormatter.formatDurationClock(45))
    assertEquals("01:01:05", TimeFormatter.formatDurationClock(3665))
  }

  @Test
  fun `formatStopwatchMillis formats centiseconds cleanly`() {
    assertEquals("00:00.00", TimeFormatter.formatStopwatchMillis(0L))
    assertEquals("01:05.42", TimeFormatter.formatStopwatchMillis(65_429L))
  }

  @Test
  fun `formatWakeTime handles 12h and 24h midnight and noon`() {
    assertEquals("12:00 AM", TimeFormatter.formatWakeTime(0, 0, use24Hour = false))
    assertEquals("12:30 PM", TimeFormatter.formatWakeTime(12, 30, use24Hour = false))
    assertEquals("00:00", TimeFormatter.formatWakeTime(0, 0, use24Hour = true))
    assertEquals("19:45", TimeFormatter.formatWakeTime(19, 45, use24Hour = true))
  }
}
