package com.example.wife.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wife.data.repository.ChatRepository
import com.example.wife.data.repository.MemoryRepository
import com.example.wife.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val chatRepository: ChatRepository,
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    val nickname: StateFlow<String> = settingsRepository.nickname
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Sayang"
        )

    val permissionMode: StateFlow<String> = settingsRepository.permissionMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "STRICT"
        )

    fun updateNickname(newName: String) {
        viewModelScope.launch {
            settingsRepository.setNickname(newName)
        }
    }

    fun updatePermissionMode(newMode: String) {
        viewModelScope.launch {
            settingsRepository.setPermissionMode(newMode)
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatRepository.clearHistory()
        }
    }

    fun clearAllMemory() {
        viewModelScope.launch {
            memoryRepository.clearAllFacts()
        }
    }
}
