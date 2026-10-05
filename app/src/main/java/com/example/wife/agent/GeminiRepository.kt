package com.example.wife.agent

import android.util.Log
import com.example.wife.agent.GeminiService.Companion.PRIMARY_MODEL
import com.example.wife.agent.GeminiService.Companion.SECONDARY_MODEL
import com.example.wife.agent.tool.AgentTool
import com.example.wife.data.local.entity.MemoryFactEntity
import com.google.firebase.ai.Chat
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.math.pow
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

sealed class GeminiFailure {
    object Overloaded : GeminiFailure()
    object RateLimited : GeminiFailure()
    object Network : GeminiFailure()
    object InvalidRequest : GeminiFailure()
    object NotFound : GeminiFailure()
    object Unconfigured : GeminiFailure()
    object Unknown : GeminiFailure()
}

sealed class GeminiCallResult {
    data class Success(val response: GenerateContentResponse) : GeminiCallResult()
    data class Failure(val error: GeminiFailure) : GeminiCallResult()
}

data class GeminiChatSession(
    val nickname: String,
    val memoryFacts: List<MemoryFactEntity>,
    val tools: List<AgentTool>,
    val modelName: String = PRIMARY_MODEL,
    val chat: Chat
)

@Singleton
class GeminiRepository @Inject constructor(
    private val geminiService: GeminiService
) {
    fun isApiKeyValid(): Boolean = geminiService.isApiKeyValid()

    fun isConfigured(): Boolean = geminiService.isApiKeyConfigured()

    fun startChat(
        nickname: String,
        memoryFacts: List<MemoryFactEntity>,
        tools: List<AgentTool>,
        modelName: String = PRIMARY_MODEL
    ): GeminiChatSession? {
        if (!isConfigured()) return null
        return try {
            val model = geminiService.createGenerativeModel(
                nickname = nickname,
                memoryFacts = memoryFacts,
                tools = tools,
                modelName = modelName
            ) ?: return null
            GeminiChatSession(
                nickname = nickname,
                memoryFacts = memoryFacts,
                tools = tools,
                modelName = modelName,
                chat = model.startChat()
            )
        } catch (exception: Exception) {
            Log.e("GeminiRepo", "API call failed", exception)
            null
        }
    }

    suspend fun sendMessage(
        session: GeminiChatSession,
        content: Content
    ): GeminiCallResult {
        if (!isConfigured()) return GeminiCallResult.Failure(GeminiFailure.Unconfigured)
        val primary = executeWithRetry { session.chat.sendMessage(content) }
        if (primary is GeminiCallResult.Success) return primary

        Log.e(TAG, "Primary Gemini model (${session.modelName}) failed; trying fallback model $SECONDARY_MODEL.")
        val fallbackSession = startChat(
            nickname = session.nickname,
            memoryFacts = session.memoryFacts,
            tools = session.tools,
            modelName = SECONDARY_MODEL
        ) ?: return primary
        val fallbackResult = executeWithRetry { fallbackSession.chat.sendMessage(content) }
        if (fallbackResult is GeminiCallResult.Success) return fallbackResult

        if (session.modelName != PRIMARY_MODEL) {
            Log.e(TAG, "Secondary Gemini model failed; trying last-resort model $PRIMARY_MODEL.")
            val lastResortSession = startChat(
                nickname = session.nickname,
                memoryFacts = session.memoryFacts,
                tools = session.tools,
                modelName = PRIMARY_MODEL
            ) ?: return fallbackResult
            return executeWithRetry { lastResortSession.chat.sendMessage(content) }
        }
        return fallbackResult
    }

    suspend fun generateContent(
        prompt: String,
        modelName: String = PRIMARY_MODEL,
        fallbackModelName: String = SECONDARY_MODEL
    ): GeminiCallResult {
        if (!isConfigured()) return GeminiCallResult.Failure(GeminiFailure.Unconfigured)
        val primary = executeWithRetry {
            val model = geminiService.createGenerativeModel(
                nickname = "",
                memoryFacts = emptyList(),
                modelName = modelName
            ) ?: throw IllegalStateException("Failed to create generative model for $modelName")
            model.generateContent(prompt)
        }
        if (primary is GeminiCallResult.Success) return primary

        Log.e(TAG, "Primary Gemini model ($modelName) failed during generation; trying fallback model $fallbackModelName.")
        val fallbackResult = executeWithRetry {
            val model = geminiService.createGenerativeModel(
                nickname = "",
                memoryFacts = emptyList(),
                modelName = fallbackModelName
            ) ?: throw IllegalStateException("Failed to create fallback generative model for $fallbackModelName")
            model.generateContent(prompt)
        }
        if (fallbackResult is GeminiCallResult.Success) return fallbackResult

        if (fallbackModelName != PRIMARY_MODEL && modelName != PRIMARY_MODEL) {
            Log.e(TAG, "Fallback model ($fallbackModelName) failed; trying last-resort model $PRIMARY_MODEL.")
            return executeWithRetry {
                val model = geminiService.createGenerativeModel(
                    nickname = "",
                    memoryFacts = emptyList(),
                    modelName = PRIMARY_MODEL
                ) ?: throw IllegalStateException("Failed to create last-resort model $PRIMARY_MODEL")
                model.generateContent(prompt)
            }
        }
        return fallbackResult
    }

    suspend fun executeWithRetry(
        maxRetries: Int = 3,
        jitterProvider: () -> Long = { Random.nextLong(50L, 251L) },
        sleeper: suspend (Long) -> Unit = { delay(it) },
        logger: (GeminiFailure, Throwable) -> Unit = { _, throwable ->
            Log.e("GeminiRepo", "API call failed", throwable)
        },
        block: suspend () -> GenerateContentResponse
    ): GeminiCallResult {
        if (!isConfigured()) return GeminiCallResult.Failure(GeminiFailure.Unconfigured)
        var lastFailure: GeminiFailure = GeminiFailure.Unknown
        repeat(maxRetries + 1) { attempt ->
            try {
                return GeminiCallResult.Success(block())
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                val failure = mapFailure(throwable)
                lastFailure = failure
                logger(failure, throwable)
                if (!failure.isRetryable() || attempt == maxRetries) {
                    return GeminiCallResult.Failure(failure)
                }
                val backoffMillis = (1000.0 * 2.0.pow(attempt)).toLong() + jitterProvider()
                sleeper(backoffMillis)
            }
        }
        return GeminiCallResult.Failure(lastFailure)
    }

    fun mapFailure(throwable: Throwable): GeminiFailure {
        if (!isConfigured()) return GeminiFailure.Unconfigured
        if (throwable is IOException) return GeminiFailure.Network

        val chain = mutableListOf<Throwable>()
        var cursor: Throwable? = throwable
        while (cursor != null && chain.size < 8) {
            chain += cursor
            cursor = cursor.cause
        }

        val haystack = chain
            .joinToString(" ") { item ->
                listOfNotNull(
                    item::class.qualifiedName,
                    item.message,
                    item.localizedMessage
                ).joinToString(" ")
            }
            .lowercase()

        return when {
            containsStatus(haystack, 503) ||
                haystack.contains("unavailable") ||
                haystack.contains("overloaded") ||
                haystack.contains("service unavailable") -> GeminiFailure.Overloaded
            containsStatus(haystack, 429) ||
                haystack.contains("rate limit") ||
                haystack.contains("too many requests") ||
                haystack.contains("quota") -> GeminiFailure.RateLimited
            containsStatus(haystack, 404) ||
                haystack.contains("not_found") ||
                haystack.contains("not found") -> GeminiFailure.NotFound
            containsStatus(haystack, 400) ||
                haystack.contains("invalid argument") ||
                haystack.contains("bad request") -> GeminiFailure.InvalidRequest
            haystack.contains("network") ||
                haystack.contains("timeout") ||
                haystack.contains("connection") ||
                haystack.contains("unable to resolve host") -> GeminiFailure.Network
            else -> GeminiFailure.Unknown
        }
    }

    private fun GeminiFailure.isRetryable(): Boolean =
        this is GeminiFailure.Overloaded || this is GeminiFailure.RateLimited

    private fun containsStatus(text: String, status: Int): Boolean =
        Regex("""(^|[^0-9])$status([^0-9]|$)""").containsMatchIn(text)

    companion object {
        private const val TAG = "GeminiRepository"
        const val PRIMARY_MODEL = GeminiService.PRIMARY_MODEL
        const val SECONDARY_MODEL = GeminiService.SECONDARY_MODEL
        const val MODEL_NAME = PRIMARY_MODEL
    }
}
