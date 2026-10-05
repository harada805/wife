package com.example.wife.agent

import android.util.Log
import com.example.wife.agent.tool.AgentTool
import com.example.wife.data.local.entity.ChatMessageEntity
import com.example.wife.data.local.entity.MemoryFactEntity
import com.example.wife.persona.PersonaPrompt
import com.google.firebase.ai.ai
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Perakit payload Gemini. Murni fungsi string/map — tanpa Android, tanpa Firebase,
 * jadi bisa dites langsung dari unit test JVM.
 */
@Singleton
class GeminiService @Inject constructor() {

    companion object {
        const val PRIMARY_MODEL = "gemini-1.5-flash"
        const val SECONDARY_MODEL = "gemini-1.5-pro"

        const val BASE_URL = "https://generativelanguage.googleapis.com/"

        /**
         * Satu-satunya sumber pesan gagal ke pengguna.
         */
        const val ERROR_UNCONFIGURED = "Duh, API key-nya belum terpasang. Coba dicek pengaturannya ya."
        const val ERROR_AUTH = "API key-nya ditolak server. Coba cek apakah key-nya masih aktif."
        const val ERROR_MODEL = "Model Gemini yang diminta nggak ketemu di server. Cek nama modelnya ya."
        const val ERROR_NETWORK = "Sambunganku ke server lagi terputus. Cek internetnya ya."
        const val ERROR_RATE_LIMIT = "Kayaknya Laras perlu tarik napas dulu. Coba lagi sebentar ya."
        const val ERROR_OVERLOADED = "Aduh, server Gemini lagi ketahan rame banget. Coba lagi sebentar ya."
        const val ERROR_INVALID_REQUEST = "Laras belum nangkep maksudnya dengan rapi. Coba tulis sedikit beda ya."
        const val ERROR_UNKNOWN = "Ada yang gagal di sambunganku ke Gemini. Coba lagi ya."
    }

    fun getApiKey(): String {
        val key = BuildConfigSafe.apiKey().trim()
        if (key.isBlank() || key.equals("null", ignoreCase = true)) {
            return ""
        }
        return key
    }

    fun isApiKeyValid(): Boolean = getApiKey().isNotBlank()

    fun isApiKeyConfigured(): Boolean = isApiKeyValid()

    fun buildSystemInstruction(nickname: String, memoryFacts: List<MemoryFactEntity>): String {
        val basePrompt = PersonaPrompt.getSystemPrompt(nickname)
        if (memoryFacts.isEmpty()) return basePrompt

        val factsFormatted = memoryFacts.joinToString("\n") { fact ->
            "- [${fact.category}] ${fact.content}"
        }

        return """
            $basePrompt

            Fakta penting yang kamu ingat tentang $nickname:
            $factsFormatted
        """.trimIndent()
    }

    /**
     * Riwayat percakapan + prompt aktif digabung jadi satu pesan teks.
     *
     * Kenapa bukan beberapa pesan bergantian role: endpoint generativelanguage menolak
     * role "function" untuk bagian teks ("Please use a valid role"). Urutan percakapan
     * dijaga di sisi kita lewat ChatRepository.getConversation(), jadi model tidak
     * kehilangan konteks meski tanpa metadata role.
     */
    fun buildPrompt(
        history: List<ChatMessageEntity>,
        currentMessage: String,
        userLabel: String = "Pengguna"
    ): String {
        val label = userLabel.ifBlank { "Pengguna" }
        val builder = StringBuilder()
        history.forEach { message ->
            val speaker = if (message.isFromUser) label else "Laras"
            builder.append(speaker).append(": ").append(message.text.trim()).append('\n')
        }
        builder.append(label).append(": ").append(currentMessage.trim())
        return builder.toString()
    }

    fun buildTools(tools: List<AgentTool>): Map<String, Any>? {
        if (tools.isEmpty()) return null
        val declarations = tools.map { tool ->
            mapOf(
                "name" to tool.name,
                "description" to tool.description,
                "parameters" to tool.parameters
            )
        }
        return mapOf("functionDeclarations" to declarations)
    }

    fun createGenerativeModel(
        nickname: String,
        memoryFacts: List<MemoryFactEntity>,
        tools: List<AgentTool> = emptyList(),
        modelName: String = PRIMARY_MODEL
    ): com.google.firebase.ai.GenerativeModel? {
        val apiKey = com.example.wife.BuildConfig.GEMINI_API_KEY.ifBlank { getApiKey() }
        if (apiKey.isBlank()) {
            Log.e("GeminiService", "Call failed: API key is blank or unconfigured")
            return null
        }
        val systemInstructionText = buildSystemInstruction(nickname, memoryFacts)
        val systemInstruction = com.google.firebase.ai.type.content {
            text(systemInstructionText)
        }

        return try {
            com.google.firebase.Firebase.ai.generativeModel(
                modelName = modelName,
                systemInstruction = systemInstruction
            )
        } catch (exception: Exception) {
            Log.e("GeminiRepo", "API call failed", exception)
            null
        }
    }
}
