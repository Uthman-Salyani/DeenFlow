package com.uthman.deenflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "hadiths")
data class HadithEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val hadithNumberInBook: Int,
    val arabicText: String,
    val narrator: String,
    val translationEn: String,
    val isBookmarked: Boolean = false
)