package com.uthman.deenflow.data.repository

import com.uthman.deenflow.data.local.dao.HadithBookDao
import com.uthman.deenflow.data.local.dao.HadithDao

class HadithRepository(
    private val hadithBookDao: HadithBookDao,
    private val hadithDao: HadithDao
) {
    fun getBooksForCollection(collection: String) = hadithBookDao.getBooksForCollection(collection)
    fun getHadithsForBook(bookId: Long) = hadithDao.getHadithsForBook(bookId)
}