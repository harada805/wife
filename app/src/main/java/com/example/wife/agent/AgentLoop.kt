package com.example.wife.agent

import android.util.Log
import com.example.wife.agent.tool.AgentTool
import com.example.wife.agent.tool.RiskLevel
import com.example.wife.agent.tool.ToolResult
import com.example.wife.data.local.dao.ActionLogDao
import com.example.wife.data.local.entity.ActionLogEntity
import com.example.wife.data.repository.ChatRepository
import com.example.wife.data.repository.MemoryRepository
import com.example.wife.data.repository.SettingsRepository
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

sealed class AgentState {
    object Idle : AgentState()
    object Loading : AgentState()
    data class Success(val message: String) : AgentState()
    data class Error(val error: String) : AgentState()
    data class RetryableError(val message: String) : AgentState()
    data class RequiresConfirmation(
        val actionLogId: Long,
        val toolName: String,
        val args: Map<String, Any?>,
        val confirmationPrompt: String
    ) : AgentState()
}

@Singleton
class AgentLoop @Inject constructor(
    private val geminiRepository: GeminiRepository,
    private val chatRepository: ChatRepository,
    private val memoryRepository: MemoryRepository,
    private val settingsRepository: SettingsRepository,
    private val actionLogDao: ActionLogDao,
    private val memoryFactExtractor: MemoryFactExtractor,
    private val tools: Set<@JvmSuppressWildcards AgentTool>
) {
    private val toolMap: Map<String, AgentTool> by lazy {
        tools.associateBy { it.name }
    }

    private var activeChatSession: GeminiChatSession? = null
    private var pendingToolCall: PendingConfirmation? = null

    data class PendingConfirmation(
        val actionLogId: Long,
        val toolName: String,
        val args: Map<String, Any?>
    )

    suspend fun processMessage(userMessageText: String): AgentState {
        if (!geminiRepository.isConfigured()) {
            val errorMsg = "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
            chatRepository.sendMessage(userMessageText, isFromUser = true)
            chatRepository.sendMessage(errorMsg, isFromUser = false)
            return AgentState.RetryableError(errorMsg)
        }

        chatRepository.sendMessage(userMessageText, isFromUser = true)

        val nickname = settingsRepository.nickname.first()
        val facts = memoryRepository.getAllFacts().first()
        val toolList = toolMap.values.toList()

        val chat = geminiRepository.startChat(
            nickname = nickname,
            memoryFacts = facts,
            tools = toolList
        )
        if (chat == null) {
            val errorMsg = "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
            chatRepository.sendMessage(errorMsg, isFromUser = false)
            return AgentState.RetryableError(errorMsg)
        }
        activeChatSession = chat

        return runCatching {
            runAgentLoop(chat, userMessageText)
        }.getOrElse { throwable ->
            Log.e("AgentLoop", "Error in agent loop", throwable)
            val errorMsg = "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
            chatRepository.sendMessage(errorMsg, isFromUser = false)
            AgentState.RetryableError(errorMsg)
        }
    }

    private suspend fun runAgentLoop(chat: GeminiChatSession, initialPrompt: String): AgentState {
        var currentPromptContent: Content = content("user") { text(initialPrompt) }
        var iterations = 0
        val maxIterations = 5

        while (iterations < maxIterations) {
            iterations++

            val response = geminiRepository.sendMessage(chat, currentPromptContent)
                .toAgentResponseOrReturn { return it }

            val functionCalls = response.functionCalls
            if (functionCalls.isEmpty()) {
                val textResponse = response.text ?: "Laras tidak dapat memberikan respon."
                chatRepository.sendMessage(textResponse, isFromUser = false)

                val recentMessages = chatRepository.getRecentMessages(10)
                memoryFactExtractor.extractAndSaveFacts(recentMessages)

                return AgentState.Success(textResponse)
            } else {
                val functionCall = functionCalls.first()
                val toolName = functionCall.name
                val argsMap: Map<String, Any?> = functionCall.args
                val tool = toolMap[toolName]

                val jsonArgs = JSONObject(argsMap).toString()

                if (tool == null) {
                    val errorResult = "Tool '$toolName' tidak ditemukan."
                    actionLogDao.insertLog(
                        ActionLogEntity(
                            tool = toolName,
                            args = jsonArgs,
                            approved = false,
                            result = errorResult
                        )
                    )
                    currentPromptContent = buildFunctionResponseContent(
                        toolName,
                        JSONObject(mapOf("error" to errorResult))
                    )
                    continue
                }

                when (tool.riskLevel) {
                    RiskLevel.FORBIDDEN -> {
                        val forbiddenResult = "Tindakan '$toolName' dilarang oleh sistem."
                        actionLogDao.insertLog(
                            ActionLogEntity(
                                tool = toolName,
                                args = jsonArgs,
                                approved = false,
                                result = forbiddenResult
                            )
                        )
                        currentPromptContent = buildFunctionResponseContent(
                            toolName,
                            JSONObject(mapOf("error" to forbiddenResult))
                        )
                    }

                    RiskLevel.NEEDS_CONFIRMATION -> {
                        val logId = actionLogDao.insertLog(
                            ActionLogEntity(
                                tool = toolName,
                                args = jsonArgs,
                                approved = false,
                                result = "Menunggu konfirmasi pengguna"
                            )
                        )
                        pendingToolCall = PendingConfirmation(
                            actionLogId = logId,
                            toolName = toolName,
                            args = argsMap
                        )
                        val promptDesc = "Laras memerlukan konfirmasi kamu untuk mejalankan '${tool.description}'"
                        return AgentState.RequiresConfirmation(
                            actionLogId = logId,
                            toolName = toolName,
                            args = argsMap,
                            confirmationPrompt = promptDesc
                        )
                    }

                    RiskLevel.READ_ONLY -> {
                        val toolResult = tool.execute(argsMap)
                        val (resultText, isSuccess) = when (toolResult) {
                            is ToolResult.Success -> Pair(toolResult.message, true)
                            is ToolResult.Failure -> Pair(toolResult.error, false)
                            is ToolResult.ConfirmationRequired -> Pair("Konfirmasi diperlukan", false)
                        }

                        actionLogDao.insertLog(
                            ActionLogEntity(
                                tool = toolName,
                                args = jsonArgs,
                                approved = true,
                                result = resultText
                            )
                        )

                        val responseJsonKey = if (isSuccess) "result" else "error"
                        currentPromptContent = buildFunctionResponseContent(
                            toolName,
                            JSONObject(mapOf(responseJsonKey to resultText))
                        )
                    }
                }
            }
        }

        val limitMsg = "Batas maksimum langkah interaksi agent telah tercapai."
        chatRepository.sendMessage(limitMsg, isFromUser = false)
        return AgentState.Success(limitMsg)
    }

    suspend fun handleConfirmation(actionLogId: Long, isApproved: Boolean): AgentState {
        val pending = pendingToolCall
        val chat = activeChatSession

        if (pending == null || pending.actionLogId != actionLogId || chat == null) {
            return AgentState.Error("Tidak ada konfirmasi aktif yang cocok.")
        }

        val toolName = pending.toolName
        val argsMap = pending.args
        val jsonArgs = JSONObject(argsMap).toString()
        val tool = toolMap[toolName]

        pendingToolCall = null

        val responseContent: Content
        if (isApproved) {
            val toolResult = tool?.execute(argsMap) ?: ToolResult.Failure("Tool tidak ditemukan")
            val (resultText, isSuccess) = when (toolResult) {
                is ToolResult.Success -> Pair(toolResult.message, true)
                is ToolResult.Failure -> Pair(toolResult.error, false)
                is ToolResult.ConfirmationRequired -> Pair("Konfirmasi diperlukan", false)
            }

            actionLogDao.updateLog(
                ActionLogEntity(
                    id = actionLogId,
                    tool = toolName,
                    args = jsonArgs,
                    approved = true,
                    result = resultText
                )
            )

            val responseJsonKey = if (isSuccess) "result" else "error"
            responseContent = buildFunctionResponseContent(
                toolName,
                JSONObject(mapOf(responseJsonKey to resultText))
            )
        } else {
            val rejectedMsg = "Pengguna menolak konfirmasi tindakan ini."
            actionLogDao.updateLog(
                ActionLogEntity(
                    id = actionLogId,
                    tool = toolName,
                    args = jsonArgs,
                    approved = false,
                    result = "Ditolak pengguna"
                )
            )
            responseContent = buildFunctionResponseContent(
                toolName,
                JSONObject(mapOf("error" to rejectedMsg))
            )
        }

        return resumeAgentLoop(chat, responseContent)
    }

    private suspend fun resumeAgentLoop(chat: GeminiChatSession, functionResponseContent: Content): AgentState {
        var currentPromptContent = functionResponseContent
        var iterations = 0
        val maxIterations = 5

        while (iterations < maxIterations) {
            iterations++

            val response = geminiRepository.sendMessage(chat, currentPromptContent)
                .toAgentResponseOrReturn { return it }

            val functionCalls = response.functionCalls
            if (functionCalls.isEmpty()) {
                val textResponse = response.text ?: "Laras tidak dapat memberikan respon."
                chatRepository.sendMessage(textResponse, isFromUser = false)

                val recentMessages = chatRepository.getRecentMessages(10)
                memoryFactExtractor.extractAndSaveFacts(recentMessages)

                return AgentState.Success(textResponse)
            } else {
                val functionCall = functionCalls.first()
                val toolName = functionCall.name
                val argsMap: Map<String, Any?> = functionCall.args
                val tool = toolMap[toolName]
                val jsonArgs = JSONObject(argsMap).toString()

                if (tool == null) {
                    val errorResult = "Tool '$toolName' tidak ditemukan."
                    actionLogDao.insertLog(
                        ActionLogEntity(
                            tool = toolName,
                            args = jsonArgs,
                            approved = false,
                            result = errorResult
                        )
                    )
                    currentPromptContent = buildFunctionResponseContent(
                        toolName,
                        JSONObject(mapOf("error" to errorResult))
                    )
                    continue
                }

                when (tool.riskLevel) {
                    RiskLevel.FORBIDDEN -> {
                        val forbiddenResult = "Tindakan '$toolName' dilarang oleh sistem."
                        actionLogDao.insertLog(
                            ActionLogEntity(
                                tool = toolName,
                                args = jsonArgs,
                                approved = false,
                                result = forbiddenResult
                            )
                        )
                        currentPromptContent = buildFunctionResponseContent(
                            toolName,
                            JSONObject(mapOf("error" to forbiddenResult))
                        )
                    }

                    RiskLevel.NEEDS_CONFIRMATION -> {
                        val logId = actionLogDao.insertLog(
                            ActionLogEntity(
                                tool = toolName,
                                args = jsonArgs,
                                approved = false,
                                result = "Menunggu konfirmasi pengguna"
                            )
                        )
                        pendingToolCall = PendingConfirmation(
                            actionLogId = logId,
                            toolName = toolName,
                            args = argsMap
                        )
                        val promptDesc = "Laras memerlukan konfirmasi kamu untuk mejalankan '${tool.description}'"
                        return AgentState.RequiresConfirmation(
                            actionLogId = logId,
                            toolName = toolName,
                            args = argsMap,
                            confirmationPrompt = promptDesc
                        )
                    }

                    RiskLevel.READ_ONLY -> {
                        val toolResult = tool.execute(argsMap)
                        val (resultText, isSuccess) = when (toolResult) {
                            is ToolResult.Success -> Pair(toolResult.message, true)
                            is ToolResult.Failure -> Pair(toolResult.error, false)
                            is ToolResult.ConfirmationRequired -> Pair("Konfirmasi diperlukan", false)
                        }

                        actionLogDao.insertLog(
                            ActionLogEntity(
                                tool = toolName,
                                args = jsonArgs,
                                approved = true,
                                result = resultText
                            )
                        )

                        val responseJsonKey = if (isSuccess) "result" else "error"
                        currentPromptContent = buildFunctionResponseContent(
                            toolName,
                            JSONObject(mapOf(responseJsonKey to resultText))
                        )
                    }
                }
            }
        }

        val limitMsg = "Batas maksimum langkah interaksi agent telah tercapai."
        chatRepository.sendMessage(limitMsg, isFromUser = false)
        return AgentState.Success(limitMsg)
    }

    private fun buildFunctionResponseContent(name: String, responseJson: JSONObject): Content {
        val firebaseJson = buildJsonObject {
            responseJson.keys().forEach { key ->
                put(key, JsonPrimitive(responseJson.optString(key)))
            }
        }
        return Content(
            role = "function",
            parts = listOf(FunctionResponsePart(name, firebaseJson))
        )
    }

    private suspend inline fun GeminiCallResult.toAgentResponseOrReturn(
        onFailure: (AgentState) -> Nothing
    ): GenerateContentResponse {
        return when (this) {
            is GeminiCallResult.Success -> response
            is GeminiCallResult.Failure -> {
                val message = error.toFriendlyMessage()
                chatRepository.sendMessage(message, isFromUser = false)
                onFailure(AgentState.RetryableError(message))
            }
        }
    }

    private fun GeminiFailure.toFriendlyMessage(): String {
        return when (this) {
            GeminiFailure.Unconfigured -> "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
            GeminiFailure.Overloaded -> "Aduh, Laras lagi ketahan rame banget. Coba lagi sebentar ya."
            GeminiFailure.RateLimited -> "Kayaknya Laras perlu tarik napas dulu. Coba lagi sebentar ya."
            GeminiFailure.Network -> "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
            GeminiFailure.InvalidRequest -> "Laras belum nangkep maksudnya dengan rapi. Coba tulis sedikit beda ya."
            GeminiFailure.NotFound -> "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
            GeminiFailure.Unknown -> "Duh, API key-nya belum terpasang atau sambunganku lagi terputus nih. Coba dicek pengaturannya ya."
        }
    }
}
