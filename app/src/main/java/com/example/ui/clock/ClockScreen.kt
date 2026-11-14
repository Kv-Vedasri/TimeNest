package com.example.ui.clock

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.ClockBackdrop
import com.example.domain.model.ClockFont
import com.example.domain.model.ClockStyle
import com.example.ui.clock.components.AnalogClockFace
import com.example.ui.clock.components.DigitalClockFace
import com.example.ui.clock.components.FlipClockFace
import com.example.ui.clock.components.MinimalClockFace
import com.example.ui.theme.toFontFamily
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClockScreen(
  uiState: ClockUiState,
  onSelectStyle: (ClockStyle) -> Unit,
  onSelectFont: (ClockFont) -> Unit,
  onSelectBackdrop: (ClockBackdrop) -> Unit,
  onToggle24Hour: (Boolean) -> Unit,
  onToggleSeconds: (Boolean) -> Unit,
  onSelectTimeZone: (String) -> Unit,
  onToggleKeepScreenOn: (Boolean) -> Unit,
  onEnterNightstand: () -> Unit,
  onExitNightstand: () -> Unit,
  onToggleNightstandDim: () -> Unit,
) {
  val context = LocalContext.current
  val prefs = uiState.preferences
  val snapshot = uiState.timeSnapshot
  var showCustomizeSheet by remember { mutableStateOf(false) }

  // Real Android TextToSpeech for Time Announcement
  var ttsReady by remember { mutableStateOf(false) }
  val tts = remember {
    var instance: TextToSpeech? = null
    instance =
      TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          instance?.language = Locale.getDefault()
          ttsReady = true
        }
      }
    instance
  }
  DisposableEffect(Unit) { onDispose { tts.shutdown() } }

  // Keep screen awake in Nightstand mode when preference is enabled
  val activity = context as? Activity
  DisposableEffect(uiState.isNightstandMode, prefs.keepScreenOnNightstand) {
    if (uiState.isNightstandMode && prefs.keepScreenOnNightstand) {
      activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
  }

  if (uiState.isNightstandMode) {
    BackHandler { onExitNightstand() }
    NightstandOverlay(
      uiState = uiState,
      onToggleDim = onToggleNightstandDim,
      onExit = onExitNightstand,
    )
    return
  }

  val fontFamily = prefs.clockFont.toFontFamily()
  val useDarkOverlayText =
    prefs.clockBackdrop == ClockBackdrop.TWILIGHT_ART ||
      prefs.clockBackdrop == ClockBackdrop.AMOLED_BLACK
  val clockContentColor =
    if (useDarkOverlayText) Color(0xFFF4F6FC) else MaterialTheme.colorScheme.onBackground
  val clockAccentColor = MaterialTheme.colorScheme.primary

  Box(modifier = Modifier.fillMaxSize()) {
    // Backdrop layer
    when (prefs.clockBackdrop) {
      ClockBackdrop.TWILIGHT_ART -> {
        Image(
          painter = painterResource(id = R.drawable.img_clock_backdrop),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
        )
      }
      ClockBackdrop.AMOLED_BLACK -> {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
      }
      ClockBackdrop.AMBIENT -> {
        Box(
          modifier =
            Modifier.fillMaxSize()
              .background(
                Brush.radialGradient(
                  colors =
                    listOf(
                      MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                      MaterialTheme.colorScheme.background,
                    )
                )
              )
        )
      }
      ClockBackdrop.SOLID -> {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
      }
    }

    Column(
      modifier =
        Modifier.fillMaxSize()
          .windowInsetsPadding(WindowInsets.safeDrawing)
          .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top Header
      Row(
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = stringResource(R.string.clock_studio_title),
            style = MaterialTheme.typography.headlineMedium,
            color = clockContentColor,
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector =
                if (snapshot.isDaytime) Icons.Rounded.WbSunny else Icons.Rounded.DarkMode,
              contentDescription = null,
              tint = clockAccentColor,
              modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${snapshot.dayPhaseLabel} · ${snapshot.timeZoneName}",
              style = MaterialTheme.typography.bodyMedium,
              color = clockContentColor.copy(alpha = 0.72f),
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = {
              if (ttsReady) {
                val spoken =
                  if (prefs.use24HourFormat) {
                    "The time is ${snapshot.hourDisplay} ${snapshot.minuteDisplay}"
                  } else {
                    "The time is ${snapshot.hourDisplay} ${snapshot.minuteDisplay} ${snapshot.amPm}"
                  }
                tts.speak(spoken, TextToSpeech.QUEUE_FLUSH, null, "timenest_time_cue")
              }
            },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("clock_speak_time_btn"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
              contentDescription = stringResource(R.string.clock_speak_time),
              tint = clockContentColor,
            )
          }
          IconButton(
            onClick = { showCustomizeSheet = true },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("clock_customize_btn"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Tune,
              contentDescription = stringResource(R.string.clock_customize_face),
              tint = clockContentColor,
            )
          }
        }
      }

      // Clock Face Switcher Pills
      FlowRow(
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        ClockStyle.entries.forEach { style ->
          FilterChip(
            selected = prefs.clockStyle == style,
            onClick = { onSelectStyle(style) },
            label = { Text(style.label) },
            modifier = Modifier.testTag("clock_style_${style.name.lowercase()}"),
          )
        }
      }

      // Main Centered Clock Face
      Box(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        when (prefs.clockStyle) {
          ClockStyle.DIGITAL ->
            DigitalClockFace(
              snapshot = snapshot,
              fontFamily = fontFamily,
              showSeconds = prefs.showSeconds,
              use24Hour = prefs.use24HourFormat,
              contentColor = clockContentColor,
              accentColor = clockAccentColor,
            )
          ClockStyle.FLIP ->
            FlipClockFace(
              snapshot = snapshot,
              fontFamily = fontFamily,
              showSeconds = prefs.showSeconds,
              use24Hour = prefs.use24HourFormat,
              contentColor = clockContentColor,
              accentColor = clockAccentColor,
            )
          ClockStyle.MINIMAL ->
            MinimalClockFace(
              snapshot = snapshot,
              fontFamily = fontFamily,
              use24Hour = prefs.use24HourFormat,
              contentColor = clockContentColor,
              accentColor = clockAccentColor,
            )
          ClockStyle.ANALOG ->
            AnalogClockFace(
              snapshot = snapshot,
              contentColor = clockContentColor,
              accentColor = clockAccentColor,
            )
        }
      }

      // Bottom Studio Action Bar
      Row(
        modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp).padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedButton(
          onClick = { onToggle24Hour(!prefs.use24HourFormat) },
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, clockContentColor.copy(alpha = 0.25f)),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
          modifier =
            Modifier.weight(1f).minimumInteractiveComponentSize().testTag("clock_format_btn"),
        ) {
          Text(
            text = if (prefs.use24HourFormat) "24-Hour" else "12-Hour",
            color = clockContentColor,
          )
        }

        FilledTonalButton(
          onClick = onEnterNightstand,
          shape = RoundedCornerShape(16.dp),
          contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
          modifier =
            Modifier.weight(1.4f).minimumInteractiveComponentSize().testTag("clock_nightstand_btn"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Fullscreen,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = stringResource(R.string.clock_nightstand_enter))
        }
      }
    }
  }

  if (showCustomizeSheet) {
    ModalBottomSheet(onDismissRequest = { showCustomizeSheet = false }) {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
          text = stringResource(R.string.clock_customize_face),
          style = MaterialTheme.typography.headlineMedium,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = "Primary Clock Timezone", style = MaterialTheme.typography.titleMedium)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
              selected = prefs.displayTimeZoneId == com.example.data.repository.TimeFormatter.ZONE_IST,
              onClick = { onSelectTimeZone(com.example.data.repository.TimeFormatter.ZONE_IST) },
              label = { Text("IST (India · UTC+5:30)") },
              modifier = Modifier.testTag("clock_tz_ist"),
            )
            FilterChip(
              selected = prefs.displayTimeZoneId == com.example.data.repository.TimeFormatter.ZONE_SYSTEM,
              onClick = { onSelectTimeZone(com.example.data.repository.TimeFormatter.ZONE_SYSTEM) },
              label = { Text("Device Local") },
              modifier = Modifier.testTag("clock_tz_system"),
            )
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = "Clock Typography", style = MaterialTheme.typography.titleMedium)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ClockFont.entries.forEach { font ->
              FilterChip(
                selected = prefs.clockFont == font,
                onClick = { onSelectFont(font) },
                label = { Text(font.label) },
                modifier = Modifier.testTag("clock_font_${font.name.lowercase()}"),
              )
            }
          }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = "Backdrop Atmosphere", style = MaterialTheme.typography.titleMedium)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ClockBackdrop.entries.forEach { backdrop ->
              FilterChip(
                selected = prefs.clockBackdrop == backdrop,
                onClick = { onSelectBackdrop(backdrop) },
                label = { Text(backdrop.label) },
                modifier = Modifier.testTag("clock_backdrop_${backdrop.name.lowercase()}"),
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(text = "Show Seconds", style = MaterialTheme.typography.titleMedium)
          Switch(checked = prefs.showSeconds, onCheckedChange = onToggleSeconds)
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = "Keep Screen On in Nightstand", style = MaterialTheme.typography.titleMedium)
            Text(
              text = "Prevents sleep while fullscreen Nightstand is open",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Switch(checked = prefs.keepScreenOnNightstand, onCheckedChange = onToggleKeepScreenOn)
        }

        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
private fun NightstandOverlay(
  uiState: ClockUiState,
  onToggleDim: () -> Unit,
  onExit: () -> Unit,
) {
  var showControls by remember { mutableStateOf(true) }
  val prefs = uiState.preferences
  val snapshot = uiState.timeSnapshot
  val alpha = if (uiState.nightstandDimmed) 0.42f else 0.92f
  val contentColor = Color(0xFFF3F6FC).copy(alpha = alpha)
  val accentColor = MaterialTheme.colorScheme.primary.copy(alpha = alpha)

  Box(
    modifier =
      Modifier.fillMaxSize()
        .background(Color.Black)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
        ) {
          showControls = !showControls
        }
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(24.dp),
  ) {
    Box(modifier = Modifier.align(Alignment.Center)) {
      when (prefs.clockStyle) {
        ClockStyle.FLIP ->
          FlipClockFace(
            snapshot = snapshot,
            fontFamily = prefs.clockFont.toFontFamily(),
            showSeconds = prefs.showSeconds,
            use24Hour = prefs.use24HourFormat,
            contentColor = contentColor,
            accentColor = accentColor,
          )
        ClockStyle.MINIMAL ->
          MinimalClockFace(
            snapshot = snapshot,
            fontFamily = prefs.clockFont.toFontFamily(),
            use24Hour = prefs.use24HourFormat,
            contentColor = contentColor,
            accentColor = accentColor,
          )
        ClockStyle.ANALOG ->
          AnalogClockFace(
            snapshot = snapshot,
            contentColor = contentColor,
            accentColor = accentColor,
          )
        ClockStyle.DIGITAL ->
          DigitalClockFace(
            snapshot = snapshot,
            fontFamily = prefs.clockFont.toFontFamily(),
            showSeconds = prefs.showSeconds,
            use24Hour = prefs.use24HourFormat,
            contentColor = contentColor,
            accentColor = accentColor,
          )
      }
    }

    AnimatedVisibility(
      visible = showControls,
      modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = CircleShape,
          color = Color.White.copy(alpha = 0.08f),
          onClick = onToggleDim,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("nightstand_dim_btn"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Bedtime,
              contentDescription = null,
              tint = Color(0xFFF3F6FC),
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (uiState.nightstandDimmed) "Brighten" else "Dim Display",
              style = MaterialTheme.typography.labelLarge,
              color = Color(0xFFF3F6FC),
            )
          }
        }

        Surface(
          shape = CircleShape,
          color = Color.White.copy(alpha = 0.12f),
          onClick = onExit,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("nightstand_exit_btn"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = "Exit Nightstand",
              tint = Color(0xFFF3F6FC),
              modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Exit",
              style = MaterialTheme.typography.labelLarge,
              color = Color(0xFFF3F6FC),
            )
          }
        }
      }
    }

    Text(
      text = stringResource(R.string.clock_nightstand_exit),
      style = MaterialTheme.typography.bodyMedium,
      color = Color.White.copy(alpha = 0.35f),
      modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
    )
  }
}
