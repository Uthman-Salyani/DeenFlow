package com.uthman.deenflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uthman.deenflow.data.local.entity.HadithEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {

    @Query("SELECT * FROM hadiths WHERE bookId = :bookId ORDER BY hadithNumberInBook ASC")
    fun getHadithsForBook(bookId: Long): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE isBookmarked = 1")
    fun getBookmarkedHadiths(): Flow<List<HadithEntity>>

    @Update
    suspend fun updateHadith(hadith: HadithEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hadiths: List<HadithEntity>)
}