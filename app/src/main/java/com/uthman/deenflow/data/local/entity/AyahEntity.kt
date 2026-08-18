package com.uthman.deenflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "ayahs")
data class AyahEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val ayahNumberInSurah: Int,
    val arabicTextUthmani: String,
    val arabicTextIndoPak: String,
    val translationEn: String,
    val juz: Int,
    val isBookmarked: Boolean = false
)