package com.uthman.deenflow.ui.hadith

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uthman.deenflow.data.local.AppDatabase
import com.uthman.deenflow.data.local.entity.HadithBookEntity
import com.uthman.deenflow.data.repository.HadithRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HadithViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HadithRepository

    private val _books = MutableStateFlow<List<HadithBookEntity>>(emptyList())
    val books: StateFlow<List<HadithBookEntity>> = _books.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = HadithRepository(db.hadithBookDao(), db.hadithDao())
    }

    fun loadBooks(collection: String) {
        viewModelScope.launch {
            repository.getBooksForCollection(collection).collect { bookList ->
                _books.value = bookList
            }
        }
    }
}