package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.domain.model.AccentPreset
import com.example.domain.model.ThemeMode

val TimeNestShapes =
  Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
  )

@Composable
fun TimeNestTheme(
  themeMode: ThemeMode = ThemeMode.DARK,
  accentPreset: AccentPreset = AccentPreset.SOLAR_AMBER,
  content: @Composable () -> Unit,
) {
  val systemDark = isSystemInDarkTheme()
  val isDark =
    when (themeMode) {
      ThemeMode.SYSTEM -> systemDark
      ThemeMode.LIGHT -> false
      ThemeMode.DARK,
      ThemeMode.AMOLED -> true
    }
  val isAmoled = themeMode == ThemeMode.AMOLED

  val colorScheme =
    when {
      isAmoled ->
        darkColorScheme(
          primary = accentPreset.primaryDark,
          onPrimary = Color(0xFF090D16),
          primaryContainer = accentPreset.primaryDark.copy(alpha = 0.16f),
          onPrimaryContainer = accentPreset.primaryDark,
          secondary = accentPreset.secondaryDark,
          onSecondary = Color(0xFF090D16),
          secondaryContainer = accentPreset.secondaryDark.copy(alpha = 0.16f),
          onSecondaryContainer = accentPreset.secondaryDark,
          tertiary = CalmEmerald,
          onTertiary = Color(0xFF051B14),
          background = AmoledBg,
          onBackground = MidnightOnBg,
          surface = AmoledSurface,
          onSurface = MidnightOnBg,
          surfaceVariant = AmoledSurfaceVariant,
          onSurfaceVariant = MidnightOnSurfaceMuted,
          surfaceContainerLowest = AmoledBg,
          surfaceContainerLow = AmoledSurface,
          surfaceContainer = AmoledSurfaceVariant,
          surfaceContainerHigh = AmoledSurfaceHigh,
          outline = AmoledOutline,
          outlineVariant = AmoledOutline.copy(alpha = 0.6f),
          error = WarmCoral,
          onError = Color(0xFF28050B),
        )
      isDark ->
        darkColorScheme(
          primary = accentPreset.primaryDark,
          onPrimary = Color(0xFF090D16),
          primaryContainer = accentPreset.primaryDark.copy(alpha = 0.18f),
          onPrimaryContainer = accentPreset.primaryDark,
          secondary = accentPreset.secondaryDark,
          onSecondary = Color(0xFF090D16),
          secondaryContainer = accentPreset.secondaryDark.copy(alpha = 0.16f),
          onSecondaryContainer = accentPreset.secondaryDark,
          tertiary = CalmEmerald,
          onTertiary = Color(0xFF051B14),
          background = MidnightBg,
          onBackground = MidnightOnBg,
          surface = MidnightSurface,
          onSurface = MidnightOnBg,
          surfaceVariant = MidnightSurfaceVariant,
          onSurfaceVariant = MidnightOnSurfaceMuted,
          surfaceContainerLowest = MidnightBg,
          surfaceContainerLow = MidnightSurface,
          surfaceContainer = MidnightSurfaceVariant,
          surfaceContainerHigh = MidnightSurfaceHigh,
          outline = MidnightOutline,
          outlineVariant = MidnightOutline.copy(alpha = 0.6f),
          error = WarmCoral,
          onError = Color(0xFF28050B),
        )
      else ->
        lightColorScheme(
          primary = accentPreset.primaryLight,
          onPrimary = Color.White,
          primaryContainer = accentPreset.primaryLight.copy(alpha = 0.14f),
          onPrimaryContainer = accentPreset.primaryLight,
          secondary = accentPreset.secondaryLight,
          onSecondary = Color.White,
          secondaryContainer = accentPreset.secondaryLight.copy(alpha = 0.14f),
          onSecondaryContainer = accentPreset.secondaryLight,
          tertiary = Color(0xFF059669),
          onTertiary = Color.White,
          background = DaylightBg,
          onBackground = DaylightOnBg,
          surface = DaylightSurface,
          onSurface = DaylightOnBg,
          surfaceVariant = DaylightSurfaceVariant,
          onSurfaceVariant = DaylightOnSurfaceMuted,
          surfaceContainerLowest = DaylightSurface,
          surfaceContainerLow = DaylightBg,
          surfaceContainer = DaylightSurfaceVariant,
          surfaceContainerHigh = DaylightSurfaceHigh,
          outline = DaylightOutline,
          outlineVariant = DaylightOutline.copy(alpha = 0.65f),
          error = Color(0xFFDC2626),
          onError = Color.White,
        )
    }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = TimeNestTypography,
    shapes = TimeNestShapes,
    content = content,
  )
}
