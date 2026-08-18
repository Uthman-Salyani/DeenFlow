package com.uthman.deenflow.ui.hadith

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
import com.uthman.deenflow.data.local.entity.HadithEntity

@Composable
fun HadithReaderScreen(
    bookId: Long,
    viewModel: HadithReaderViewModel = viewModel()
) {
    val hadiths by viewModel.hadiths.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadHadiths(bookId)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(hadiths, key = { it.id }) { hadith ->
            HadithRow(hadith = hadith)
            HorizontalDivider()
        }
    }
}

@Composable
fun HadithRow(hadith: HadithEntity) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = hadith.arabicText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = hadith.narrator,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = hadith.translationEn,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}