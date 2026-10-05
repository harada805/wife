package com.example.wife.agent.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST langsung ke Gemini Developer API. Ini jalur yang sudah terbukti hidup untuk key
 * yang dipakai aplikasi ini, tanpa perlu Firebase project / google-services.json.
 */
interface GeminiApi {

    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Header("x-goog-api-key") apiKey: String,
        @Body body: GenerateContentRequest
    ): GenerateContentResponse
}

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<WireContent>,
    val tools: List<WireTool>? = null,
    val systemInstruction: WireContent? = null,
    val generationConfig: GenerationConfig? = null
)

// ponytail: tanpa "role". Model terbaru menolak role tak dikenal, dan prompt/riwayat
// digabung jadi satu blok teks — lihat GeminiRepository.buildContents.
@JsonClass(generateAdapter = true)
data class WireContent(
    val parts: List<WirePart>,
    @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class WirePart(
    val text: String? = null,
    @Json(name = "functionCall") val functionCall: WireFunctionCall? = null,
    @Json(name = "functionResponse") val functionResponse: WireFunctionResponse? = null
)

@JsonClass(generateAdapter = true)
data class WireFunctionCall(
    val name: String,
    val args: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class WireFunctionResponse(
    val name: String,
    val response: Map<String, Any?>? = null
)

@JsonClass(generateAdapter = true)
data class WireTool(
    @Json(name = "functionDeclarations") val functionDeclarations: List<WireFunctionDeclaration>
)

@JsonClass(generateAdapter = true)
data class WireFunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Double? = null,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<WireCandidate>? = null,
    @Json(name = "promptFeedback") val promptFeedback: WirePromptFeedback? = null
) {
    val firstContent: WireContent? get() = candidates?.firstOrNull()?.content
    val firstParts: List<WirePart> get() = firstContent?.parts.orEmpty()
    val text: String? get() = firstParts.mapNotNull { it.text }.firstOrNull { it.isNotBlank() }
    val functionCall: WireFunctionCall? get() = firstParts.firstNotNullOfOrNull { it.functionCall }
}

@JsonClass(generateAdapter = true)
data class WireCandidate(
    val content: WireContent? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class WirePromptFeedback(val blockReason: String? = null)

/** Amplop error standar Google API. */
@JsonClass(generateAdapter = true)
data class WireErrorEnvelope(val error: WireError? = null)

@JsonClass(generateAdapter = true)
data class WireError(
    val code: Int = 0,
    val message: String = "",
    val status: String = ""
)
