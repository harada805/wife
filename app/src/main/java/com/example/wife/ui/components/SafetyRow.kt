package com.example.wife.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography

@Composable
fun SafetyRow(
    modifier: Modifier = Modifier,
    modeText: String = "Mode: bertanya dulu",
    onStopAgent: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Visibility,
                contentDescription = "Permission Mode",
                tint = TerasSenjaTheme.colors.inkMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = modeText,
                style = TerasSenjaTypography.captionStatusTimestamp,
                color = TerasSenjaTheme.colors.inkMuted
            )
        }

        OutlinedButton(
            onClick = onStopAgent,
            border = BorderStroke(1.dp, TerasSenjaTheme.colors.danger),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = TerasSenjaTheme.colors.danger
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 10.dp,
                vertical = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = "Stop Icon",
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Hentikan agen",
                style = TerasSenjaTypography.captionStatusTimestamp
            )
        }
    }
}

@Preview(name = "SafetyRow Light Mode", showBackground = true)
@Composable
fun SafetyRowPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            SafetyRow(onStopAgent = {})
        }
    }
}

@Preview(name = "SafetyRow Dark Mode", showBackground = true)
@Composable
fun SafetyRowPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            SafetyRow(onStopAgent = {})
        }
    }
}
