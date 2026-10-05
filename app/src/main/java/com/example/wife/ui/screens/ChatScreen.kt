package com.example.wife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wife.agent.AgentState
import com.example.wife.data.local.entity.ChatMessageEntity
import com.example.wife.ui.components.CharacterBubble
import com.example.wife.ui.components.ChatInputBar
import com.example.wife.ui.components.Expression
import com.example.wife.ui.components.LarasAvatar
import com.example.wife.ui.components.PermissionCard
import com.example.wife.ui.components.SafetyRow
import com.example.wife.ui.components.TypingIndicator
import com.example.wife.ui.components.UserBubble
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMemory: () -> Unit = {},
    onNavigateToActionLog: () -> Unit = {}
) {
    val messages by viewModel.messages.collectAsState()
    val permissionMode by viewModel.permissionMode.collectAsState()
    val agentState by viewModel.agentState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()

    ChatScreenContent(
        messages = messages,
        permissionMode = permissionMode,
        agentState = agentState,
        inputText = inputText,
        onInputTextChange = viewModel::onInputTextChange,
        onSendMessage = viewModel::sendMessage,
        onRetryLastMessage = viewModel::retryLastMessage,
        onConfirmation = viewModel::handleConfirmation,
        onStopAgent = viewModel::stopAgent,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToMemory = onNavigateToMemory,
        onNavigateToActionLog = onNavigateToActionLog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    messages: List<ChatMessageEntity>,
    permissionMode: String,
    agentState: AgentState,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onRetryLastMessage: () -> Unit,
    onConfirmation: (Long, Boolean) -> Unit,
    onStopAgent: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMemory: () -> Unit = {},
    onNavigateToActionLog: () -> Unit = {}
) {
    var showOverflowMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val timeFormatter = remember {
        SimpleDateFormat("HH.mm", Locale.getDefault())
    }

    LaunchedEffect(messages.size, agentState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val modeLabel = when (permissionMode) {
        "STRICT" -> "Mode: bertanya dulu"
        "AUTOMATIC" -> "Mode: otomatis untuk hal sepele"
        "SUGGEST" -> "Mode: beri saran"
        "READ_ONLY" -> "Mode: baca saja"
        else -> "Mode: $permissionMode"
    }

    val isAgentThinking = agentState is AgentState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LarasAvatar(
                            size = 44.dp,
                            expression = if (isAgentThinking) Expression.THINKING else Expression.HAPPY
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Laras",
                                style = TerasSenjaTypography.characterHeader,
                                color = TerasSenjaTheme.colors.ink
                            )
                            Text(
                                text = if (isAgentThinking) "lagi mikir..." else "lagi nyeduh teh, sore di teras",
                                style = TerasSenjaTypography.captionStatusTimestamp,
                                color = TerasSenjaTheme.colors.inkMuted
                            )
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = "Menu overflow",
                                tint = TerasSenjaTheme.colors.ink
                            )
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Buku Catatan", style = TerasSenjaTypography.labelMedium) },
                                onClick = {
                                    showOverflowMenu = false
                                    onNavigateToMemory()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Riwayat Tindakan", style = TerasSenjaTypography.labelMedium) },
                                onClick = {
                                    showOverflowMenu = false
                                    onNavigateToActionLog()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pengaturan", style = TerasSenjaTypography.labelMedium) },
                                onClick = {
                                    showOverflowMenu = false
                                    onNavigateToSettings()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerasSenjaTheme.colors.paper,
                    titleContentColor = TerasSenjaTheme.colors.ink
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TerasSenjaTheme.colors.paper)
            ) {
                // Pending permission card if confirmation required
                if (agentState is AgentState.RequiresConfirmation) {
                    PermissionCard(
                        title = agentState.confirmationPrompt,
                        onApprove = { onConfirmation(agentState.actionLogId, true) },
                        onDeny = { onConfirmation(agentState.actionLogId, false) }
                    )
                }
                if (agentState is AgentState.RetryableError || agentState is AgentState.Error) {
                    val message = when (agentState) {
                        is AgentState.RetryableError -> agentState.message
                        is AgentState.Error -> agentState.error
                        else -> ""
                    }
                    RetryErrorCard(
                        message = message,
                        onRetry = onRetryLastMessage
                    )
                }

                // Safety Row with Kill Switch
                SafetyRow(
                    modeText = modeLabel,
                    onStopAgent = onStopAgent
                )

                // Input Bar
                ChatInputBar(
                    value = inputText,
                    onValueChange = onInputTextChange,
                    onSend = onSendMessage,
                    enabled = !isAgentThinking
                )
            }
        },
        containerColor = TerasSenjaTheme.colors.paper
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Centered Date Separator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hari ini",
                    style = TerasSenjaTypography.captionStatusTimestamp,
                    color = TerasSenjaTheme.colors.inkMuted
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val formattedTime = timeFormatter.format(Date(msg.timestamp))

                    if (msg.isFromUser) {
                        UserBubble(
                            message = msg.text,
                            timestamp = formattedTime
                        )
                    } else {
                        CharacterBubble(
                            message = msg.text,
                            timestamp = formattedTime
                        )
                    }
                }

                if (isAgentThinking) {
                    item {
                        TypingIndicator()
                    }
                }
            }
        }
    }
}

@Preview(name = "ChatScreen Light Mode", showBackground = true)
@Composable
fun ChatScreenPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        ChatScreenContent(
            messages = listOf(
                ChatMessageEntity(id = 1, text = "Selamat sore. Laras siap membantumu sore ini.", isFromUser = false),
                ChatMessageEntity(id = 2, text = "Tolong catatkan bahwa saya suka teh chamomile.", isFromUser = true)
            ),
            permissionMode = "STRICT",
            agentState = AgentState.Idle,
            inputText = "",
            onInputTextChange = {},
            onSendMessage = {},
            onRetryLastMessage = {},
            onConfirmation = { _, _ -> },
            onStopAgent = {}
        )
    }
}

@Preview(name = "ChatScreen Dark Mode", showBackground = true)
@Composable
fun ChatScreenPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        ChatScreenContent(
            messages = listOf(
                ChatMessageEntity(id = 1, text = "Selamat sore. Laras siap membantumu sore ini.", isFromUser = false),
                ChatMessageEntity(id = 2, text = "Tolong catatkan bahwa saya suka teh chamomile.", isFromUser = true)
            ),
            permissionMode = "STRICT",
            agentState = AgentState.Idle,
            inputText = "",
            onInputTextChange = {},
            onSendMessage = {},
            onRetryLastMessage = {},
            onConfirmation = { _, _ -> },
            onStopAgent = {}
        )
    }
}

@Composable
private fun RetryErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TerasSenjaTheme.colors.paperRaised)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = message,
            style = TerasSenjaTypography.labelMedium,
            color = TerasSenjaTheme.colors.ink
        )
        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = TerasSenjaTheme.colors.ink,
                contentColor = TerasSenjaTheme.colors.onInk
            )
        ) {
            Text(
                text = "Coba lagi",
                style = TerasSenjaTypography.labelMedium
            )
        }
    }
}
