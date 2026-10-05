package com.example.wife.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wife.data.local.entity.ActionLogEntity
import com.example.wife.data.repository.ActionLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionLogViewModel @Inject constructor(
    private val actionLogRepository: ActionLogRepository
) : ViewModel() {

    val logs: StateFlow<List<ActionLogEntity>> = actionLogRepository.getAllLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clearLogs() {
        viewModelScope.launch {
            actionLogRepository.clearLogs()
        }
    }
}
