package com.example.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.TimeNestRepository
import com.example.ui.clock.ClockScreen
import com.example.ui.clock.ClockViewModel
import com.example.ui.games.GamesScreen
import com.example.ui.games.GamesViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.profile.ProfileScreen
import com.example.ui.profile.ProfileViewModel
import com.example.ui.tools.ToolsScreen
import com.example.ui.tools.ToolsViewModel

@Composable
fun TimeNestShell(repository: TimeNestRepository) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.HOME

  val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
  val clockViewModel: ClockViewModel = viewModel(factory = ClockViewModel.Factory(repository))
  val toolsViewModel: ToolsViewModel = viewModel(factory = ToolsViewModel.Factory(repository))
  val gamesViewModel: GamesViewModel = viewModel(factory = GamesViewModel.Factory(repository))
  val profileViewModel: ProfileViewModel =
    viewModel(factory = ProfileViewModel.Factory(repository))

  val clockState by clockViewModel.uiState.collectAsStateWithLifecycle()
  val hideChrome = currentRoute == NavRoutes.CLOCK && clockState.isNightstandMode

  val navigateTopLevel: (String) -> Unit = { route ->
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useRail = maxWidth >= 600.dp

    Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      contentWindowInsets = if (hideChrome) WindowInsets(0) else WindowInsets.safeDrawing,
      bottomBar = {
        if (!useRail && !hideChrome) {
          NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            BottomNavDestination.entries.forEach { dest ->
              val selected = currentRoute == dest.route
              val label = stringResource(dest.labelRes)
              NavigationBarItem(
                selected = selected,
                onClick = { navigateTopLevel(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = { Text(label, maxLines = 1) },
                modifier = Modifier.testTag(dest.testTag),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        if (useRail && !hideChrome) {
          NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight(),
          ) {
            BottomNavDestination.entries.forEach { dest ->
              val selected = currentRoute == dest.route
              val label = stringResource(dest.labelRes)
              NavigationRailItem(
                selected = selected,
                onClick = { navigateTopLevel(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = { Text(label, maxLines = 1) },
                modifier = Modifier.testTag(dest.testTag),
              )
            }
          }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
          NavHost(
            navController = navController,
            startDestination = NavRoutes.HOME,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
          ) {
            composable(NavRoutes.HOME) {
              val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
              HomeScreen(
                uiState = homeState,
                onToggleFormat = homeViewModel::toggle24HourFormat,
                onOpenClockStudio = { navigateTopLevel(NavRoutes.CLOCK) },
                onNavigateToTool = { tab ->
                  toolsViewModel.selectTab(tab)
                  navigateTopLevel(NavRoutes.TOOLS)
                },
                onStartQuickTimerPreset = homeViewModel::startQuickTimerPreset,
                onToggleQuickTimer = homeViewModel::toggleQuickTimer,
                onResetQuickTimer = homeViewModel::resetQuickTimer,
                onDismissQuickTimerDone = homeViewModel::dismissQuickTimerDone,
                onToggleCardVisibility = homeViewModel::toggleDashboardCardVisibility,
                onMoveCard = homeViewModel::moveDashboardCard,
                onResetLayout = homeViewModel::resetDashboardLayout,
                onAddWorldCity = homeViewModel::addWorldCity,
                onRemoveWorldCity = homeViewModel::removeWorldCity,
                onUpdateWakeTarget = homeViewModel::updateWakeTarget,
              )
            }

            composable(NavRoutes.CLOCK) {
              ClockScreen(
                uiState = clockState,
                onSelectStyle = clockViewModel::setClockStyle,
                onSelectFont = clockViewModel::setClockFont,
                onSelectBackdrop = clockViewModel::setClockBackdrop,
                onToggle24Hour = clockViewModel::setUse24HourFormat,
                onToggleSeconds = clockViewModel::setShowSeconds,
                onSelectTimeZone = clockViewModel::setDisplayTimeZoneId,
                onToggleKeepScreenOn = clockViewModel::setKeepScreenOnNightstand,
                onEnterNightstand = clockViewModel::enterNightstand,
                onExitNightstand = clockViewModel::exitNightstand,
                onToggleNightstandDim = clockViewModel::toggleNightstandDim,
              )
            }

            composable(NavRoutes.TOOLS) {
              val toolsState by toolsViewModel.uiState.collectAsStateWithLifecycle()
              ToolsScreen(
                uiState = toolsState,
                onSelectTab = toolsViewModel::selectTab,
                onSetTimerPreset = toolsViewModel::setTimerPreset,
                onAdjustTimerMinutes = toolsViewModel::adjustTimerMinutes,
                onToggleTimer = toolsViewModel::toggleTimer,
                onResetTimer = toolsViewModel::resetTimer,
                onToggleStopwatch = toolsViewModel::toggleStopwatch,
                onRecordLap = toolsViewModel::recordLap,
                onResetStopwatch = toolsViewModel::resetStopwatch,
                onSetFocusConfig = toolsViewModel::setFocusConfig,
                onToggleFocus = toolsViewModel::toggleFocus,
                onFinishFocusEarly = toolsViewModel::finishFocusEarlyAndLog,
                onResetFocus = toolsViewModel::resetFocus,
              )
            }

            composable(NavRoutes.GAMES) {
              val gamesState by gamesViewModel.uiState.collectAsStateWithLifecycle()
              GamesScreen(
                uiState = gamesState,
                onArenaTap = gamesViewModel::onArenaTap,
              )
            }

            composable(NavRoutes.PROFILE) {
              val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
              ProfileScreen(
                uiState = profileState,
                onSelectThemeMode = profileViewModel::setThemeMode,
                onSelectAccent = profileViewModel::setAccentPreset,
                onToggle24Hour = profileViewModel::setUse24HourFormat,
                onToggleShowSeconds = profileViewModel::setShowSeconds,
                onSelectTimeZone = profileViewModel::setDisplayTimeZoneId,
                onSelectClockFont = profileViewModel::setClockFont,
                onToggleKeepScreenOn = profileViewModel::setKeepScreenOnNightstand,
                onSelectDailyFocusGoal = profileViewModel::setDailyFocusGoal,
                onResetDashboard = profileViewModel::resetDashboardLayout,
                onReplayOnboarding = profileViewModel::replayOnboarding,
              )
            }
          }
        }
      }
    }
  }
}
