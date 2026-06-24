package com.teeacademy.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.teeacademy.app.core.designsystem.KeyPointBg
import com.teeacademy.app.core.designsystem.KeyPointBgDark
import com.teeacademy.app.core.designsystem.LabelAdvancedBg
import com.teeacademy.app.core.designsystem.LabelAdvancedBorder
import com.teeacademy.app.core.designsystem.LabelAdvancedFg
import com.teeacademy.app.core.designsystem.LabelMustKnowBg
import com.teeacademy.app.core.designsystem.LabelMustKnowFg
import com.teeacademy.app.core.designsystem.PitfallAccent
import com.teeacademy.app.core.designsystem.PitfallBg
import com.teeacademy.app.core.designsystem.PitfallBgDark
import com.teeacademy.app.domain.model.LessonLabel

/**
 * MK/ADV badge. Color is NEVER the only signal — text is always shown
 * alongside the fill/outline style, per accessibility spec Section 12.
 */
@Composable
fun LessonLabelBadge(label: LessonLabel, modifier: Modifier = Modifier) {
    val bg: Color
    val fg: Color
    val text: String

    when (label) {
        LessonLabel.MUST_KNOW -> {
            bg = LabelMustKnowBg
            fg = LabelMustKnowFg
            text = "MUST KNOW"
        }
        LessonLabel.ADVANCED -> {
            bg = LabelAdvancedBg
            fg = LabelAdvancedFg
            text = "ADVANCED"
        }
    }

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = fg, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun KeyPointsBox(points: List<String>, modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .background(if (dark) KeyPointBgDark else KeyPointBg, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Key Points",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        points.forEach { point ->
            Text(
                text = "• $point",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PitfallBox(text: String, modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()
    Row(
        modifier = modifier
            .background(if (dark) PitfallBgDark else PitfallBg, RoundedCornerShape(8.dp))
            .padding(12.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Pitfall",
            tint = PitfallAccent
        )
        androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Small tappable citation chip — expands to a full SourceCard on tap (see CitationCard). */
@Composable
fun CitationChip(sourceCode: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = "[$sourceCode]",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
