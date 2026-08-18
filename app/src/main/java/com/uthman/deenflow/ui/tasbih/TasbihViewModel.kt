package com.uthman.deenflow.ui.tasbih

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasbihViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("deenflow_prefs", Application.MODE_PRIVATE)

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _incrementStep = MutableStateFlow(1)
    val incrementStep: StateFlow<Int> = _incrementStep.asStateFlow()

    private val _goal = MutableStateFlow<Int?>(null)
    val goal: StateFlow<Int?> = _goal.asStateFlow()

    private val _goalReachedEvent = MutableStateFlow(false)
    val goalReachedEvent: StateFlow<Boolean> = _goalReachedEvent.asStateFlow()

    init {
        _count.value = prefs.getInt("tasbih_count", 0)
        _incrementStep.value = prefs.getInt("tasbih_increment_step", 1)
        val savedGoal = prefs.getInt("tasbih_goal", -1)
        _goal.value = if (savedGoal >= 0) savedGoal else null
    }

    private fun saveCount(value: Int) {
        prefs.edit().putInt("tasbih_count", value).apply()
    }

    fun increment() {
        val newCount = _count.value + _incrementStep.value
        _count.value = newCount
        saveCount(newCount)

        if (_goal.value != null && newCount >= _goal.value!! && (newCount - _incrementStep.value) < _goal.value!!) {
            _goalReachedEvent.value = true
        }
    }

    fun consumeGoalReachedEvent() {
        _goalReachedEvent.value = false
    }

    fun decrement() {
        if (_count.value <= 0) return
        val newCount = maxOf(0, _count.value - _incrementStep.value)
        _count.value = newCount
        saveCount(newCount)
    }

    fun reset() {
        _count.value = 0
        saveCount(0)
    }

    fun setCountDirectly(value: Int) {
        val newCount = value.coerceAtLeast(0)
        _count.value = newCount
        saveCount(newCount)
    }

    fun setIncrementStep(step: Int) {
        _incrementStep.value = step
        prefs.edit().putInt("tasbih_increment_step", step).apply()
    }

    fun setGoal(newGoal: Int?) {
        _goal.value = newGoal
        prefs.edit().putInt("tasbih_goal", newGoal ?: -1).apply()
    }
}