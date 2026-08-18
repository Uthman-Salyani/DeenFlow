package com.uthman.deenflow.data.calendar

import android.content.Context

class CalendarRepository(context: Context) {

    private val prefs = context.getSharedPreferences("deenflow_prefs", Context.MODE_PRIVATE)

    fun getAnchorDate(): HijriDate? {
        val day = prefs.getInt("hijri_anchor_day", -1)
        val month = prefs.getInt("hijri_anchor_month", -1)
        val year = prefs.getInt("hijri_anchor_year", -1)
        if (day == -1 || month == -1 || year == -1) return null
        return HijriDate(day, month, year)
    }

    fun getAnchorTimestamp(): Long {
        return prefs.getLong("hijri_anchor_timestamp", -1L)
    }

    fun setAnchor(date: HijriDate, timestamp: Long) {
        prefs.edit()
            .putInt("hijri_anchor_day", date.day)
            .putInt("hijri_anchor_month", date.month)
            .putInt("hijri_anchor_year", date.year)
            .putLong("hijri_anchor_timestamp", timestamp)
            .apply()
    }

    fun getSunsetTime(): Pair<Int, Int>? {
        val hour = prefs.getInt("sunset_hour", -1)
        val minute = prefs.getInt("sunset_minute", -1)
        if (hour == -1 || minute == -1) return null
        return hour to minute
    }

    fun setSunsetTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt("sunset_hour", hour)
            .putInt("sunset_minute", minute)
            .apply()
    }
}