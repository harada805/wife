package com.example.wife.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.CharacterBubbleShape
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography

@Composable
fun CharacterBubble(
    message: String,
    modifier: Modifier = Modifier,
    timestamp: String? = null,
    showAvatar: Boolean = true
) {
    val alphaAnim = remember { Animatable(0f) }
    val translationYAnim = remember { Animatable(16f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(Unit) {
        translationYAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = alphaAnim.value
                translationY = translationYAnim.value
            }
            .padding(vertical = 4.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (showAvatar) {
            LarasAvatar(size = 36.dp)
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(44.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.84f)
        ) {
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
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message,
                        style = TerasSenjaTypography.characterMessage,
                        color = TerasSenjaTheme.colors.ink
                    )
                }
            }

            if (!timestamp.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timestamp,
                    style = TerasSenjaTypography.captionStatusTimestamp,
                    color = TerasSenjaTheme.colors.inkMuted,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Preview(name = "CharacterBubble Light Mode", showBackground = true)
@Composable
fun CharacterBubblePreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            CharacterBubble(
                message = "Selamat sore. Hari ini cuacanya tenang sekali. Ada yang ingin kamu ceritakan pada Laras?",
                timestamp = "16.45"
            )
        }
    }
}

@Preview(name = "CharacterBubble Dark Mode", showBackground = true)
@Composable
fun CharacterBubblePreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            CharacterBubble(
                message = "Selamat sore. Hari ini cuacanya tenang sekali. Ada yang ingin kamu ceritakan pada Laras?",
                timestamp = "16.45"
            )
        }
    }
}
