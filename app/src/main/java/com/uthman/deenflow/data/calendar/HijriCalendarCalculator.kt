package com.uthman.deenflow.data.calendar

import java.util.Calendar

data class CalendarResult(
    val date: HijriDate,
    val needsConfirmation: Boolean
)

object HijriCalendarCalculator {

    fun calculateCurrentDate(
        anchor: HijriDate,
        anchorTimestamp: Long,
        sunsetHour: Int,
        sunsetMinute: Int,
        now: Long = System.currentTimeMillis()
    ): CalendarResult {
        var day = anchor.day
        var month = anchor.month
        var year = anchor.year

        var boundary = nextSunsetBoundaryAfter(anchorTimestamp, sunsetHour, sunsetMinute)
        var needsConfirmation = false

        while (boundary <= now) {
            when {
                day == 29 -> {
                    needsConfirmation = true
                    return CalendarResult(HijriDate(day, month, year), needsConfirmation)
                }
                day == 30 -> {
                    day = 1
                    month += 1
                    if (month > 12) {
                        month = 1
                        year += 1
                    }
                }
                else -> day += 1
            }
            boundary = nextSunsetBoundaryAfter(boundary, sunsetHour, sunsetMinute)
        }

        return CalendarResult(HijriDate(day, month, year), needsConfirmation)
    }

    private fun nextSunsetBoundaryAfter(fromTimestamp: Long, hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = fromTimestamp
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        if (cal.timeInMillis <= fromTimestamp) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return cal.timeInMillis
    }
}