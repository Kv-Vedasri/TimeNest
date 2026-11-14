package com.example.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.AccentPreset
import com.example.domain.model.ClockFont
import com.example.domain.model.ThemeMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
  uiState: ProfileUiState,
  onSelectThemeMode: (ThemeMode) -> Unit,
  onSelectAccent: (AccentPreset) -> Unit,
  onToggle24Hour: (Boolean) -> Unit,
  onToggleShowSeconds: (Boolean) -> Unit,
  onSelectTimeZone: (String) -> Unit,
  onSelectClockFont: (ClockFont) -> Unit,
  onToggleKeepScreenOn: (Boolean) -> Unit,
  onSelectDailyFocusGoal: (Int) -> Unit,
  onResetDashboard: () -> Unit,
  onReplayOnboarding: () -> Unit,
) {
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var layoutResetNotice by remember { mutableStateOf(false) }
  val prefs = uiState.preferences

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        Column {
          Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineLarge,
          )
          Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Live Local Activity Strip
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          ProfileStatTile(
            label = "Focus Today",
            value = "${uiState.todaySummary.focusMinutesToday}m",
            modifier = Modifier.weight(1f),
          )
          ProfileStatTile(
            label = "Timers Done",
            value = "${uiState.todaySummary.completedTimersCount}",
            modifier = Modifier.weight(1f),
          )
          ProfileStatTile(
            label = "Best Reflex",
            value = uiState.bestReactionMs?.let { "${it}ms" } ?: "—",
            modifier = Modifier.weight(1f),
          )
        }
      }

      // Appearance & Theme
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Text(
              text = stringResource(R.string.settings_appearance),
              style = MaterialTheme.typography.titleLarge,
            )

            Text(text = "Theme Mode", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              ThemeMode.entries.forEach { mode ->
                FilterChip(
                  selected = prefs.themeMode == mode,
                  onClick = { onSelectThemeMode(mode) },
                  label = { Text(mode.label) },
                  modifier = Modifier.testTag("profile_theme_${mode.name.lowercase()}"),
                )
              }
            }

            Text(text = "Accent Colour", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              AccentPreset.entries.forEach { preset ->
                val selected = prefs.accentPreset == preset
                Box(
                  modifier =
                    Modifier.size(44.dp)
                      .minimumInteractiveComponentSize()
                      .clip(CircleShape)
                      .background(preset.primaryDark)
                      .clickable { onSelectAccent(preset) }
                      .testTag("profile_accent_${preset.name.lowercase()}"),
                  contentAlignment = Alignment.Center,
                ) {
                  if (selected) {
                    Icon(
                      imageVector = Icons.Rounded.Check,
                      contentDescription = "${preset.label} selected",
                      tint = Color(0xFF090D16),
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Clock & Focus Settings
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Text(
              text = stringResource(R.string.settings_clock),
              style = MaterialTheme.typography.titleLarge,
            )

            SettingsToggleRow(
              title = "24-Hour Time Format",
              subtitle = "Use 00:00–23:59 instead of AM/PM",
              checked = prefs.use24HourFormat,
              onCheckedChange = onToggle24Hour,
              tag = "profile_24h_switch",
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            SettingsToggleRow(
              title = "Show Seconds",
              subtitle = "Display live ticking seconds on clocks",
              checked = prefs.showSeconds,
              onCheckedChange = onToggleShowSeconds,
              tag = "profile_seconds_switch",
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            SettingsToggleRow(
              title = "Keep Screen On in Nightstand",
              subtitle = "Prevents screen timeout while Nightstand mode is open",
              checked = prefs.keepScreenOnNightstand,
              onCheckedChange = onToggleKeepScreenOn,
              tag = "profile_keep_screen_switch",
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Text(text = "Primary Clock Timezone", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              FilterChip(
                selected = prefs.displayTimeZoneId == com.example.data.repository.TimeFormatter.ZONE_IST,
                onClick = { onSelectTimeZone(com.example.data.repository.TimeFormatter.ZONE_IST) },
                label = { Text("IST (India · UTC+5:30)") },
              )
              FilterChip(
                selected = prefs.displayTimeZoneId == com.example.data.repository.TimeFormatter.ZONE_SYSTEM,
                onClick = { onSelectTimeZone(com.example.data.repository.TimeFormatter.ZONE_SYSTEM) },
                label = { Text("Device Local") },
              )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Text(text = "Clock Font", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              ClockFont.entries.forEach { font ->
                FilterChip(
                  selected = prefs.clockFont == font,
                  onClick = { onSelectClockFont(font) },
                  label = { Text(font.label) },
                )
              }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Text(text = "Daily Focus Goal", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf(30, 45, 60, 90, 120).forEach { mins ->
                FilterChip(
                  selected = prefs.dailyFocusGoalMinutes == mins,
                  onClick = { onSelectDailyFocusGoal(mins) },
                  label = { Text("${mins}m") },
                )
              }
            }
          }
        }
      }

      // Space & Privacy Actions
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = stringResource(R.string.settings_privacy),
              style = MaterialTheme.typography.titleLarge,
            )

            OutlinedButton(
              onClick = {
                onResetDashboard()
                layoutResetNotice = true
              },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize(),
            ) {
              Icon(Icons.Rounded.DashboardCustomize, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                if (layoutResetNotice) "Dashboard Layout Restored"
                else "Reset Dashboard Arrangement"
              )
            }

            OutlinedButton(
              onClick = onReplayOnboarding,
              shape = RoundedCornerShape(14.dp),
              modifier =
                Modifier.fillMaxWidth()
                  .minimumInteractiveComponentSize()
                  .testTag("profile_replay_onboarding_btn"),
            ) {
              Icon(Icons.Rounded.Replay, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Replay Onboarding Experience")
            }

            OutlinedButton(
              onClick = { showPrivacyDialog = true },
              shape = RoundedCornerShape(14.dp),
              modifier =
                Modifier.fillMaxWidth()
                  .minimumInteractiveComponentSize()
                  .testTag("profile_privacy_btn"),
            ) {
              Icon(Icons.Rounded.PrivacyTip, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Privacy Policy & Offline Data")
            }
          }
        }
      }

      // Phase 1 Status Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Shield,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "TimeNest Phase 1 · Active Build",
                style = MaterialTheme.typography.titleMedium,
              )
              Text(
                text = "Dashboard, Clock Studio, Nightstand, Quick Tools, Reaction Test & Local Privacy.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }

  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text(stringResource(R.string.privacy_dialog_title)) },
      text = { Text(stringResource(R.string.privacy_dialog_body)) },
      confirmButton = {
        TextButton(onClick = { showPrivacyDialog = false }) {
          Text(stringResource(R.string.close))
        }
      },
    )
  }
}

@Composable
private fun ProfileStatTile(label: String, value: String, modifier: Modifier = Modifier) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier,
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(fontFeatureSettings = "tnum"),
        color = MaterialTheme.colorScheme.primary,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun SettingsToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  tag: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
      Text(text = title, style = MaterialTheme.typography.titleMedium)
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(checked = checked, onCheckedChange = onCheckedChange, modifier = Modifier.testTag(tag))
  }
}
