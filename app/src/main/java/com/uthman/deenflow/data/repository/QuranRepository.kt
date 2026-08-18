package com.uthman.deenflow.data.repository

import com.uthman.deenflow.data.local.dao.AyahDao
import com.uthman.deenflow.data.local.dao.SurahDao

class QuranRepository(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao
) {
    fun getAllSurahs() = surahDao.getAllSurahs()
    fun getAyahsForSurah(surahNumber: Int) = ayahDao.getAyahsForSurah(surahNumber)
}