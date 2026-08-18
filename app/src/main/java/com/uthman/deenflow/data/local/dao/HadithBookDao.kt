package com.uthman.deenflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uthman.deenflow.data.local.entity.HadithBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithBookDao {

    @Query("SELECT * FROM hadith_books WHERE collection = :collection ORDER BY bookNumber ASC")
    fun getBooksForCollection(collection: String): Flow<List<HadithBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<HadithBookEntity>)
}