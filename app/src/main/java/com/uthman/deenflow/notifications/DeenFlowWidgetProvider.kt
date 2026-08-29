package com.uthman.deenflow.notifications

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.uthman.deenflow.R
import com.uthman.deenflow.data.calendar.CalendarRepository
import com.uthman.deenflow.data.calendar.HijriCalendarCalculator
import com.uthman.deenflow.data.calendar.HijriDate
import com.uthman.deenflow.data.calendar.HijriMonths
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeenFlowWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val widgetIds = manager.getAppWidgetIds(
                android.content.ComponentName(context, DeenFlowWidgetProvider::class.java)
            )
            for (widgetId in widgetIds) {
                updateWidget(context, manager, widgetId)
            }
        }

        private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val repository = CalendarRepository(context)
            val views = RemoteViews(context.packageName, R.layout.widget_calendar)

            val anchor = repository.getAnchorDate()
            val anchorTimestamp = repository.getAnchorTimestamp()
            val sunset = repository.getSunsetTime()

            if (anchor == null) {
                views.setViewVisibility(R.id.widget_content_group, View.GONE)
                views.setViewVisibility(R.id.widget_empty_message, View.VISIBLE)
            } else {
                val displayDate: HijriDate = if (sunset != null) {
                    HijriCalendarCalculator.calculateCurrentDate(
                        anchor = anchor,
                        anchorTimestamp = anchorTimestamp,
                        sunsetHour = sunset.first,
                        sunsetMinute = sunset.second
                    ).date
                } else {
                    anchor
                }

                views.setViewVisibility(R.id.widget_content_group, View.VISIBLE)
                views.setViewVisibility(R.id.widget_empty_message, View.GONE)

                views.setTextViewText(R.id.widget_arabic_month, HijriMonths.arabic(displayDate.month))
                views.setTextViewText(R.id.widget_arabic_day, HijriMonths.toArabicIndicNumeral(displayDate.day))

                val gregorianDate = SimpleDateFormat("MMM dd", Locale.US).format(Date())
                views.setTextViewText(R.id.widget_gregorian_date, gregorianDate)
                views.setTextViewText(
                    R.id.widget_hijri_month_day,
                    "${HijriMonths.english(displayDate.month)} ${displayDate.day}"
                )
                views.setTextViewText(R.id.widget_hijri_year, "${displayDate.year} A.H")
            }

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}