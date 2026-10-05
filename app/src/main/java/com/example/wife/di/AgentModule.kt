package com.example.wife.di

import com.example.wife.agent.tool.AgentTool
import com.example.wife.agent.tool.ReadCalendarTodayTool
import com.example.wife.agent.tool.SetAlarmTool
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AgentModule {

    @Binds
    @IntoSet
    abstract fun bindSetAlarmTool(tool: SetAlarmTool): AgentTool

    @Binds
    @IntoSet
    abstract fun bindReadCalendarTodayTool(tool: ReadCalendarTodayTool): AgentTool
}
