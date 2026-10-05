package com.example.wife.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wife.data.local.entity.MemoryFactEntity
import com.example.wife.data.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryFactsViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    val facts: StateFlow<List<MemoryFactEntity>> = memoryRepository.getAllFacts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addFact(category: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            memoryRepository.addFact(
                category = category.ifBlank { "Umum" },
                content = content.trim()
            )
        }
    }

    fun updateFact(fact: MemoryFactEntity) {
        viewModelScope.launch {
            memoryRepository.updateFact(fact)
        }
    }

    fun deleteFact(id: Long) {
        viewModelScope.launch {
            memoryRepository.deleteFact(id)
        }
    }
}
