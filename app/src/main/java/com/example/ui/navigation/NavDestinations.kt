package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.R

object NavRoutes {
  const val HOME = "home"
  const val CLOCK = "clock"
  const val TOOLS = "tools"
  const val GAMES = "games"
  const val PROFILE = "profile"
}

enum class BottomNavDestination(
  val route: String,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
) {
  HOME(
    route = NavRoutes.HOME,
    labelRes = R.string.nav_home,
    selectedIcon = Icons.Rounded.Home,
    unselectedIcon = Icons.Outlined.Home,
    testTag = "nav_home",
  ),
  CLOCK(
    route = NavRoutes.CLOCK,
    labelRes = R.string.nav_clock,
    selectedIcon = Icons.Rounded.AccessTimeFilled,
    unselectedIcon = Icons.Outlined.AccessTime,
    testTag = "nav_clock",
  ),
  TOOLS(
    route = NavRoutes.TOOLS,
    labelRes = R.string.nav_tools,
    selectedIcon = Icons.Rounded.Build,
    unselectedIcon = Icons.Outlined.Build,
    testTag = "nav_tools",
  ),
  GAMES(
    route = NavRoutes.GAMES,
    labelRes = R.string.nav_games,
    selectedIcon = Icons.Rounded.SportsEsports,
    unselectedIcon = Icons.Outlined.SportsEsports,
    testTag = "nav_games",
  ),
  PROFILE(
    route = NavRoutes.PROFILE,
    labelRes = R.string.nav_profile,
    selectedIcon = Icons.Rounded.Person,
    unselectedIcon = Icons.Outlined.Person,
    testTag = "nav_profile",
  ),
}
