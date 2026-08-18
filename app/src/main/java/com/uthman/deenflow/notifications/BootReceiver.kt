package com.uthman.deenflow.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uthman.deenflow.data.calendar.CalendarRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = CalendarRepository(context)
            val sunset = repository.getSunsetTime()
            if (sunset != null) {
                AlarmScheduler.scheduleNextSunsetAlarm(context, sunset.first, sunset.second)
            }
        }
    }
}