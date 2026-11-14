package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.ThemeMode
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
  preferences: UserPreferences,
  onSelectTheme: (ThemeMode) -> Unit,
  onToggle24Hour: (Boolean) -> Unit,
  onComplete: () -> Unit,
) {
  val pagerState = rememberPagerState(pageCount = { 3 })
  val scope = rememberCoroutineScope()

  Box(
    modifier =
      Modifier.fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors =
              listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.background,
              )
          )
        )
        .windowInsetsPadding(WindowInsets.safeDrawing),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier.fillMaxSize().widthIn(max = 560.dp).padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.AccessTimeFilled,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
        }
        AnimatedVisibility(visible = pagerState.currentPage < 2) {
          TextButton(
            onClick = onComplete,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("onboarding_skip_btn"),
          ) {
            Text(text = stringResource(R.string.onboarding_skip))
          }
        }
      }

      HorizontalPager(
        state = pagerState,
        modifier = Modifier.weight(1f).fillMaxWidth(),
      ) { page ->
        when (page) {
          0 -> OnboardingWelcomePage()
          1 ->
            OnboardingTailorPage(
              preferences = preferences,
              onSelectTheme = onSelectTheme,
              onToggle24Hour = onToggle24Hour,
            )
          else -> OnboardingReadyPage()
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          repeat(3) { idx ->
            val selected = pagerState.currentPage == idx
            Box(
              modifier =
                Modifier.height(8.dp)
                  .width(if (selected) 24.dp else 8.dp)
                  .clip(CircleShape)
                  .background(
                    if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                  )
            )
          }
        }

        Button(
          onClick = {
            if (pagerState.currentPage < 2) {
              scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            } else {
              onComplete()
            }
          },
          shape = RoundedCornerShape(18.dp),
          contentPadding = ButtonDefaults.ContentPadding,
          modifier =
            Modifier.height(52.dp)
              .minimumInteractiveComponentSize()
              .testTag(
                if (pagerState.currentPage < 2) "onboarding_next_btn" else "onboarding_start_btn"
              ),
        ) {
          Text(
            text =
              if (pagerState.currentPage < 2) stringResource(R.string.onboarding_next)
              else stringResource(R.string.onboarding_start),
            style = MaterialTheme.typography.labelLarge,
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector =
              if (pagerState.currentPage < 2) Icons.AutoMirrored.Rounded.ArrowForward
              else Icons.Rounded.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun OnboardingWelcomePage() {
  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      modifier = Modifier.fillMaxWidth().height(230.dp),
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_onboarding_nest),
        contentDescription = "TimeNest horizon illustration",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
    }
    Spacer(modifier = Modifier.height(28.dp))
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
    ) {
      Text(
        text = stringResource(R.string.app_tagline),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = stringResource(R.string.onboarding_title_1),
      style = MaterialTheme.typography.headlineLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = stringResource(R.string.onboarding_desc_1),
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun OnboardingTailorPage(
  preferences: UserPreferences,
  onSelectTheme: (ThemeMode) -> Unit,
  onToggle24Hour: (Boolean) -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(R.string.onboarding_title_2),
      style = MaterialTheme.typography.headlineLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = stringResource(R.string.onboarding_desc_2),
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(24.dp))

    val modes =
      listOf(
        Triple(ThemeMode.DARK, "Midnight Dark", Icons.Rounded.DarkMode),
        Triple(ThemeMode.AMOLED, "AMOLED Black", Icons.Rounded.AutoAwesome),
        Triple(ThemeMode.LIGHT, "Daylight Cream", Icons.Rounded.LightMode),
      )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      modes.forEach { (mode, label, icon) ->
        val selected = preferences.themeMode == mode
        OutlinedCard(
          shape = RoundedCornerShape(18.dp),
          border =
            BorderStroke(
              width = if (selected) 2.dp else 1.dp,
              color =
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
            ),
          colors =
            CardDefaults.outlinedCardColors(
              containerColor =
                if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                else MaterialTheme.colorScheme.surface
            ),
          modifier =
            Modifier.fillMaxWidth()
              .minimumInteractiveComponentSize()
              .clip(RoundedCornerShape(18.dp))
              .clickable { onSelectTheme(mode) }
              .testTag("onboarding_theme_${mode.name.lowercase()}"),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint =
                  if (selected) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
            if (selected) {
              Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
    Card(
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "24-Hour Clock Format",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = if (preferences.use24HourFormat) "14:30 format active" else "2:30 PM format active",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Switch(
          checked = preferences.use24HourFormat,
          onCheckedChange = onToggle24Hour,
          modifier = Modifier.testTag("onboarding_24h_switch"),
        )
      }
    }
  }
}

@Composable
private fun OnboardingReadyPage() {
  Column(
    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(R.string.onboarding_title_3),
      style = MaterialTheme.typography.headlineLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = stringResource(R.string.onboarding_desc_3),
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(24.dp))

    val highlights =
      listOf(
        Triple(
          Icons.Rounded.Schedule,
          "Clock Studio & Nightstand",
          "Digital, Flip, Minimal & Analog faces with custom Google Fonts",
        ),
        Triple(
          Icons.Rounded.Timer,
          "Quick Timer, Stopwatch & Focus",
          "Start counting down or track deep work offline in one tap",
        ),
        Triple(
          Icons.Rounded.AutoAwesome,
          "Customizable Dashboard",
          "Reorder or hide Home cards—all preferences saved privately on device",
        ),
      )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
      highlights.forEach { (icon, title, desc) ->
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.size(44.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                )
              }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }
    }
  }
}
