package com.uthman.deenflow.ui.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uthman.deenflow.data.local.entity.SurahEntity

@Composable
fun QuranScreen(
    viewModel: QuranViewModel = viewModel(),
    onSurahClick: (Int) -> Unit
) {
    val surahs by viewModel.surahs.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(surahs, key = { it.surahNumber }) { surah ->
            SurahRow(surah = surah, onClick = { onSurahClick(surah.surahNumber) })
            HorizontalDivider()
        }
    }
}

@Composable
fun SurahRow(surah: SurahEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = surah.surahNumber.toString(),
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(text = surah.nameTransliteration, style = MaterialTheme.typography.bodyLarge)
            Text(text = surah.revelationType, style = MaterialTheme.typography.bodySmall)
        }

        Text(text = surah.nameArabic, style = MaterialTheme.typography.titleMedium)
    }
}