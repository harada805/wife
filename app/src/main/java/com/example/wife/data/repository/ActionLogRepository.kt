package com.example.wife.data.repository

import com.example.wife.data.local.dao.ActionLogDao
import com.example.wife.data.local.entity.ActionLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionLogRepository @Inject constructor(
    private val actionLogDao: ActionLogDao
) {
    fun getAllLogs(): Flow<List<ActionLogEntity>> = actionLogDao.getAllLogs()

    suspend fun getRecentLogs(limit: Int): List<ActionLogEntity> =
        actionLogDao.getRecentLogs(limit)

    suspend fun logAction(
        tool: String,
        args: String,
        approved: Boolean = false,
        result: String? = null
    ): Long {
        val log = ActionLogEntity(
            tool = tool,
            args = args,
            approved = approved,
            result = result
        )
        return actionLogDao.insertLog(log)
    }

    suspend fun updateActionResult(id: Long, tool: String, args: String, approved: Boolean, result: String?) {
        val log = ActionLogEntity(
            id = id,
            tool = tool,
            args = args,
            approved = approved,
            result = result
        )
        actionLogDao.updateLog(log)
    }

    suspend fun clearLogs() {
        actionLogDao.clearAll()
    }
}
