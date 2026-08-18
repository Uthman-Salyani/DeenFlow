package com.uthman.deenflow.ui.hadith

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uthman.deenflow.data.local.AppDatabase
import com.uthman.deenflow.data.local.entity.HadithEntity
import com.uthman.deenflow.data.repository.HadithRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HadithReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HadithRepository

    private val _hadiths = MutableStateFlow<List<HadithEntity>>(emptyList())
    val hadiths: StateFlow<List<HadithEntity>> = _hadiths.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = HadithRepository(db.hadithBookDao(), db.hadithDao())
    }

    fun loadHadiths(bookId: Long) {
        viewModelScope.launch {
            repository.getHadithsForBook(bookId).collect { hadithList ->
                _hadiths.value = hadithList
            }
        }
    }
}