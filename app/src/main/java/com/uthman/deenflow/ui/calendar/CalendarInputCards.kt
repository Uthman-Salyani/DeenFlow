package com.uthman.deenflow.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
@Composable
private fun PlainNumberField(
    value: String,
    placeholder: String,
    maxLength: Int,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { c -> c.isDigit() }.take(maxLength)) },
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
        modifier = modifier,
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun CardHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun SetDateCard(onSubmit: (day: Int, month: Int, year: Int) -> Unit) {
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            CardHeader(Icons.Default.CalendarMonth, "Set Hijri Date", "Manually set the Hijri date")

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlainNumberField(day, "DD", 2, Modifier.weight(1f)) { day = it }
                PlainNumberField(month, "MM", 2, Modifier.weight(1f)) { month = it }
                PlainNumberField(year, "YYYY", 4, Modifier.weight(1.4f)) { year = it }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val d = day.toIntOrNull()
                    val m = month.toIntOrNull()
                    val y = year.toIntOrNull()
                    if (d != null && m != null && y != null && d in 1..30 && m in 1..12) {
                        onSubmit(d, m, y)
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun SetSunsetTimeCard(currentSunset: Pair<Int, Int>?, onSubmit: (hour: Int, minute: Int) -> Unit) {
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var isPm by remember { mutableStateOf(false) }
    var showAmPmMenu by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            CardHeader(
                Icons.Default.WbTwilight,
                "Daily Update Time (Sunset)",
                "The date will automatically update at this time each day"
            )

            if (currentSunset != null) {
                Spacer(modifier = Modifier.height(4.dp))
                val h24 = currentSunset.first
                val displayHour = when {
                    h24 == 0 -> 12
                    h24 > 12 -> h24 - 12
                    else -> h24
                }
                val amPmLabel = if (h24 >= 12) "PM" else "AM"
                Text(
                    text = "Currently: %d:%02d %s".format(displayHour, currentSunset.second, amPmLabel),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlainNumberField(hour, "HH", 2, Modifier.width(60.dp)) { hour = it }
                Text(text = ":", style = MaterialTheme.typography.headlineSmall)
                PlainNumberField(minute, "MM", 2, Modifier.width(60.dp)) { minute = it }

                Spacer(modifier = Modifier.width(16.dp))

                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showAmPmMenu = true }
                    ) {
                        Text(text = if (isPm) "PM" else "AM", style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = showAmPmMenu, onDismissRequest = { showAmPmMenu = false }) {
                        DropdownMenuItem(text = { Text("AM") }, onClick = { isPm = false; showAmPmMenu = false })
                        DropdownMenuItem(text = { Text("PM") }, onClick = { isPm = true; showAmPmMenu = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val h12 = hour.toIntOrNull()
                    val m = minute.toIntOrNull()
                    if (h12 != null && m != null && h12 in 1..12 && m in 0..59) {
                        val h24 = when {
                            !isPm && h12 == 12 -> 0
                            isPm && h12 != 12 -> h12 + 12
                            else -> h12
                        }
                        onSubmit(h24, m)
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Submit")
            }
        }
    }
}