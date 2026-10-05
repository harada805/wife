package com.example.wife.data.repository

import com.example.wife.data.local.dao.MemoryFactDao
import com.example.wife.data.local.entity.MemoryFactEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val memoryFactDao: MemoryFactDao
) {
    fun getAllFacts(): Flow<List<MemoryFactEntity>> = memoryFactDao.getAllFacts()

    suspend fun getFactsByCategory(category: String): List<MemoryFactEntity> =
        memoryFactDao.getFactsByCategory(category)

    suspend fun addFact(category: String, content: String): Long {
        val fact = MemoryFactEntity(category = category, content = content)
        return memoryFactDao.insertFact(fact)
    }

    suspend fun updateFact(fact: MemoryFactEntity) {
        memoryFactDao.insertFact(fact)
    }

    suspend fun deleteFact(id: Long) {
        memoryFactDao.deleteFactById(id)
    }

    suspend fun clearAllFacts() {
        memoryFactDao.clearAll()
    }
}
