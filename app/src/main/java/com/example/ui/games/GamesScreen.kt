package com.example.ui.games

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun GamesScreen(
  uiState: GamesUiState,
  onArenaTap: () -> Unit,
) {
  val arenaColor =
    when (uiState.phase) {
      ReactionPhase.IDLE -> MaterialTheme.colorScheme.surfaceVariant
      ReactionPhase.WAITING -> Color(0xFFB45309)
      ReactionPhase.READY -> Color(0xFF059669)
      ReactionPhase.TOO_SOON -> Color(0xFFBE123C)
      ReactionPhase.RESULT -> MaterialTheme.colorScheme.primaryContainer
    }

  val arenaOnColor =
    when (uiState.phase) {
      ReactionPhase.WAITING,
      ReactionPhase.READY,
      ReactionPhase.TOO_SOON -> Color.White
      ReactionPhase.RESULT -> MaterialTheme.colorScheme.onPrimaryContainer
      ReactionPhase.IDLE -> MaterialTheme.colorScheme.onSurface
    }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text = stringResource(R.string.games_title),
              style = MaterialTheme.typography.headlineLarge,
            )
            Text(
              text = "${stringResource(R.string.reaction_game_title)} · ${stringResource(R.string.reaction_game_subtitle)}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }

      item {
        Card(
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(containerColor = arenaColor),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier =
            Modifier.fillMaxWidth()
              .height(250.dp)
              .clip(RoundedCornerShape(28.dp))
              .clickable(onClick = onArenaTap)
              .testTag("reaction_arena_card"),
        ) {
          Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector =
                  if (uiState.phase == ReactionPhase.READY) Icons.Rounded.Bolt
                  else Icons.Rounded.TouchApp,
                contentDescription = null,
                tint = arenaOnColor,
                modifier = Modifier.size(42.dp),
              )
              Spacer(modifier = Modifier.height(14.dp))
              val headline =
                when (uiState.phase) {
                  ReactionPhase.IDLE -> "Tap to Start Reaction Test"
                  ReactionPhase.WAITING -> "Wait for Green..."
                  ReactionPhase.READY -> "TAP NOW!"
                  ReactionPhase.TOO_SOON -> "Too Soon!"
                  ReactionPhase.RESULT -> "${uiState.lastReactionMs ?: 0} ms"
                }
              val subtitle =
                when (uiState.phase) {
                  ReactionPhase.IDLE -> "Measures reflex precision in milliseconds"
                  ReactionPhase.WAITING -> "Hold steady until the card turns emerald"
                  ReactionPhase.READY -> "React as fast as you can"
                  ReactionPhase.TOO_SOON -> "Tap anywhere to try again"
                  ReactionPhase.RESULT -> "Saved locally · Tap to play another round"
                }
              Text(
                text = headline,
                style =
                  MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum",
                  ),
                color = arenaOnColor,
                textAlign = TextAlign.Center,
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = arenaOnColor.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
              )
            }
          }
        }
      }

      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(text = "Personal Best", style = MaterialTheme.typography.titleMedium)
                Text(
                  text = "Stored offline in Room SQLite",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
            Text(
              text = uiState.bestReactionMs?.let { "$it ms" } ?: "—",
              style = MaterialTheme.typography.headlineMedium.copy(fontFeatureSettings = "tnum"),
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }
      }

      if (uiState.recentScores.isNotEmpty()) {
        item {
          Text(text = "Recent Reflex Attempts", style = MaterialTheme.typography.titleMedium)
        }
        itemsIndexed(uiState.recentScores, key = { _, item -> item.id }) { index, record ->
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
                text = "Attempt #${index + 1}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Text(
                text = "${record.scoreValue} ms",
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
              )
            }
          }
        }
      }

      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "Coming in Phase 5",
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.primary,
            )
            Text(
              text = "Memory Card, Quick Maths, Number Sequence, Chess Clock, and Daily Time Challenge.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }
  }
}
