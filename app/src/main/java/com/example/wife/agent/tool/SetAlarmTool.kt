package com.example.wife.agent.tool

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetAlarmTool @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AgentTool {

    override val name: String = "set_alarm"

    override val description: String = "Pasang alarm pada jam dan menit tertentu dengan label"

    override val parameters: Map<String, Any> = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "hour" to mapOf("type" to "integer", "description" to "Jam alarm (0-23)"),
            "minutes" to mapOf("type" to "integer", "description" to "Menit alarm (0-59)"),
            "message" to mapOf("type" to "string", "description" to "Label atau pesan alarm")
        ),
        "required" to listOf("hour", "minutes")
    )

    override val riskLevel: RiskLevel = RiskLevel.NEEDS_CONFIRMATION

    override suspend fun execute(args: Map<String, Any?>): ToolResult {
        return try {
            val hour = args["hour"]?.toString()?.toDoubleOrNull()?.toInt()
                ?: args["hour"]?.toString()?.toIntOrNull()
                ?: args["jam"]?.toString()?.toIntOrNull()
                ?: return ToolResult.Failure("Jam alarm tidak valid.")

            val minutes = args["minutes"]?.toString()?.toDoubleOrNull()?.toInt()
                ?: args["minutes"]?.toString()?.toIntOrNull()
                ?: args["menit"]?.toString()?.toIntOrNull()
                ?: 0

            val message = args["message"]?.toString()
                ?: args["label"]?.toString()
                ?: args["pesan"]?.toString()
                ?: "Alarm"

            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)

            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minutes)
            ToolResult.Success("Alarm berhasil dipasang untuk jam $formattedTime dengan label '$message'.")
        } catch (e: Exception) {
            Log.e("SetAlarmTool", "Failed to set alarm.", e)
            ToolResult.Failure("Alarmnya belum berhasil Laras pasang.")
        }
    }
}
