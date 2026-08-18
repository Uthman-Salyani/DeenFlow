package com.uthman.deenflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val surahNumber: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTransliteration: String,
    val revelationType: String,
    val ayahCount: Int
)