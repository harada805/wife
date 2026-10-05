package com.example.wife.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wife.agent.AgentLoop
import com.example.wife.agent.AgentState
import com.example.wife.data.local.entity.ChatMessageEntity
import com.example.wife.data.repository.ActionLogRepository
import com.example.wife.data.repository.ChatRepository
import com.example.wife.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val agentLoop: AgentLoop,
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository,
    private val actionLogRepository: ActionLogRepository
) : ViewModel() {

    val messages: StateFlow<List<ChatMessageEntity>> = chatRepository.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val permissionMode: StateFlow<String> = settingsRepository.permissionMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "STRICT"
        )

    private val _agentState = MutableStateFlow<AgentState>(AgentState.Idle)
    val agentState: StateFlow<AgentState> = _agentState.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private var activeJob: Job? = null
    private var lastUserMessage: String? = null

    fun onInputTextChange(newText: String) {
        _inputText.value = newText
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return

        lastUserMessage = text
        _inputText.value = ""
        _agentState.value = AgentState.Loading

        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            runCatching {
                val resultState = agentLoop.processMessage(text)
                _agentState.value = resultState
            }.onFailure {
                val errorMsg = "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
                chatRepository.sendMessage(errorMsg, isFromUser = false)
                _agentState.value = AgentState.RetryableError(errorMsg)
            }
        }
    }

    fun retryLastMessage() {
        val text = lastUserMessage?.trim().orEmpty()
        if (text.isEmpty()) return

        _agentState.value = AgentState.Loading
        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            runCatching {
                val resultState = agentLoop.processMessage(text)
                _agentState.value = resultState
            }.onFailure {
                val errorMsg = "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
                chatRepository.sendMessage(errorMsg, isFromUser = false)
                _agentState.value = AgentState.RetryableError(errorMsg)
            }
        }
    }

    fun handleConfirmation(actionLogId: Long, approved: Boolean) {
        _agentState.value = AgentState.Loading
        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            runCatching {
                val resultState = agentLoop.handleConfirmation(actionLogId, approved)
                _agentState.value = resultState
            }.onFailure {
                val errorMsg = "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
                chatRepository.sendMessage(errorMsg, isFromUser = false)
                _agentState.value = AgentState.RetryableError(errorMsg)
            }
        }
    }

    fun stopAgent() {
        activeJob?.cancel()
        _agentState.value = AgentState.Idle
        viewModelScope.launch {
            actionLogRepository.logAction(
                tool = "KillSwitch",
                args = "{}",
                approved = true,
                result = "Agen dihentikan secara paksa oleh pengguna"
            )
        }
    }
}
