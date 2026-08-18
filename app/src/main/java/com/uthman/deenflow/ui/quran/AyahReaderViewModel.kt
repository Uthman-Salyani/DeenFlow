package com.uthman.deenflow.ui.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uthman.deenflow.data.local.AppDatabase
import com.uthman.deenflow.data.local.entity.AyahEntity
import com.uthman.deenflow.data.repository.QuranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AyahReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuranRepository

    private val _ayahs = MutableStateFlow<List<AyahEntity>>(emptyList())
    val ayahs: StateFlow<List<AyahEntity>> = _ayahs.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = QuranRepository(db.surahDao(), db.ayahDao())
    }

    fun loadAyahs(surahNumber: Int) {
        viewModelScope.launch {
            repository.getAyahsForSurah(surahNumber).collect { ayahList ->
                _ayahs.value = ayahList
            }
        }
    }
}