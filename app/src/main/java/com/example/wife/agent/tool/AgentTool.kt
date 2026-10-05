package com.example.wife.agent.tool

enum class RiskLevel {
    READ_ONLY,
    NEEDS_CONFIRMATION,
    FORBIDDEN
}

sealed class ToolResult {
    data class Success(val message: String) : ToolResult()
    data class Failure(val error: String) : ToolResult()
    object ConfirmationRequired : ToolResult()
}

interface AgentTool {
    val name: String
    val description: String
    val parameters: Map<String, Any>
    val riskLevel: RiskLevel
    suspend fun execute(args: Map<String, Any?>): ToolResult
}
