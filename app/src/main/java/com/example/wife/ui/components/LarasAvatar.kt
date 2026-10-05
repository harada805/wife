package com.example.wife.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.TerasSenjaTheme

enum class Expression {
    HAPPY, THINKING, BLINK
}

@Composable
fun LarasAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    expression: Expression = Expression.HAPPY
) {
    val inkColor = TerasSenjaTheme.colors.ink
    val paperRaisedColor = TerasSenjaTheme.colors.paperRaised
    val terracottaColor = TerasSenjaTheme.colors.terracotta

    val infiniteTransition = rememberInfiniteTransition(label = "BlinkAnimation")
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 200, delayMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BlinkFloat"
    )

    Canvas(modifier = modifier.size(size)) {
        val radius = size.toPx() / 2f
        val center = Offset(radius, radius)

        // Background Circle (Warm beige / paperRaised)
        drawCircle(
            color = paperRaisedColor,
            radius = radius - 2f,
            center = center
        )

        // Outline (Ink color)
        drawCircle(
            color = inkColor,
            radius = radius - 2f,
            center = center,
            style = Stroke(width = 2.5f)
        )

        // Hair / Bangs top arc
        val hairPath = Path().apply {
            moveTo(center.x - radius * 0.7f, center.y - radius * 0.2f)
            cubicTo(
                center.x - radius * 0.4f, center.y - radius * 0.9f,
                center.x + radius * 0.4f, center.y - radius * 0.9f,
                center.x + radius * 0.7f, center.y - radius * 0.2f
            )
            cubicTo(
                center.x + radius * 0.3f, center.y - radius * 0.5f,
                center.x - radius * 0.3f, center.y - radius * 0.5f,
                center.x - radius * 0.7f, center.y - radius * 0.2f
            )
            close()
        }
        drawPath(path = hairPath, color = inkColor)

        // Soft Terracotta Blush / Cheeks
        val cheekRadius = radius * 0.18f
        drawCircle(
            color = terracottaColor.copy(alpha = 0.35f),
            radius = cheekRadius,
            center = Offset(center.x - radius * 0.4f, center.y + radius * 0.12f)
        )
        drawCircle(
            color = terracottaColor.copy(alpha = 0.35f),
            radius = cheekRadius,
            center = Offset(center.x + radius * 0.4f, center.y + radius * 0.12f)
        )

        // Eyes calculation
        val currentBlink = if (expression == Expression.BLINK) 0.1f else blinkProgress
        val eyeHeight = (radius * 0.14f) * currentBlink
        val eyeWidth = radius * 0.12f

        // Left Eye
        drawOval(
            color = inkColor,
            topLeft = Offset(center.x - radius * 0.3f - eyeWidth / 2, center.y - radius * 0.1f - eyeHeight / 2),
            size = Size(eyeWidth, eyeHeight)
        )

        // Right Eye
        drawOval(
            color = inkColor,
            topLeft = Offset(center.x + radius * 0.3f - eyeWidth / 2, center.y - radius * 0.1f - eyeHeight / 2),
            size = Size(eyeWidth, eyeHeight)
        )

        // Mouth (Smile)
        val mouthPath = Path().apply {
            moveTo(center.x - radius * 0.22f, center.y + radius * 0.25f)
            cubicTo(
                center.x - radius * 0.12f, center.y + radius * 0.42f,
                center.x + radius * 0.12f, center.y + radius * 0.42f,
                center.x + radius * 0.22f, center.y + radius * 0.25f
            )
        }
        drawPath(
            path = mouthPath,
            color = inkColor,
            style = Stroke(width = 2.2f)
        )
    }
}

@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    expression: Expression = Expression.HAPPY
) {
    LarasAvatar(modifier = modifier, size = size, expression = expression)
}

@Preview(name = "LarasAvatar Light Mode", showBackground = true)
@Composable
fun LarasAvatarPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            LarasAvatar()
        }
    }
}

@Preview(name = "LarasAvatar Dark Mode", showBackground = true)
@Composable
fun LarasAvatarPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            LarasAvatar()
        }
    }
}
