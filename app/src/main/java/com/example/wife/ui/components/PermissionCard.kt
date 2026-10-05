package com.example.wife.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.PermissionCardShape
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography

@Composable
fun PermissionCard(
    title: String,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
    modifier: Modifier = Modifier,
    approveLabel: String = "Pasang",
    denyLabel: String = "Nanti dulu"
) {
    Surface(
        shape = PermissionCardShape,
        color = TerasSenjaTheme.colors.paperRaised,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(
                width = Dp.Hairline,
                color = TerasSenjaTheme.colors.hairline,
                shape = PermissionCardShape
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // 3dp terracotta left accent stripe
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(color = TerasSenjaTheme.colors.terracotta)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Butuh izinmu",
                    style = TerasSenjaTypography.captionStatusTimestamp,
                    color = TerasSenjaTheme.colors.terracottaDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    style = TerasSenjaTypography.permissionCardTitle,
                    color = TerasSenjaTheme.colors.ink
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TerasSenjaTheme.colors.ink,
                            contentColor = TerasSenjaTheme.colors.onInk
                        )
                    ) {
                        Text(
                            text = approveLabel,
                            style = TerasSenjaTypography.labelMedium
                        )
                    }

                    OutlinedButton(
                        onClick = onDeny,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = TerasSenjaTheme.colors.ink
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TerasSenjaTheme.colors.ink
                        )
                    ) {
                        Text(
                            text = denyLabel,
                            style = TerasSenjaTypography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "PermissionCard Light Mode", showBackground = true)
@Composable
fun PermissionCardPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            PermissionCard(
                title = "Pasang alarm Senin 05.30, label \"Kelas pagi\"?",
                onApprove = {},
                onDeny = {}
            )
        }
    }
}

@Preview(name = "PermissionCard Dark Mode", showBackground = true)
@Composable
fun PermissionCardPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        Surface(color = TerasSenjaTheme.colors.paper) {
            PermissionCard(
                title = "Pasang alarm Senin 05.30, label \"Kelas pagi\"?",
                onApprove = {},
                onDeny = {}
            )
        }
    }
}
