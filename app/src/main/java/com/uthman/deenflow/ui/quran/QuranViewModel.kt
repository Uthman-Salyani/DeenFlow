package com.uthman.deenflow.ui.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uthman.deenflow.data.local.AppDatabase
import com.uthman.deenflow.data.local.entity.SurahEntity
import com.uthman.deenflow.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.uthman.deenflow.data.local.entity.AyahEntity

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository

    private val _surahs = MutableStateFlow<List<SurahEntity>>(emptyList())
    val surahs: StateFlow<List<SurahEntity>> = _surahs.asStateFlow()

    private val _juzStartAyahs = MutableStateFlow<List<AyahEntity>>(emptyList())
    val juzStartAyahs: StateFlow<List<AyahEntity>> = _juzStartAyahs.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.surahDao(), db.ayahDao())

        viewModelScope.launch {
            repository.getAllSurahs().collect { surahList ->
                _surahs.value = surahList
            }
        }
        viewModelScope.launch {
            repository.getJuzStartAyahs().collect { list ->
                _juzStartAyahs.value = list
            }
        }
    }
}