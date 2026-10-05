package com.example.wife.di

import android.content.Context
import androidx.room.Room
import com.example.wife.data.local.AppDatabase
import com.example.wife.data.local.dao.ActionLogDao
import com.example.wife.data.local.dao.ChatDao
import com.example.wife.data.local.dao.MemoryFactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "wife_app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideChatDao(database: AppDatabase): ChatDao = database.chatDao()

    @Provides
    fun provideMemoryFactDao(database: AppDatabase): MemoryFactDao = database.memoryFactDao()

    @Provides
    fun provideActionLogDao(database: AppDatabase): ActionLogDao = database.actionLogDao()
}
