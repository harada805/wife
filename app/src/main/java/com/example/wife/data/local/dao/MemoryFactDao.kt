package com.example.wife.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wife.data.local.entity.MemoryFactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryFactDao {
    @Query("SELECT * FROM memory_facts ORDER BY createdAt DESC")
    fun getAllFacts(): Flow<List<MemoryFactEntity>>

    @Query("SELECT * FROM memory_facts WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getFactsByCategory(category: String): List<MemoryFactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFact(fact: MemoryFactEntity): Long

    @Delete
    suspend fun deleteFact(fact: MemoryFactEntity)

    @Query("DELETE FROM memory_facts WHERE id = :id")
    suspend fun deleteFactById(id: Long)

    @Query("DELETE FROM memory_facts")
    suspend fun clearAll()
}
