package com.uthman.deenflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "hadith_books")
data class HadithBookEntity(
    @PrimaryKey val id: Long,
    val collection: String,
    val bookNumber: Int,
    val nameEnglish: String,
    val nameArabic: String,
    val hadithCount: Int
)