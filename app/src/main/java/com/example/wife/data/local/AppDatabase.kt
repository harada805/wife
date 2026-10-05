package com.example.wife.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.wife.data.local.dao.ActionLogDao
import com.example.wife.data.local.dao.ChatDao
import com.example.wife.data.local.dao.MemoryFactDao
import com.example.wife.data.local.entity.ActionLogEntity
import com.example.wife.data.local.entity.ChatMessageEntity
import com.example.wife.data.local.entity.MemoryFactEntity

@Database(
    entities = [
        ChatMessageEntity::class,
        MemoryFactEntity::class,
        ActionLogEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun memoryFactDao(): MemoryFactDao
    abstract fun actionLogDao(): ActionLogDao
}
