package com.example.wife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.InputBarShape
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Tulis sesuatu untuk Laras",
    enabled: Boolean = true
) {
    Surface(
        shape = InputBarShape,
        color = TerasSenjaTheme.colors.paperRaised,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(
                width = Dp.Hairline,
                color = TerasSenjaTheme.colors.hairline,
                shape = InputBarShape
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TerasSenjaTypography.userMessage,
                        color = TerasSenjaTheme.colors.inkMuted
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    textStyle = TerasSenjaTypography.userMessage.copy(
                        color = TerasSenjaTheme.colors.ink
                    ),
                    cursorBrush = SolidColor(TerasSenjaTheme.colors.terracotta),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (value.isNotBlank() && enabled) {
                            onSend()
                        }
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val canSend = value.isNotBlank() && enabled
            IconButton(
                onClick = {
                    if (canSend) {
                        onSend()
                    }
                },
                enabled = canSend,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (canSend) TerasSenjaTheme.colors.terracotta else TerasSenjaTheme.colors.hairline
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = "Send",
                    tint = TerasSenjaTheme.colors.paperRaised,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(name = "ChatInputBar Light Mode", showBackground = true)
@Composable
fun ChatInputBarPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            ChatInputBar(
                value = "",
                onValueChange = {},
                onSend = {}
            )
        }
    }
}

@Preview(name = "ChatInputBar Dark Mode", showBackground = true)
@Composable
fun ChatInputBarPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            ChatInputBar(
                value = "Halo Laras!",
                onValueChange = {},
                onSend = {}
            )
        }
    }
}
