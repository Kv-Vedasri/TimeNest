package com.example.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessAlarm
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.WorldCityEntity
import com.example.data.repository.TimeFormatter
import com.example.data.repository.TodayProductivitySummary
import com.example.data.repository.WorldZoneInfo
import com.example.domain.model.TimeSnapshot
import com.example.domain.model.UserPreferences
import com.example.ui.theme.toFontFamily

@Composable
fun HeroClockCard(
  snapshot: TimeSnapshot,
  preferences: UserPreferences,
  onToggleFormat: () -> Unit,
  onOpenClockStudio: () -> Unit,
) {
  val fontFamily = preferences.clockFont.toFontFamily()
  Card(
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth().testTag("home_hero_clock_card"),
  ) {
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .background(
            Brush.linearGradient(
              colors =
                listOf(
                  MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
                  MaterialTheme.colorScheme.surface,
                  MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f),
                )
            )
          )
          .padding(22.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector =
                  if (snapshot.isDaytime) Icons.Rounded.WbSunny else Icons.Rounded.DarkMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text =
                  if (snapshot.isDaytime) {
                    "${stringResource(R.string.day_indicator)} · ${snapshot.dayPhaseLabel}"
                  } else {
                    "${stringResource(R.string.night_indicator)} · ${snapshot.dayPhaseLabel}"
                  },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceVariant,
              onClick = onToggleFormat,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("home_format_pill"),
            ) {
              Text(
                text = if (preferences.use24HourFormat) "24H" else "12H",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              )
            }
            FilledTonalIconButton(
              onClick = onOpenClockStudio,
              modifier =
                Modifier.minimumInteractiveComponentSize().testTag("home_open_clock_studio_btn"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Fullscreen,
                contentDescription = "Open Clock Studio",
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = "${snapshot.hourDisplay}:${snapshot.minuteDisplay}",
            style =
              TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 62.sp,
                letterSpacing = (-2).sp,
                fontFeatureSettings = "tnum",
              ),
            color = MaterialTheme.colorScheme.onSurface,
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.padding(bottom = 10.dp)) {
            if (!preferences.use24HourFormat) {
              Text(
                text = snapshot.amPm,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
              )
            }
            if (preferences.showSeconds) {
              Text(
                text = snapshot.secondDisplay,
                style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "${snapshot.dayOfWeek}, ${snapshot.dateFormatted} · ${snapshot.timeZoneName}",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
fun QuickActionsCard(
  onOpenTimer: () -> Unit,
  onOpenStopwatch: () -> Unit,
  onOpenFocus: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().testTag("home_quick_actions_row"),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    QuickShortcutTile(
      title = stringResource(R.string.quick_timer_title),
      subtitle = "Countdown",
      icon = Icons.Rounded.HourglassBottom,
      tag = "shortcut_timer",
      onClick = onOpenTimer,
      modifier = Modifier.weight(1f),
    )
    QuickShortcutTile(
      title = stringResource(R.string.stopwatch_shortcut_title),
      subtitle = "Laps & splits",
      icon = Icons.Rounded.Timer,
      tag = "shortcut_stopwatch",
      onClick = onOpenStopwatch,
      modifier = Modifier.weight(1f),
    )
    QuickShortcutTile(
      title = stringResource(R.string.focus_shortcut_title),
      subtitle = "Pomodoro",
      icon = Icons.Rounded.CenterFocusStrong,
      tag = "shortcut_focus",
      onClick = onOpenFocus,
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun QuickShortcutTile(
  title: String,
  subtitle: String,
  icon: ImageVector,
  tag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier =
      modifier
        .minimumInteractiveComponentSize()
        .clip(RoundedCornerShape(22.dp))
        .clickable(onClick = onClick)
        .testTag(tag),
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(38.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
          )
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickTimerDashboardCard(
  remainingSeconds: Int,
  totalSeconds: Int,
  isRunning: Boolean,
  completedNotice: Boolean,
  onSelectPresetMinutes: (Int) -> Unit,
  onToggleRun: () -> Unit,
  onReset: () -> Unit,
  onDismissNotice: () -> Unit,
  onOpenFullTools: () -> Unit,
) {
  val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth().testTag("home_quick_timer_card"),
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.HourglassBottom,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.quick_timer_title),
            style = MaterialTheme.typography.titleMedium,
          )
        }
        TextButton(onClick = onOpenFullTools, modifier = Modifier.minimumInteractiveComponentSize()) {
          Text("Full Tools")
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = TimeFormatter.formatDurationClock(remainingSeconds),
          style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = "tnum"),
          color = MaterialTheme.colorScheme.onSurface,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilledTonalIconButton(
            onClick = onReset,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("home_timer_reset_btn"),
          ) {
            Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "Reset quick timer")
          }
          FilledTonalIconButton(
            onClick = onToggleRun,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("home_timer_toggle_btn"),
          ) {
            Icon(
              imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
              contentDescription = if (isRunning) "Pause timer" else "Start timer",
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
      )

      AnimatedVisibility(visible = completedNotice) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
          onClick = onDismissNotice,
          modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        ) {
          Text(
            text = "Timer completed & logged to Today's Rhythm! Tap to dismiss.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(1, 5, 10, 25).forEach { mins ->
          FilterChip(
            selected = totalSeconds == mins * 60,
            onClick = { onSelectPresetMinutes(mins) },
            label = { Text("${mins}m") },
            modifier = Modifier.testTag("home_timer_preset_${mins}m"),
          )
        }
      }
    }
  }
}

@Composable
fun UpcomingScheduleCard(
  wakeHour: Int,
  wakeMinute: Int,
  wakeEnabled: Boolean,
  use24Hour: Boolean,
  onToggleWakeEnabled: (Boolean) -> Unit,
  onEditWakeTarget: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .clickable(onClick = onEditWakeTarget)
        .testTag("home_schedule_card"),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.secondaryContainer,
          modifier = Modifier.size(44.dp),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector =
                if (wakeEnabled) Icons.Rounded.AccessAlarm else Icons.Rounded.Bedtime,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
            )
          }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = stringResource(R.string.upcoming_schedule_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = TimeFormatter.formatWakeTime(wakeHour, wakeMinute, use24Hour),
            style = MaterialTheme.typography.headlineMedium.copy(fontFeatureSettings = "tnum"),
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = "Tap to adjust · AlarmManager scheduling arrives in Phase 2",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      Switch(
        checked = wakeEnabled,
        onCheckedChange = onToggleWakeEnabled,
        modifier = Modifier.testTag("home_wake_target_switch"),
      )
    }
  }
}

@Composable
fun WorldClockPreviewCard(
  cities: List<Pair<WorldCityEntity, WorldZoneInfo>>,
  onAddCityClick: () -> Unit,
  onRemoveCity: (Long) -> Unit,
) {
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth().testTag("home_world_clock_card"),
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.Public,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.world_clock_preview_title),
            style = MaterialTheme.typography.titleMedium,
          )
        }
        IconButton(
          onClick = onAddCityClick,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("home_add_city_btn"),
        ) {
          Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add world clock city")
        }
      }

      if (cities.isEmpty()) {
        Text(
          text = "No cities selected yet. Tap + to add any IANA time zone.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(vertical = 8.dp),
        )
      } else {
        cities.forEachIndexed { index, (entity, info) ->
          if (index > 0) {
            HorizontalDivider(
              color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
              modifier = Modifier.padding(vertical = 8.dp),
            )
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                imageVector = if (info.isDaytime) Icons.Rounded.WbSunny else Icons.Rounded.DarkMode,
                contentDescription = null,
                tint =
                  if (info.isDaytime) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp),
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = info.cityName, style = MaterialTheme.typography.titleMedium)
                Text(
                  text = "${info.dayRelation} · ${info.offsetLabel}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = info.formattedTime,
                style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
                color = MaterialTheme.colorScheme.onSurface,
              )
              if (cities.size > 1) {
                IconButton(
                  onClick = { onRemoveCity(entity.id) },
                  modifier = Modifier.size(36.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Remove ${info.cityName}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProductivitySummaryCard(
  summary: TodayProductivitySummary,
  dailyGoalMinutes: Int,
  onStartFocus: () -> Unit,
) {
  val goalProgress =
    if (dailyGoalMinutes > 0) (summary.focusMinutesToday.toFloat() / dailyGoalMinutes).coerceIn(0f, 1f)
    else 0f
  Card(
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth().testTag("home_productivity_card"),
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.Insights,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.productivity_summary_title),
            style = MaterialTheme.typography.titleMedium,
          )
        }
        TextButton(onClick = onStartFocus, modifier = Modifier.minimumInteractiveComponentSize()) {
          Text("Start Focus")
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        MetricPill(
          label = "Focus Today",
          value = "${summary.focusMinutesToday}m",
          modifier = Modifier.weight(1f),
        )
        MetricPill(
          label = "Focus Sessions",
          value = "${summary.completedFocusCount}",
          modifier = Modifier.weight(1f),
        )
        MetricPill(
          label = "Timers Done",
          value = "${summary.completedTimersCount}",
          modifier = Modifier.weight(1f),
        )
      }

      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "Daily Focus Goal",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = "${summary.focusMinutesToday} / $dailyGoalMinutes min",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary,
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      LinearProgressIndicator(
        progress = { goalProgress },
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
      )
    }
  }
}

@Composable
private fun MetricPill(label: String, value: String, modifier: Modifier = Modifier) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
    modifier = modifier,
  ) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
      )
    }
  }
}
