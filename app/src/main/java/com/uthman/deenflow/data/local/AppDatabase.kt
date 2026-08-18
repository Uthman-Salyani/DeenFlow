package com.uthman.deenflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.uthman.deenflow.data.local.dao.AyahDao
import com.uthman.deenflow.data.local.dao.HadithBookDao
import com.uthman.deenflow.data.local.dao.HadithDao
import com.uthman.deenflow.data.local.dao.SurahDao
import com.uthman.deenflow.data.local.entity.AyahEntity
import com.uthman.deenflow.data.local.entity.HadithBookEntity
import com.uthman.deenflow.data.local.entity.HadithEntity
import com.uthman.deenflow.data.local.entity.SurahEntity

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        HadithBookEntity::class,
        HadithEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun hadithBookDao(): HadithBookDao
    abstract fun hadithDao(): HadithDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "deenflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}