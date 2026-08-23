package com.uthman.deenflow.ui.quran

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uthman.deenflow.data.local.entity.SurahEntity

private enum class QuranTab { SURAH, JUZ }

@Composable
fun QuranScreen(
    viewModel: QuranViewModel = viewModel(),
    onSurahClick: (Int) -> Unit
) {
    val surahs by viewModel.surahs.collectAsState()
    var selectedTab by remember { mutableStateOf(QuranTab.SURAH) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            FilterChip(
                selected = selectedTab == QuranTab.JUZ,
                onClick = { selectedTab = QuranTab.JUZ },
                label = { Text("Juz") },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            FilterChip(
                selected = selectedTab == QuranTab.SURAH,
                onClick = { selectedTab = QuranTab.SURAH },
                label = { Text("Surah") },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }

        when (selectedTab) {
            QuranTab.SURAH -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(surahs, key = { it.surahNumber }) { surah ->
                        SurahRow(surah = surah, onClick = { onSurahClick(surah.surahNumber) })
                        HorizontalDivider()
                    }
                }
            }
            QuranTab.JUZ -> {
                val juzStartAyahs by viewModel.juzStartAyahs.collectAsState()
                val surahNameLookup = remember(surahs) { surahs.associateBy { it.surahNumber } }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(juzStartAyahs, key = { it.juz }) { startAyah ->
                        val surahName = surahNameLookup[startAyah.surahNumber]?.nameTransliteration ?: ""
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSurahClick(startAyah.surahNumber) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = startAyah.juz.toString(),
                                modifier = Modifier.width(32.dp),
                                textAlign = TextAlign.Center
                            )
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(text = "Juz ${startAyah.juz}", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "Starts at $surahName, Ayah ${startAyah.ayahNumberInSurah}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
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