package com.uthman.deenflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uthman.deenflow.data.local.entity.AyahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AyahDao {

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumberInSurah ASC")
    fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE isBookmarked = 1")
    fun getBookmarkedAyahs(): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE juz = :juzNumber ORDER BY surahNumber ASC, ayahNumberInSurah ASC")
    fun getAyahsForJuz(juzNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE id IN (SELECT MIN(id) FROM ayahs GROUP BY juz) ORDER BY juz ASC")
    fun getJuzStartAyahs(): Flow<List<AyahEntity>>

    @Update
    suspend fun updateAyah(ayah: AyahEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ayahs: List<AyahEntity>)
}