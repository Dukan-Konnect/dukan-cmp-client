package org.example.project.booking.util


import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun formatScheduledDateString(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        formatLocalDateTime(dateTime)
    } catch (e: Exception) {
        try {
            val dateTime = LocalDateTime.parse(dateString)
            formatLocalDateTime(dateTime)
        } catch (e2: Exception) {
            dateString
        }
    }
}

@OptIn(ExperimentalTime::class)
fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return formatLocalDateTime(dateTime)
}

private fun formatLocalDateTime(dateTime: LocalDateTime): String {
    val month = when (dateTime.monthNumber) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
        5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
        9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> ""
    }

    val hour = if (dateTime.hour == 0) 12 else if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
    val amPm = if (dateTime.hour < 12) "AM" else "PM"
    val minute = dateTime.minute.toString().padStart(2, '0')

    return "${dateTime.dayOfMonth} $month ${dateTime.year}, $hour:$minute $amPm"
}