package com.uthman.deenflow.ui.quran

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uthman.deenflow.data.local.entity.AyahEntity

@Composable
fun AyahReaderScreen(
    surahNumber: Int,
    viewModel: AyahReaderViewModel = viewModel()
) {
    val ayahs by viewModel.ayahs.collectAsState()

    LaunchedEffect(surahNumber) {
        viewModel.loadAyahs(surahNumber)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(ayahs, key = { it.id }) { ayah ->
            AyahRow(ayah = ayah)
            HorizontalDivider()
        }
    }
}

@Composable
fun AyahRow(ayah: AyahEntity) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "${ayah.arabicTextUthmani} ۝${toArabicIndicNumeral(ayah.ayahNumberInSurah)}",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = ayah.translationEn,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun toArabicIndicNumeral(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { arabicDigits[it - '0'] }.joinToString("")
}