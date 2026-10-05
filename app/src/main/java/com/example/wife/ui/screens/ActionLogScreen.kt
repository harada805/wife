package com.example.wife.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.data.local.entity.ActionLogEntity
import com.example.wife.ui.theme.PermissionCardShape
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActionLogScreen(
    viewModel: ActionLogViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val logs by viewModel.logs.collectAsState()

    ActionLogScreenContent(
        logs = logs,
        onClearLogs = viewModel::clearLogs,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionLogScreenContent(
    logs: List<ActionLogEntity>,
    onClearLogs: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Tindakan",
                        style = TerasSenjaTypography.characterHeader,
                        color = TerasSenjaTheme.colors.ink
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TerasSenjaTheme.colors.ink
                        )
                    }
                },
                actions = {
                    if (logs.isNotEmpty()) {
                        IconButton(onClick = onClearLogs) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Hapus Riwayat",
                                tint = TerasSenjaTheme.colors.danger
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerasSenjaTheme.colors.paper
                )
            )
        },
        containerColor = TerasSenjaTheme.colors.paper
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (logs.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = "No Logs",
                        tint = TerasSenjaTheme.colors.inkMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "Belum ada riwayat tindakan yang dicatat oleh Laras.",
                        style = TerasSenjaTypography.characterMessage,
                        color = TerasSenjaTheme.colors.ink
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(logs, key = { it.id }) { log ->
                        ActionLogCard(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionLogCard(
    log: ActionLogEntity,
    modifier: Modifier = Modifier
) {
    val dateTimeFormatter = remember {
        SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault())
    }
    val formattedTime = dateTimeFormatter.format(Date(log.timestamp))

    Surface(
        shape = PermissionCardShape,
        color = TerasSenjaTheme.colors.paperRaised,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = Dp.Hairline,
                color = TerasSenjaTheme.colors.hairline,
                shape = PermissionCardShape
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.tool,
                    style = TerasSenjaTypography.permissionCardTitle,
                    color = TerasSenjaTheme.colors.ink
                )

                val (statusText, statusColor, statusIcon) = when {
                    !log.approved -> Triple("Ditolak", TerasSenjaTheme.colors.danger, Icons.Rounded.Error)
                    log.result?.startsWith("Error") == true -> Triple("Gagal", TerasSenjaTheme.colors.danger, Icons.Rounded.Error)
                    else -> Triple("Disetujui", TerasSenjaTheme.colors.terracotta, Icons.Rounded.CheckCircle)
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = PermissionCardShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = statusText,
                            tint = statusColor,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = statusText,
                            style = TerasSenjaTypography.captionStatusTimestamp,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (log.args.isNotBlank()) {
                Text(
                    text = "Argumen: ${log.args}",
                    style = TerasSenjaTypography.userMessage,
                    color = TerasSenjaTheme.colors.inkMuted
                )
            }

            if (!log.result.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hasil: ${log.result}",
                    style = TerasSenjaTypography.characterMessage,
                    color = TerasSenjaTheme.colors.ink
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedTime,
                style = TerasSenjaTypography.captionStatusTimestamp,
                color = TerasSenjaTheme.colors.inkMuted
            )
        }
    }
}

@Preview(name = "ActionLogScreen Light Mode", showBackground = true)
@Composable
fun ActionLogScreenPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        ActionLogScreenContent(
            logs = listOf(
                ActionLogEntity(id = 1, tool = "set_alarm", args = "{\"time\":\"05.30\"}", approved = true, result = "Alarm berhasil dipasang"),
                ActionLogEntity(id = 2, tool = "read_calendar", args = "{}", approved = false, result = "Ditolak oleh pengguna")
            ),
            onClearLogs = {}
        )
    }
}

@Preview(name = "ActionLogScreen Dark Mode", showBackground = true)
@Composable
fun ActionLogScreenPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        ActionLogScreenContent(
            logs = listOf(
                ActionLogEntity(id = 1, tool = "set_alarm", args = "{\"time\":\"05.30\"}", approved = true, result = "Alarm berhasil dipasang"),
                ActionLogEntity(id = 2, tool = "read_calendar", args = "{}", approved = false, result = "Ditolak oleh pengguna")
            ),
            onClearLogs = {}
        )
    }
}
