package com.example.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.DashboardCustomize
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.repository.TimeFormatter
import com.example.domain.model.DashboardCardType
import com.example.ui.home.components.HeroClockCard
import com.example.ui.home.components.ProductivitySummaryCard
import com.example.ui.home.components.QuickActionsCard
import com.example.ui.home.components.QuickTimerDashboardCard
import com.example.ui.home.components.UpcomingScheduleCard
import com.example.ui.home.components.WorldClockPreviewCard
import com.example.ui.tools.ToolTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  uiState: HomeUiState,
  onToggleFormat: () -> Unit,
  onOpenClockStudio: () -> Unit,
  onNavigateToTool: (ToolTab) -> Unit,
  onStartQuickTimerPreset: (Int) -> Unit,
  onToggleQuickTimer: () -> Unit,
  onResetQuickTimer: () -> Unit,
  onDismissQuickTimerDone: () -> Unit,
  onToggleCardVisibility: (DashboardCardType) -> Unit,
  onMoveCard: (DashboardCardType, Boolean) -> Unit,
  onResetLayout: () -> Unit,
  onAddWorldCity: (String, String, String) -> Unit,
  onRemoveWorldCity: (Long) -> Unit,
  onUpdateWakeTarget: (Int, Int, Boolean) -> Unit,
) {
  var showCustomizeSheet by remember { mutableStateOf(false) }
  var showCityPickerSheet by remember { mutableStateOf(false) }
  var showWakeTargetDialog by remember { mutableStateOf(false) }

  val prefs = uiState.preferences
  val visibleCards =
    remember(prefs.dashboardCardOrder, prefs.hiddenDashboardCards) {
      prefs.dashboardCardOrder.filterNot { it in prefs.hiddenDashboardCards }
    }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item(key = "header") {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = stringResource(R.string.app_name),
              style = MaterialTheme.typography.headlineLarge,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
              text = stringResource(R.string.app_tagline),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          FilledTonalButton(
            onClick = { showCustomizeSheet = true },
            shape = RoundedCornerShape(14.dp),
            modifier =
              Modifier.minimumInteractiveComponentSize().testTag("home_customize_dashboard_btn"),
          ) {
            Icon(
              imageVector = Icons.Rounded.DashboardCustomize,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = stringResource(R.string.dashboard_customize))
          }
        }
      }

      item(key = "hero_clock") {
        HeroClockCard(
          snapshot = uiState.timeSnapshot,
          preferences = prefs,
          onToggleFormat = onToggleFormat,
          onOpenClockStudio = onOpenClockStudio,
        )
      }

      items(visibleCards, key = { it.name }) { cardType ->
        when (cardType) {
          DashboardCardType.QUICK_ACTIONS ->
            QuickActionsCard(
              onOpenTimer = { onNavigateToTool(ToolTab.TIMER) },
              onOpenStopwatch = { onNavigateToTool(ToolTab.STOPWATCH) },
              onOpenFocus = { onNavigateToTool(ToolTab.FOCUS) },
            )
          DashboardCardType.QUICK_TIMER ->
            QuickTimerDashboardCard(
              remainingSeconds = uiState.quickTimerRemainingSec,
              totalSeconds = uiState.quickTimerTotalSec,
              isRunning = uiState.quickTimerRunning,
              completedNotice = uiState.quickTimerCompletedNotice,
              onSelectPresetMinutes = onStartQuickTimerPreset,
              onToggleRun = onToggleQuickTimer,
              onReset = onResetQuickTimer,
              onDismissNotice = onDismissQuickTimerDone,
              onOpenFullTools = { onNavigateToTool(ToolTab.TIMER) },
            )
          DashboardCardType.UPCOMING_SCHEDULE ->
            UpcomingScheduleCard(
              wakeHour = prefs.wakeTargetHour,
              wakeMinute = prefs.wakeTargetMinute,
              wakeEnabled = prefs.wakeTargetEnabled,
              use24Hour = prefs.use24HourFormat,
              onToggleWakeEnabled = { enabled ->
                onUpdateWakeTarget(prefs.wakeTargetHour, prefs.wakeTargetMinute, enabled)
              },
              onEditWakeTarget = { showWakeTargetDialog = true },
            )
          DashboardCardType.WORLD_CLOCK ->
            WorldClockPreviewCard(
              cities = uiState.worldCities,
              onAddCityClick = { showCityPickerSheet = true },
              onRemoveCity = onRemoveWorldCity,
            )
          DashboardCardType.PRODUCTIVITY_SUMMARY ->
            ProductivitySummaryCard(
              summary = uiState.todaySummary,
              dailyGoalMinutes = prefs.dailyFocusGoalMinutes,
              onStartFocus = { onNavigateToTool(ToolTab.FOCUS) },
            )
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }

  // Customize Dashboard BottomSheet
  if (showCustomizeSheet) {
    ModalBottomSheet(onDismissRequest = { showCustomizeSheet = false }) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = stringResource(R.string.dashboard_customize_title),
              style = MaterialTheme.typography.headlineMedium,
            )
            Text(
              text = stringResource(R.string.dashboard_customize_subtitle),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          TextButton(onClick = onResetLayout) {
            Text(stringResource(R.string.dashboard_reset_layout))
          }
        }

        prefs.dashboardCardOrder.forEachIndexed { index, card ->
          val isVisible = card !in prefs.hiddenDashboardCards
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = card.title, style = MaterialTheme.typography.titleMedium)
                Text(
                  text = card.description,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Row {
                IconButton(
                  onClick = { onMoveCard(card, true) },
                  enabled = index > 0,
                ) {
                  Icon(Icons.Rounded.ArrowUpward, contentDescription = "Move ${card.title} up")
                }
                IconButton(
                  onClick = { onMoveCard(card, false) },
                  enabled = index < prefs.dashboardCardOrder.lastIndex,
                ) {
                  Icon(Icons.Rounded.ArrowDownward, contentDescription = "Move ${card.title} down")
                }
                IconButton(
                  onClick = { onToggleCardVisibility(card) },
                  modifier = Modifier.testTag("toggle_card_${card.name.lowercase()}"),
                ) {
                  Icon(
                    imageVector =
                      if (isVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                    contentDescription = if (isVisible) "Hide ${card.title}" else "Show ${card.title}",
                    tint =
                      if (isVisible) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))
      }
    }
  }

  // World City Picker BottomSheet
  if (showCityPickerSheet) {
    var searchQuery by remember { mutableStateOf("") }
    val allZones = remember { TimeFormatter.availableCityZones() }
    val filteredZones =
      remember(searchQuery, allZones) {
        if (searchQuery.isBlank()) allZones.take(40)
        else {
          val q = searchQuery.trim().lowercase()
          allZones.filter {
            it.second.lowercase().contains(q) || it.third.lowercase().contains(q)
          }.take(40)
        }
      }

    ModalBottomSheet(onDismissRequest = { showCityPickerSheet = false }) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(text = "Add World Clock City", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search city or continent...") },
          leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().testTag("world_city_search_input"),
        )
        LazyColumn(
          modifier = Modifier.fillMaxWidth().height(340.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          items(filteredZones, key = { it.first }) { (zoneId, city, region) ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
              modifier =
                Modifier.fillMaxWidth()
                  .minimumInteractiveComponentSize()
                  .clickable {
                    onAddWorldCity(zoneId, city, region)
                    showCityPickerSheet = false
                  },
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(text = city, style = MaterialTheme.typography.titleMedium)
                Text(
                  text = region,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }

  // Wake Target Picker Dialog
  if (showWakeTargetDialog) {
    val timePickerState =
      rememberTimePickerState(
        initialHour = prefs.wakeTargetHour,
        initialMinute = prefs.wakeTargetMinute,
        is24Hour = prefs.use24HourFormat,
      )
    AlertDialog(
      onDismissRequest = { showWakeTargetDialog = false },
      title = { Text("Set Wake Target") },
      text = { TimePicker(state = timePickerState) },
      confirmButton = {
        TextButton(
          onClick = {
            onUpdateWakeTarget(timePickerState.hour, timePickerState.minute, true)
            showWakeTargetDialog = false
          }
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showWakeTargetDialog = false }) { Text("Cancel") }
      },
    )
  }
}
