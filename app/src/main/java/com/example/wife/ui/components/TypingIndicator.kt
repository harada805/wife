package com.example.wife.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.CharacterBubbleShape
import com.example.wife.ui.theme.TerasSenjaTheme

@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LarasAvatar(size = 36.dp, expression = Expression.THINKING)
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = CharacterBubbleShape,
            color = TerasSenjaTheme.colors.paperRaised,
            modifier = Modifier
                .border(
                    width = Dp.Hairline,
                    color = TerasSenjaTheme.colors.hairline,
                    shape = CharacterBubbleShape
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulsatingDot(delayMillis = 0)
                PulsatingDot(delayMillis = 150)
                PulsatingDot(delayMillis = 300)
            }
        }
    }
}

@Composable
private fun PulsatingDot(delayMillis: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "DotTransition_$delayMillis")

    val scaleProgress by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, delayMillis = delayMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotScale_$delayMillis"
    )

    val alphaProgress by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, delayMillis = delayMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotAlpha_$delayMillis"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .scale(scaleProgress)
            .graphicsLayer { alpha = alphaProgress }
            .background(color = TerasSenjaTheme.colors.ink, shape = CircleShape)
    )
}

@Preview(name = "TypingIndicator Light Mode", showBackground = true)
@Composable
fun TypingIndicatorPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            TypingIndicator()
        }
    }
}

@Preview(name = "TypingIndicator Dark Mode", showBackground = true)
@Composable
fun TypingIndicatorPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            TypingIndicator()
        }
    }
}
