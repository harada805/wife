package com.example.wife.agent

import com.example.wife.data.local.dao.MemoryFactDao
import com.example.wife.data.local.entity.ChatMessageEntity
import com.example.wife.data.local.entity.MemoryFactEntity
import android.util.Log
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryFactExtractor @Inject constructor(
    private val memoryFactDao: MemoryFactDao,
    private val geminiRepository: GeminiRepository
) {

    companion object {
        const val PRIMARY_MODEL = "gemini-1.5-flash"
        const val SECONDARY_MODEL = "gemini-1.5-pro"
        const val MODEL_NAME = PRIMARY_MODEL
    }

    suspend fun extractAndSaveFacts(recentMessages: List<ChatMessageEntity>) {
        if (!geminiRepository.isConfigured()) return

        val userMessages = recentMessages.filter { it.isFromUser }
        if (userMessages.isEmpty()) return

        val recentText = userMessages.takeLast(5).joinToString("\n") { it.text }
        val prompt = """
            Analisislah teks dari pengguna berikut. Ekstrak fakta-fakta singkat yang baru dipelajari tentang pengguna (seperti nama, hobi, makanan kesukaan, jadwal, teman, pekerjaan, atau preferensi).
            
            Format respons HANYA berupa JSON array valid dari objek yang memiliki kunci "category" dan "content".
            Contoh respons:
            [
              {"category": "Hobi", "content": "Suka minum kopi tanpa gula"},
              {"category": "Teman", "content": "Punya teman bernama Budi"}
            ]
            Jika tidak ada fakta pengguna baru yang dapat diekstrak, kembalikan JSON array kosong: [].
            
            Teks Pengguna:
            $recentText
        """.trimIndent()

        try {
            val response = when (val result = geminiRepository.generateContent(prompt, modelName = MODEL_NAME)) {
                is GeminiCallResult.Success -> result.response
                is GeminiCallResult.Failure -> return
            }
            val responseText = response.text ?: return

            val jsonString = extractJsonArrayString(responseText)
            if (jsonString.isNotBlank() && jsonString != "[]") {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val category = obj.optString("category", "General").trim()
                    val content = obj.optString("content", "").trim()
                    if (content.isNotEmpty()) {
                        memoryFactDao.insertFact(
                            MemoryFactEntity(
                                category = category.ifEmpty { "General" },
                                content = content
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MemoryFactExtractor", "Failed to extract memory facts.", e)
        }
    }

    private fun extractJsonArrayString(raw: String): String {
        val trimmed = raw.trim()
        val startIndex = trimmed.indexOf('[')
        val endIndex = trimmed.lastIndexOf(']')
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return trimmed.substring(startIndex, endIndex + 1)
        }
        return "[]"
    }
}
