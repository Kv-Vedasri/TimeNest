package com.example.ui.tools

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.TimeFormatter

@Composable
fun ToolsScreen(
  uiState: ToolsUiState,
  onSelectTab: (ToolTab) -> Unit,
  onSetTimerPreset: (Int, String) -> Unit,
  onAdjustTimerMinutes: (Int) -> Unit,
  onToggleTimer: () -> Unit,
  onResetTimer: () -> Unit,
  onToggleStopwatch: () -> Unit,
  onRecordLap: () -> Unit,
  onResetStopwatch: () -> Unit,
  onSetFocusConfig: (Int, String) -> Unit,
  onToggleFocus: () -> Unit,
  onFinishFocusEarly: () -> Unit,
  onResetFocus: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp).padding(horizontal = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.tools_title),
          style = MaterialTheme.typography.headlineLarge,
        )
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
        ) {
          Text(
            text = "${uiState.todaySummary.focusMinutesToday}m focused today",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        ToolTab.entries.forEachIndexed { index, tab ->
          SegmentedButton(
            selected = uiState.activeTab == tab,
            onClick = { onSelectTab(tab) },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = ToolTab.entries.size),
            modifier = Modifier.testTag("tools_tab_${tab.name.lowercase()}"),
          ) {
            Text(text = tab.label)
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      when (uiState.activeTab) {
        ToolTab.TIMER ->
          TimerToolPane(
            uiState = uiState,
            onSetPreset = onSetTimerPreset,
            onAdjustMinutes = onAdjustTimerMinutes,
            onToggle = onToggleTimer,
            onReset = onResetTimer,
          )
        ToolTab.STOPWATCH ->
          StopwatchToolPane(
            uiState = uiState,
            onToggle = onToggleStopwatch,
            onLap = onRecordLap,
            onReset = onResetStopwatch,
          )
        ToolTab.FOCUS ->
          FocusToolPane(
            uiState = uiState,
            onSetConfig = onSetFocusConfig,
            onToggle = onToggleFocus,
            onFinishEarly = onFinishFocusEarly,
            onReset = onResetFocus,
          )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimerToolPane(
  uiState: ToolsUiState,
  onSetPreset: (Int, String) -> Unit,
  onAdjustMinutes: (Int) -> Unit,
  onToggle: () -> Unit,
  onReset: () -> Unit,
) {
  val progress =
    if (uiState.timerTotalSeconds > 0) {
      uiState.timerRemainingSeconds.toFloat() / uiState.timerTotalSeconds
    } else 0f

  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.HourglassBottom,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = uiState.timerLabel, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(14.dp))
        CircularProgressClock(
          progress = progress,
          centerText = TimeFormatter.formatDurationClock(uiState.timerRemainingSeconds),
          subText = "${uiState.timerTotalSeconds / 60} min total",
          ringColor = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(14.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          FilledTonalIconButton(
            onClick = { onAdjustMinutes(-1) },
            enabled = !uiState.timerRunning,
            modifier = Modifier.minimumInteractiveComponentSize(),
          ) {
            Icon(Icons.Rounded.Remove, contentDescription = "Subtract 1 minute")
          }
          Button(
            onClick = onToggle,
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(horizontal = 26.dp, vertical = 14.dp),
            modifier = Modifier.minimumInteractiveComponentSize().testTag("tools_timer_toggle_btn"),
          ) {
            Icon(
              imageVector = if (uiState.timerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
              contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (uiState.timerRunning) "Pause" else "Start")
          }
          FilledTonalIconButton(
            onClick = { onAdjustMinutes(1) },
            enabled = !uiState.timerRunning,
            modifier = Modifier.minimumInteractiveComponentSize(),
          ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add 1 minute")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
          onClick = onReset,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.minimumInteractiveComponentSize().testTag("tools_timer_reset_btn"),
        ) {
          Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Reset")
        }

        AnimatedVisibility(visible = uiState.timerCompletedMessage != null) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
          ) {
            Text(
              text = uiState.timerCompletedMessage.orEmpty(),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.tertiary,
              modifier = Modifier.padding(12.dp),
            )
          }
        }
      }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(text = "Quick Presets", style = MaterialTheme.typography.titleMedium)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val presets =
          listOf(
            1 to "1m Sprint",
            5 to "Workout 5m",
            10 to "Cooking 10m",
            15 to "Meditation 15m",
            25 to "Study 25m",
          )
        presets.forEach { (mins, label) ->
          FilterChip(
            selected = uiState.timerLabel == label && uiState.timerTotalSeconds == mins * 60,
            onClick = { onSetPreset(mins, label) },
            label = { Text(label) },
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun StopwatchToolPane(
  uiState: ToolsUiState,
  onToggle: () -> Unit,
  onLap: () -> Unit,
  onReset: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.Timer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Precision Stopwatch", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = TimeFormatter.formatStopwatchMillis(uiState.stopwatchElapsedMillis),
          style =
            MaterialTheme.typography.displayLarge.copy(
              fontSize = 48.sp,
              fontFeatureSettings = "tnum",
            ),
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedButton(
            onClick = onReset,
            enabled = uiState.stopwatchElapsedMillis > 0L,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.minimumInteractiveComponentSize().testTag("stopwatch_reset_btn"),
          ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset")
          }
          Button(
            onClick = onToggle,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.minimumInteractiveComponentSize().testTag("stopwatch_toggle_btn"),
          ) {
            Icon(
              imageVector =
                if (uiState.stopwatchRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
              contentDescription = null,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (uiState.stopwatchRunning) "Pause" else "Start")
          }
          FilledTonalButton(
            onClick = onLap,
            enabled = uiState.stopwatchElapsedMillis > 0L,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.minimumInteractiveComponentSize().testTag("stopwatch_lap_btn"),
          ) {
            Icon(Icons.Rounded.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Lap")
          }
        }
      }
    }

    Text(
      text = "Laps (${uiState.stopwatchLaps.size})",
      style = MaterialTheme.typography.titleMedium,
    )

    if (uiState.stopwatchLaps.isEmpty()) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = "Start the stopwatch and tap Lap to record split intervals.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(18.dp),
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(uiState.stopwatchLaps, key = { it.index }) { lap ->
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text = "Lap ${lap.index}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
              )
              Text(
                text = "+${TimeFormatter.formatStopwatchMillis(lap.lapMillis)}",
                style = MaterialTheme.typography.bodyLarge.copy(fontFeatureSettings = "tnum"),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Text(
                text = TimeFormatter.formatStopwatchMillis(lap.totalMillis),
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FocusToolPane(
  uiState: ToolsUiState,
  onSetConfig: (Int, String) -> Unit,
  onToggle: () -> Unit,
  onFinishEarly: () -> Unit,
  onReset: () -> Unit,
) {
  val progress =
    if (uiState.focusTotalSeconds > 0) {
      uiState.focusRemainingSeconds.toFloat() / uiState.focusTotalSeconds
    } else 0f

  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.CenterFocusStrong,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${uiState.focusCategory} · Pomodoro",
            style = MaterialTheme.typography.titleMedium,
          )
        }
        Spacer(modifier = Modifier.height(20.dp))
        CircularProgressClock(
          progress = progress,
          centerText = TimeFormatter.formatDurationClock(uiState.focusRemainingSeconds),
          subText = "${uiState.focusTotalSeconds / 60} min goal",
          ringColor = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedButton(
            onClick = onReset,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.minimumInteractiveComponentSize(),
          ) {
            Text("Reset")
          }
          Button(
            onClick = onToggle,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.minimumInteractiveComponentSize().testTag("focus_toggle_btn"),
          ) {
            Icon(
              imageVector =
                if (uiState.focusRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
              contentDescription = null,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (uiState.focusRunning) "Pause" else "Focus")
          }
          if (uiState.focusTotalSeconds - uiState.focusRemainingSeconds >= 60) {
            FilledTonalButton(
              onClick = onFinishEarly,
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Log")
            }
          }
        }

        AnimatedVisibility(visible = uiState.focusCompletedMessage != null) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
          ) {
            Text(
              text = uiState.focusCompletedMessage.orEmpty(),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.tertiary,
              modifier = Modifier.padding(12.dp),
            )
          }
        }
      }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(text = "Category", style = MaterialTheme.typography.titleMedium)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Deep Work", "Study", "Reading", "Creative").forEach { category ->
          FilterChip(
            selected = uiState.focusCategory == category,
            onClick = { onSetConfig(uiState.focusTotalSeconds / 60, category) },
            enabled = !uiState.focusRunning,
            label = { Text(category) },
          )
        }
      }
      HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        modifier = Modifier.padding(vertical = 4.dp),
      )
      Text(text = "Duration", style = MaterialTheme.typography.titleMedium)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(15, 25, 45, 60).forEach { mins ->
          FilterChip(
            selected = uiState.focusTotalSeconds == mins * 60,
            onClick = { onSetConfig(mins, uiState.focusCategory) },
            enabled = !uiState.focusRunning,
            label = { Text("${mins}m") },
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun CircularProgressClock(
  progress: Float,
  centerText: String,
  subText: String,
  ringColor: Color,
) {
  Box(modifier = Modifier.size(176.dp), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
      drawArc(
        color = ringColor.copy(alpha = 0.16f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        style = stroke,
      )
      drawArc(
        color = ringColor,
        startAngle = -90f,
        sweepAngle = progress.coerceIn(0f, 1f) * 360f,
        useCenter = false,
        style = stroke,
      )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = centerText,
        style =
          MaterialTheme.typography.displayMedium.copy(
            fontWeight = FontWeight.Bold,
            fontFeatureSettings = "tnum",
          ),
      )
      Text(
        text = subText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
