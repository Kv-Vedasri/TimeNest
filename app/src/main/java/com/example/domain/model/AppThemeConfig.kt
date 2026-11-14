package com.example.domain.model

import androidx.compose.ui.graphics.Color

enum class ThemeMode(val label: String) {
  SYSTEM("System"),
  LIGHT("Light"),
  DARK("Dark"),
  AMOLED("AMOLED Black"),
}

enum class AccentPreset(
  val label: String,
  val primaryDark: Color,
  val primaryLight: Color,
  val secondaryDark: Color,
  val secondaryLight: Color,
) {
  SOLAR_AMBER(
    label = "Solar Amber",
    primaryDark = Color(0xFFFBBF24),
    primaryLight = Color(0xFFD97706),
    secondaryDark = Color(0xFF38BDF8),
    secondaryLight = Color(0xFF0284C7),
  ),
  ASTRAL_CYAN(
    label = "Astral Cyan",
    primaryDark = Color(0xFF38BDF8),
    primaryLight = Color(0xFF0284C7),
    secondaryDark = Color(0xFFFBBF24),
    secondaryLight = Color(0xFFD97706),
  ),
  EMERALD_SAGE(
    label = "Emerald Sage",
    primaryDark = Color(0xFF34D399),
    primaryLight = Color(0xFF059669),
    secondaryDark = Color(0xFF2DD4BF),
    secondaryLight = Color(0xFF0D9488),
  ),
  TWILIGHT_VIOLET(
    label = "Twilight Violet",
    primaryDark = Color(0xFFA78BFA),
    primaryLight = Color(0xFF7C3AED),
    secondaryDark = Color(0xFFF472B6),
    secondaryLight = Color(0xFFDB2777),
  ),
  CRIMSON_PULSE(
    label = "Crimson Pulse",
    primaryDark = Color(0xFFFB7185),
    primaryLight = Color(0xFFE11D48),
    secondaryDark = Color(0xFFFBBF24),
    secondaryLight = Color(0xFFD97706),
  ),
}

enum class ClockStyle(val label: String, val subtitle: String) {
  DIGITAL("Digital", "Crisp tabular live readout"),
  FLIP("Flip Clock", "Classic split-flap cards"),
  MINIMAL("Minimal", "Calm orbital ring & dial"),
  ANALOG("Analog", "Precision sweeping hands"),
}

enum class ClockFont(val label: String) {
  OUTFIT("Outfit"),
  JETBRAINS_MONO("JetBrains Mono"),
  SPACE_GROTESK("Space Grotesk"),
  PLUS_JAKARTA("Plus Jakarta Sans"),
}

enum class ClockBackdrop(val label: String) {
  AMBIENT("Ambient Glow"),
  TWILIGHT_ART("Twilight Orbit"),
  SOLID("Clean Surface"),
  AMOLED_BLACK("Pure AMOLED"),
}
