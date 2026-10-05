package com.example.wife.agent.tool

/**
 * Schema function declaration untuk Gemini REST. Bentuknya OpenAPI subset:
 * { type: "object", properties: {...}, required: [...] }.
 * Tool mendeklarasikan parameter-nya sebagai Map, jadi langsung dipakai apa adanya.
 */
fun AgentTool.toFunctionDeclaration(): Map<String, Any> = mapOf(
    "name" to name,
    "description" to description,
    "parameters" to parameters
)
