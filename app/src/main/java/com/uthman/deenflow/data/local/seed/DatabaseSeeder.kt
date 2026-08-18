package com.uthman.deenflow.data.local.seed

import android.content.Context
import com.uthman.deenflow.data.local.AppDatabase
import com.uthman.deenflow.data.local.entity.AyahEntity
import com.uthman.deenflow.data.local.entity.HadithBookEntity
import com.uthman.deenflow.data.local.entity.HadithEntity
import com.uthman.deenflow.data.local.entity.SurahEntity
import kotlinx.serialization.json.Json

object DatabaseSeeder {

    private val json = Json { ignoreUnknownKeys = true }
    private const val PREFS_NAME = "deenflow_prefs"
    private const val KEY_IS_SEEDED = "is_database_seeded"

    suspend fun seedIfNeeded(context: Context) {
        val db = AppDatabase.getInstance(context)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alreadySeeded = prefs.getBoolean(KEY_IS_SEEDED, false)

        if (alreadySeeded) return

        val surahs = readAsset<List<SurahEntity>>(context, "surahs.json")
        val ayahs = readAsset<List<AyahEntity>>(context, "ayahs.json")
        val hadithBooks = readAsset<List<HadithBookEntity>>(context, "hadith_books.json")
        val hadiths = readAsset<List<HadithEntity>>(context, "hadiths.json")

        db.surahDao().insertAll(surahs)
        db.ayahDao().insertAll(ayahs)
        db.hadithBookDao().insertAll(hadithBooks)
        db.hadithDao().insertAll(hadiths)

        prefs.edit().putBoolean(KEY_IS_SEEDED, true).apply()
    }

    private inline fun <reified T> readAsset(context: Context, fileName: String): T {
        val jsonString = context.assets.open(fileName)
            .bufferedReader()
            .use { it.readText() }
        return json.decodeFromString(jsonString)
    }
}