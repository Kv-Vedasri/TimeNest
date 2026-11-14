package com.example.ui.clock.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TimeSnapshot
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DigitalClockFace(
  snapshot: TimeSnapshot,
  fontFamily: FontFamily,
  showSeconds: Boolean,
  use24Hour: Boolean,
  contentColor: Color,
  accentColor: Color,
  compact: Boolean = false,
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
      Text(
        text = "${snapshot.hourDisplay}:${snapshot.minuteDisplay}",
        style =
          TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 60.sp else 78.sp,
            letterSpacing = (-2).sp,
            fontFeatureSettings = "tnum",
          ),
        color = contentColor,
        maxLines = 1,
      )
      if (showSeconds || !use24Hour) {
        Spacer(modifier = Modifier.width(8.dp))
        Column(
          modifier = Modifier.padding(bottom = if (compact) 10.dp else 14.dp),
          horizontalAlignment = Alignment.Start,
        ) {
          if (!use24Hour) {
            Text(
              text = snapshot.amPm,
              style =
                TextStyle(
                  fontFamily = fontFamily,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = if (compact) 16.sp else 20.sp,
                ),
              color = accentColor,
            )
          }
          if (showSeconds) {
            Text(
              text = snapshot.secondDisplay,
              style =
                TextStyle(
                  fontFamily = fontFamily,
                  fontWeight = FontWeight.Medium,
                  fontSize = if (compact) 20.sp else 26.sp,
                  fontFeatureSettings = "tnum",
                ),
              color = contentColor.copy(alpha = 0.72f),
            )
          }
        }
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "${snapshot.dayOfWeek} · ${snapshot.dateFormatted}",
      style = MaterialTheme.typography.titleMedium,
      color = contentColor.copy(alpha = 0.75f),
    )
  }
}

@Composable
fun FlipClockFace(
  snapshot: TimeSnapshot,
  fontFamily: FontFamily,
  showSeconds: Boolean,
  use24Hour: Boolean,
  contentColor: Color,
  accentColor: Color,
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      FlipCardUnit(
        value = snapshot.hourDisplay,
        fontFamily = fontFamily,
        contentColor = contentColor,
        badge = if (!use24Hour) snapshot.amPm else null,
        badgeColor = accentColor,
      )
      Text(
        text = ":",
        style = TextStyle(fontFamily = fontFamily, fontSize = 52.sp, fontWeight = FontWeight.Bold),
        color = accentColor,
      )
      FlipCardUnit(
        value = snapshot.minuteDisplay,
        fontFamily = fontFamily,
        contentColor = contentColor,
        badge = if (showSeconds) snapshot.secondDisplay else null,
        badgeColor = contentColor.copy(alpha = 0.7f),
      )
    }
    Spacer(modifier = Modifier.height(14.dp))
    Text(
      text = "${snapshot.dayOfWeek} · ${snapshot.dateFormatted}",
      style = MaterialTheme.typography.titleMedium,
      color = contentColor.copy(alpha = 0.75f),
    )
  }
}

@Composable
private fun FlipCardUnit(
  value: String,
  fontFamily: FontFamily,
  contentColor: Color,
  badge: String?,
  badgeColor: Color,
) {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
    tonalElevation = 4.dp,
    modifier = Modifier.size(width = 128.dp, height = 148.dp),
  ) {
    Box(contentAlignment = Alignment.Center) {
      AnimatedContent(
        targetState = value,
        transitionSpec = {
          (slideInVertically(tween(240)) { -it / 2 } + fadeIn(tween(200))) togetherWith
            (slideOutVertically(tween(240)) { it / 2 } + fadeOut(tween(180)))
        },
        label = "flip_digit",
      ) { animatedVal ->
        Text(
          text = animatedVal,
          style =
            TextStyle(
              fontFamily = fontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 64.sp,
              fontFeatureSettings = "tnum",
            ),
          color = contentColor,
        )
      }
      HorizontalDivider(
        thickness = 1.5.dp,
        color = Color.Black.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth(),
      )
      if (badge != null) {
        Text(
          text = badge,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = badgeColor,
          modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp),
        )
      }
    }
  }
}

@Composable
fun MinimalClockFace(
  snapshot: TimeSnapshot,
  fontFamily: FontFamily,
  use24Hour: Boolean,
  contentColor: Color,
  accentColor: Color,
) {
  val secondProgress = snapshot.second / 60f
  Box(
    modifier = Modifier.size(268.dp),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
      drawArc(
        color = contentColor.copy(alpha = 0.12f),
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        style = stroke,
      )
      drawArc(
        color = accentColor,
        startAngle = -90f,
        sweepAngle = secondProgress * 360f,
        useCenter = false,
        style = stroke,
      )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "${snapshot.hourDisplay}:${snapshot.minuteDisplay}",
        style =
          TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 54.sp,
            fontFeatureSettings = "tnum",
          ),
        color = contentColor,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text =
          if (use24Hour) "${snapshot.secondDisplay}s · ${snapshot.dayPhaseLabel}"
          else "${snapshot.amPm} · ${snapshot.dayPhaseLabel}",
        style = MaterialTheme.typography.labelLarge,
        color = accentColor,
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = snapshot.dayOfWeek,
        style = MaterialTheme.typography.bodyMedium,
        color = contentColor.copy(alpha = 0.68f),
      )
    }
  }
}

@Composable
fun AnalogClockFace(
  snapshot: TimeSnapshot,
  contentColor: Color,
  accentColor: Color,
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Canvas(
      modifier = Modifier.size(256.dp).aspectRatio(1f)
    ) {
      val radius = size.minDimension / 2f
      val center = Offset(size.width / 2f, size.height / 2f)

      // Outer dial ring
      drawCircle(
        color = contentColor.copy(alpha = 0.14f),
        radius = radius - 4.dp.toPx(),
        center = center,
        style = Stroke(width = 2.dp.toPx()),
      )

      // Hour markers
      for (i in 0 until 12) {
        val angleRad = (i * 30.0 - 90.0) * PI / 180.0
        val outerR = radius - 12.dp.toPx()
        val innerR = if (i % 3 == 0) radius - 28.dp.toPx() else radius - 20.dp.toPx()
        drawLine(
          color =
            if (i % 3 == 0) accentColor.copy(alpha = 0.9f)
            else contentColor.copy(alpha = 0.45f),
          start =
            Offset(
              center.x + (innerR * cos(angleRad)).toFloat(),
              center.y + (innerR * sin(angleRad)).toFloat(),
            ),
          end =
            Offset(
              center.x + (outerR * cos(angleRad)).toFloat(),
              center.y + (outerR * sin(angleRad)).toFloat(),
            ),
          strokeWidth = if (i % 3 == 0) 3.dp.toPx() else 1.5.dp.toPx(),
          cap = StrokeCap.Round,
        )
      }

      val hourFloat = (snapshot.hour24 % 12) + snapshot.minute / 60f
      val minuteFloat = snapshot.minute + snapshot.second / 60f
      val secondFloat = snapshot.second.toFloat()

      val hourAngle = (hourFloat * 30.0 - 90.0) * PI / 180.0
      val minuteAngle = (minuteFloat * 6.0 - 90.0) * PI / 180.0
      val secondAngle = (secondFloat * 6.0 - 90.0) * PI / 180.0

      // Hour hand
      drawLine(
        color = contentColor,
        start = center,
        end =
          Offset(
            center.x + (radius * 0.50f * cos(hourAngle)).toFloat(),
            center.y + (radius * 0.50f * sin(hourAngle)).toFloat(),
          ),
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round,
      )

      // Minute hand
      drawLine(
        color = contentColor.copy(alpha = 0.9f),
        start = center,
        end =
          Offset(
            center.x + (radius * 0.72f * cos(minuteAngle)).toFloat(),
            center.y + (radius * 0.72f * sin(minuteAngle)).toFloat(),
          ),
        strokeWidth = 4.dp.toPx(),
        cap = StrokeCap.Round,
      )

      // Second hand
      drawLine(
        color = accentColor,
        start =
          Offset(
            center.x - (radius * 0.16f * cos(secondAngle)).toFloat(),
            center.y - (radius * 0.16f * sin(secondAngle)).toFloat(),
          ),
        end =
          Offset(
            center.x + (radius * 0.80f * cos(secondAngle)).toFloat(),
            center.y + (radius * 0.80f * sin(secondAngle)).toFloat(),
          ),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round,
      )

      drawCircle(color = accentColor, radius = 6.dp.toPx(), center = center)
    }
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = "${snapshot.dayOfWeek} · ${snapshot.dateFormatted}",
      style = MaterialTheme.typography.titleMedium,
      color = contentColor.copy(alpha = 0.75f),
    )
  }
}
