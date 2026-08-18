package com.uthman.deenflow.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uthman.deenflow.data.calendar.CalendarRepository
import com.uthman.deenflow.data.calendar.HijriCalendarCalculator
import com.uthman.deenflow.data.calendar.HijriDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CalendarRepository(application)

    private val _currentDate = MutableStateFlow<HijriDate?>(null)
    val currentDate: StateFlow<HijriDate?> = _currentDate.asStateFlow()

    private val _needsConfirmation = MutableStateFlow(false)
    val needsConfirmation: StateFlow<Boolean> = _needsConfirmation.asStateFlow()

    private val _sunsetTime = MutableStateFlow<Pair<Int, Int>?>(null)
    val sunsetTime: StateFlow<Pair<Int, Int>?> = _sunsetTime.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        val anchor = repository.getAnchorDate()
        val anchorTimestamp = repository.getAnchorTimestamp()
        val sunset = repository.getSunsetTime()
        _sunsetTime.value = sunset

        if (anchor == null) {
            _currentDate.value = null
            _needsConfirmation.value = false
            return
        }

        if (sunset == null) {
            _currentDate.value = anchor
            _needsConfirmation.value = false
            return
        }

        val now = System.currentTimeMillis()
        val result = HijriCalendarCalculator.calculateCurrentDate(
            anchor = anchor,
            anchorTimestamp = anchorTimestamp,
            sunsetHour = sunset.first,
            sunsetMinute = sunset.second,
            now = now
        )
        _currentDate.value = result.date
        _needsConfirmation.value = result.needsConfirmation

        repository.setAnchor(result.date, now)
        com.uthman.deenflow.notifications.DeenFlowWidgetProvider.updateAllWidgets(getApplication())
    }

    fun setManualDate(day: Int, month: Int, year: Int) {
        viewModelScope.launch {
            repository.setAnchor(HijriDate(day, month, year), System.currentTimeMillis())
            refresh()
        }
    }

    fun setSunsetTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.setSunsetTime(hour, minute)
            com.uthman.deenflow.notifications.AlarmScheduler.scheduleNextSunsetAlarm(
                getApplication(), hour, minute
            )
            refresh()
        }
    }

    fun confirmDay30() {
        val current = _currentDate.value ?: return
        setManualDate(30, current.month, current.year)
    }

    fun confirmNewMonth() {
        val current = _currentDate.value ?: return
        var newMonth = current.month + 1
        var newYear = current.year
        if (newMonth > 12) {
            newMonth = 1
            newYear += 1
        }
        setManualDate(1, newMonth, newYear)
    }
}