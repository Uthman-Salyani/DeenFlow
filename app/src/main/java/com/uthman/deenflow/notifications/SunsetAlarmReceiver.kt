package com.uthman.deenflow.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.uthman.deenflow.data.calendar.CalendarRepository
import com.uthman.deenflow.data.calendar.HijriCalendarCalculator

class SunsetAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val repository = CalendarRepository(context)

        val anchor = repository.getAnchorDate()
        val anchorTimestamp = repository.getAnchorTimestamp()
        val sunset = repository.getSunsetTime()

        Log.d("DeenFlowAlarm", "Receiver fired. anchor=$anchor, anchorTimestamp=$anchorTimestamp, sunset=$sunset, now=${System.currentTimeMillis()}")

        if (anchor != null && sunset != null) {
            val now = System.currentTimeMillis()
            val result = HijriCalendarCalculator.calculateCurrentDate(
                anchor = anchor,
                anchorTimestamp = anchorTimestamp,
                sunsetHour = sunset.first,
                sunsetMinute = sunset.second,
                now = now
            )

            Log.d("DeenFlowAlarm", "Calculated result: date=${result.date}, needsConfirmation=${result.needsConfirmation}")

            repository.setAnchor(result.date, now)
            DeenFlowWidgetProvider.updateAllWidgets(context)

            if (result.needsConfirmation) {
                Log.d("DeenFlowAlarm", "Showing notification now")
                NotificationHelper.createChannel(context)
                NotificationHelper.showMoonSightingNotification(context)
            }

            AlarmScheduler.scheduleNextSunsetAlarm(context, sunset.first, sunset.second)
        } else {
            Log.d("DeenFlowAlarm", "Skipped — anchor or sunset was null")
        }
    }
}