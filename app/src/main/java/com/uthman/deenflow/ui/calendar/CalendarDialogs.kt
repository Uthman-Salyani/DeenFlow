package com.uthman.deenflow.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SetDateDialog(
    onDismiss: () -> Unit,
    onConfirm: (day: Int, month: Int, year: Int) -> Unit
) {
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Hijri Date") },
        text = {
            Column {
                OutlinedTextField(
                    value = day,
                    onValueChange = { day = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Day (1-30)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Month (1-12)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it.filter { c -> c.isDigit() }.take(4) },
                    label = { Text("Year (A.H)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val d = day.toIntOrNull()
                val m = month.toIntOrNull()
                val y = year.toIntOrNull()
                if (d != null && m != null && y != null && d in 1..30 && m in 1..12) {
                    onConfirm(d, m, y)
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SetSunsetTimeDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Update Time") },
        text = {
            Column {
                OutlinedTextField(
                    value = hour,
                    onValueChange = { hour = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Hour (0-23)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = minute,
                    onValueChange = { minute = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Minute (0-59)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val h = hour.toIntOrNull()
                val min = minute.toIntOrNull()
                if (h != null && min != null && h in 0..23 && min in 0..59) {
                    onConfirm(h, min)
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}