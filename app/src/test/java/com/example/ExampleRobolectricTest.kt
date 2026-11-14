package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.TimeFormatter
import com.example.domain.model.DashboardCardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `app name matches TimeNest`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TimeNest", appName)
  }

  @Test
  fun `time formatter respects 12h and 24h formats and IANA zones`() {
    val fixedMillis = 1_760_000_000_000L
    val snap24 = TimeFormatter.buildLocalSnapshot(fixedMillis, use24Hour = true)
    val snap12 = TimeFormatter.buildLocalSnapshot(fixedMillis, use24Hour = false)
    assertEquals(2, snap24.hourDisplay.length)
    assertEquals(2, snap12.minuteDisplay.length)
    assertTrue(snap12.amPm == "AM" || snap12.amPm == "PM")

    val tokyo =
      TimeFormatter.formatWorldZone(
        zoneId = "Asia/Tokyo",
        cityName = "Tokyo",
        regionLabel = "Asia",
        epochMillis = fixedMillis,
        use24Hour = true,
      )
    assertEquals("Tokyo", tokyo.cityName)
    assertTrue(tokyo.formattedTime.contains(":"))
  }

  @Test
  fun `dashboard card order parser restores missing cards`() {
    val parsed = DashboardCardType.parseOrder("WORLD_CLOCK,QUICK_TIMER")
    assertEquals(DashboardCardType.WORLD_CLOCK, parsed[0])
    assertEquals(DashboardCardType.QUICK_TIMER, parsed[1])
    assertEquals(DashboardCardType.entries.size, parsed.size)
  }

  @Test
  fun `onboarding skip opens dashboard and navigates across all tabs`() {
    composeTestRule.waitUntil(timeoutMillis = 5_000L) {
      composeTestRule.onAllNodesWithTag("onboarding_skip_btn").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("onboarding_skip_btn").assertIsDisplayed().performClick()

    composeTestRule.waitUntil(timeoutMillis = 5_000L) {
      composeTestRule.onAllNodesWithTag("home_hero_clock_card").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("home_hero_clock_card").assertIsDisplayed()

    // Navigate to Clock Studio
    composeTestRule.onNodeWithTag("nav_clock").performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000L) {
      composeTestRule.onAllNodesWithTag("clock_nightstand_btn").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("clock_nightstand_btn").assertIsDisplayed()

    // Navigate to Tools
    composeTestRule.onNodeWithTag("nav_tools").performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000L) {
      composeTestRule.onAllNodesWithTag("tools_tab_stopwatch").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("tools_tab_stopwatch").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tools_timer_toggle_btn").performScrollTo().assertIsDisplayed()

    // Navigate to Games
    composeTestRule.onNodeWithTag("nav_games").performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000L) {
      composeTestRule.onAllNodesWithTag("reaction_arena_card").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("reaction_arena_card").assertIsDisplayed()

    // Navigate to Profile
    composeTestRule.onNodeWithTag("nav_profile").performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000L) {
      composeTestRule.onAllNodesWithTag("profile_theme_dark").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("profile_theme_dark").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profile_theme_amoled").assertIsDisplayed().performClick()
  }
}
