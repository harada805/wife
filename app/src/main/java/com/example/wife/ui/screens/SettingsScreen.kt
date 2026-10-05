package com.example.wife.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.ui.theme.PermissionCardShape
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography

data class PermissionOption(
    val modeKey: String,
    val title: String,
    val description: String
)

val permissionLadder = listOf(
    PermissionOption(
        modeKey = "READ_ONLY",
        title = "baca saja",
        description = "Laras hanya membaca informasi dan menjawab, tidak mengeksekusi fungsi perangkat."
    ),
    PermissionOption(
        modeKey = "SUGGEST",
        title = "beri saran",
        description = "Laras memberikan rekomendasi tindakan, tetapi kamu yang harus menjalankannya."
    ),
    PermissionOption(
        modeKey = "STRICT",
        title = "tanya dulu",
        description = "Laras meminta izin sebelum mengeksekusi setiap tindakan perangkat."
    ),
    PermissionOption(
        modeKey = "AUTOMATIC",
        title = "otomatis untuk hal sepele",
        description = "Laras mengeksekusi aksi sepele (seperti pasang alarm) secara otomatis."
    )
)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val nickname by viewModel.nickname.collectAsState()
    val permissionMode by viewModel.permissionMode.collectAsState()

    SettingsScreenContent(
        nickname = nickname,
        permissionMode = permissionMode,
        onUpdateNickname = viewModel::updateNickname,
        onUpdatePermissionMode = viewModel::updatePermissionMode,
        onClearChatHistory = viewModel::clearChatHistory,
        onClearAllMemory = viewModel::clearAllMemory,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    nickname: String,
    permissionMode: String,
    onUpdateNickname: (String) -> Unit,
    onUpdatePermissionMode: (String) -> Unit,
    onClearChatHistory: () -> Unit,
    onClearAllMemory: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    var showClearChatDialog by remember { mutableStateOf(false) }
    var showClearMemoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengaturan",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Nickname Section
            Text(
                text = "Panggilan untukmu",
                style = TerasSenjaTypography.permissionCardTitle,
                color = TerasSenjaTheme.colors.ink
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bagaimana Laras menyapamu saat berbincang?",
                style = TerasSenjaTypography.captionStatusTimestamp,
                color = TerasSenjaTheme.colors.inkMuted
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = onUpdateNickname,
                label = { Text("Nama panggilan") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TerasSenjaTheme.colors.terracotta,
                    unfocusedBorderColor = TerasSenjaTheme.colors.hairline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Permission Ladder Section
            Text(
                text = "Tingkat Izin Otonomi Agen",
                style = TerasSenjaTypography.permissionCardTitle,
                color = TerasSenjaTheme.colors.ink
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Atur seberapa bebas Laras menjalankan aksi di perangkatmu.",
                style = TerasSenjaTypography.captionStatusTimestamp,
                color = TerasSenjaTheme.colors.inkMuted
            )
            Spacer(modifier = Modifier.height(12.dp))

            permissionLadder.forEach { option ->
                val isSelected = option.modeKey == permissionMode
                Surface(
                    shape = PermissionCardShape,
                    color = if (isSelected) TerasSenjaTheme.colors.paperRaised else TerasSenjaTheme.colors.paper,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onUpdatePermissionMode(option.modeKey) }
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = 1.5.dp,
                                    color = TerasSenjaTheme.colors.terracotta,
                                    shape = PermissionCardShape
                                )
                            } else {
                                Modifier.border(
                                    width = Dp.Hairline,
                                    color = TerasSenjaTheme.colors.hairline,
                                    shape = PermissionCardShape
                                )
                            }
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onUpdatePermissionMode(option.modeKey) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = TerasSenjaTheme.colors.terracotta,
                                unselectedColor = TerasSenjaTheme.colors.inkMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = option.title,
                                style = TerasSenjaTypography.labelMedium,
                                color = TerasSenjaTheme.colors.ink
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = option.description,
                                style = TerasSenjaTypography.captionStatusTimestamp,
                                color = TerasSenjaTheme.colors.inkMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Data Management / Danger Zone
            Text(
                text = "Manajemen Data & Privasi",
                style = TerasSenjaTypography.permissionCardTitle,
                color = TerasSenjaTheme.colors.danger
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showClearChatDialog = true },
                border = BorderStroke(1.dp, TerasSenjaTheme.colors.danger),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TerasSenjaTheme.colors.danger
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Hapus Riwayat Chat")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showClearMemoryDialog = true },
                border = BorderStroke(1.dp, TerasSenjaTheme.colors.danger),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TerasSenjaTheme.colors.danger
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Hapus Semua Memori Buku Catatan")
            }
        }
    }

    if (showClearChatDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatDialog = false },
            title = {
                Text(
                    text = "Hapus Riwayat Chat?",
                    style = TerasSenjaTypography.characterHeader,
                    color = TerasSenjaTheme.colors.ink
                )
            },
            text = {
                Text(
                    text = "Semua obrolan dengan Laras akan dihapus permanen.",
                    style = TerasSenjaTypography.userMessage,
                    color = TerasSenjaTheme.colors.inkMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearChatHistory()
                        showClearChatDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerasSenjaTheme.colors.danger,
                        contentColor = TerasSenjaTheme.colors.paperRaised
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearChatDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = TerasSenjaTheme.colors.paperRaised
        )
    }

    if (showClearMemoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearMemoryDialog = false },
            title = {
                Text(
                    text = "Hapus Semua Memori?",
                    style = TerasSenjaTypography.characterHeader,
                    color = TerasSenjaTheme.colors.ink
                )
            },
            text = {
                Text(
                    text = "Laras akan melupakan semua fakta dan catatan yang telah dipelajari tentangmu.",
                    style = TerasSenjaTypography.userMessage,
                    color = TerasSenjaTheme.colors.inkMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllMemory()
                        showClearMemoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerasSenjaTheme.colors.danger,
                        contentColor = TerasSenjaTheme.colors.paperRaised
                    )
                ) {
                    Text("Hapus Memori")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearMemoryDialog = false }) {
                    Text("Batal")
                }
            },
            containerColor = TerasSenjaTheme.colors.paperRaised
        )
    }
}

@Preview(name = "SettingsScreen Light Mode", showBackground = true)
@Composable
fun SettingsScreenPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        SettingsScreenContent(
            nickname = "Sayang",
            permissionMode = "STRICT",
            onUpdateNickname = {},
            onUpdatePermissionMode = {},
            onClearChatHistory = {},
            onClearAllMemory = {}
        )
    }
}

@Preview(name = "SettingsScreen Dark Mode", showBackground = true)
@Composable
fun SettingsScreenPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        SettingsScreenContent(
            nickname = "Sayang",
            permissionMode = "STRICT",
            onUpdateNickname = {},
            onUpdatePermissionMode = {},
            onClearChatHistory = {},
            onClearAllMemory = {}
        )
    }
}
