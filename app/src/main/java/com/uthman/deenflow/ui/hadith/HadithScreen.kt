package com.uthman.deenflow.ui.hadith

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uthman.deenflow.data.local.entity.HadithBookEntity

@Composable
fun HadithScreen(
    viewModel: HadithViewModel = viewModel(),
    onBookClick: (Long) -> Unit
) {
    var selectedCollection by remember { mutableStateOf("Saheeh Bukhari") }
    val books by viewModel.books.collectAsState()

    LaunchedEffect(selectedCollection) {
        viewModel.loadBooks(selectedCollection)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            FilterChip(
                selected = selectedCollection == "Saheeh Bukhari",
                onClick = { selectedCollection = "Saheeh Bukhari" },
                label = { Text("Saheeh Bukhari") },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            FilterChip(
                selected = selectedCollection == "Saheeh Muslim",
                onClick = { selectedCollection = "Saheeh Muslim" },
                label = { Text("Saheeh Muslim") },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(books, key = { it.id }) { book ->
                BookRow(book = book, onClick = { onBookClick(book.id) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun BookRow(book: HadithBookEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = book.bookNumber.toString(),
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(text = book.nameEnglish, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Hadiths: ${book.hadithCount}", style = MaterialTheme.typography.bodySmall)
        }
        Text(text = book.nameArabic, style = MaterialTheme.typography.titleMedium)
    }
}