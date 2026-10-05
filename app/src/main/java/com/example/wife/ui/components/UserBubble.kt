package com.example.wife.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography
import com.example.wife.ui.theme.UserBubbleShape

@Composable
fun UserBubble(
    message: String,
    modifier: Modifier = Modifier,
    timestamp: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth(0.70f)
        ) {
            Surface(
                shape = UserBubbleShape,
                color = TerasSenjaTheme.colors.ink
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message,
                        style = TerasSenjaTypography.userMessage,
                        color = TerasSenjaTheme.colors.onInk
                    )
                }
            }

            if (!timestamp.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timestamp,
                    style = TerasSenjaTypography.captionStatusTimestamp,
                    color = TerasSenjaTheme.colors.inkMuted,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}

@Preview(name = "UserBubble Light Mode", showBackground = true)
@Composable
fun UserBubblePreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            UserBubble(
                message = "Tolong pasangkan alarm untuk besok jam 05.30 ya",
                timestamp = "16.46"
            )
        }
    }
}

@Preview(name = "UserBubble Dark Mode", showBackground = true)
@Composable
fun UserBubblePreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            UserBubble(
                message = "Tolong pasangkan alarm untuk besok jam 05.30 ya",
                timestamp = "16.46"
            )
        }
    }
}
