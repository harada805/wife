package com.example.wife.agent.tool

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadCalendarTodayTool @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AgentTool {

    override val name: String = "read_calendar_today"

    override val description: String = "Membaca jadwal kalender hari ini"

    override val parameters: Map<String, Any> = mapOf(
        "type" to "object",
        "properties" to emptyMap<String, Any>()
    )

    override val riskLevel: RiskLevel = RiskLevel.READ_ONLY

    override suspend fun execute(args: Map<String, Any?>): ToolResult {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return ToolResult.Failure("Izin READ_CALENDAR belum diberikan. Mohon berikan izin mengakses kalender terlebih dahulu.")
        }

        return try {
            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val projection = arrayOf(
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.EVENT_LOCATION
            )

            val selection = "(${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTSTART} <= ?) AND (${CalendarContract.Events.DELETED} = 0)"
            val selectionArgs = arrayOf(startOfDay.toString(), endOfDay.toString())
            val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

            val eventsList = mutableListOf<String>()
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val titleIdx = cursor.getColumnIndex(CalendarContract.Events.TITLE)
                val startIdx = cursor.getColumnIndex(CalendarContract.Events.DTSTART)
                val endIdx = cursor.getColumnIndex(CalendarContract.Events.DTEND)
                val locationIdx = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION)

                while (cursor.moveToNext()) {
                    val title = if (titleIdx != -1) cursor.getString(titleIdx) else "Tanpa Judul"
                    val startTimeMs = if (startIdx != -1) cursor.getLong(startIdx) else 0L
                    val endTimeMs = if (endIdx != -1) cursor.getLong(endIdx) else 0L
                    val location = if (locationIdx != -1) cursor.getString(locationIdx) else null

                    val startTimeStr = if (startTimeMs > 0) timeFormat.format(startTimeMs) else "-"
                    val endTimeStr = if (endTimeMs > 0) timeFormat.format(endTimeMs) else "-"

                    val eventDetail = buildString {
                        append("- $title ($startTimeStr - $endTimeStr)")
                        if (!location.isNullOrBlank()) {
                            append(" di $location")
                        }
                    }
                    eventsList.add(eventDetail)
                }
            }

            if (eventsList.isEmpty()) {
                ToolResult.Success("Tidak ada jadwal atau agenda di kalender untuk hari ini.")
            } else {
                val resultText = "Jadwal kalender hari ini:\n" + eventsList.joinToString("\n")
                ToolResult.Success(resultText)
            }
        } catch (e: Exception) {
            Log.e("ReadCalendarTodayTool", "Failed to read calendar.", e)
            ToolResult.Failure("Laras belum berhasil baca kalendernya.")
        }
    }
}
