package com.uthman.deenflow.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uthman.deenflow.data.calendar.HijriMonths
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


@Composable
fun CalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val currentDate by viewModel.currentDate.collectAsState()
    val needsConfirmation by viewModel.needsConfirmation.collectAsState()
    val sunsetTime by viewModel.sunsetTime.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        val context = androidx.compose.ui.platform.LocalContext.current

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { /* granted or denied — either way, nothing more to do here right now */ }

        LaunchedEffect(Unit) {
            com.uthman.deenflow.notifications.NotificationHelper.createChannel(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    if (currentDate != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = HijriMonths.arabic(currentDate!!.month),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = HijriMonths.toArabicIndicNumeral(currentDate!!.day),
                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Today's Date is:",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (currentDate == null) {
                    Text(
                        text = "No date set yet",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val gregorianDate = SimpleDateFormat("MMM dd\nyyyy", Locale.US).format(Date())
                        Text(text = gregorianDate, textAlign = androidx.compose.ui.text.style.TextAlign.Center)

                        VerticalDivider(modifier = Modifier.height(40.dp))

                        Text(text = "${HijriMonths.english(currentDate!!.month)} ${currentDate!!.day}")

                        VerticalDivider(modifier = Modifier.height(40.dp))

                        Text(text = "${currentDate!!.year} A.H")
                    }

                    if (sunsetTime == null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Set a sunset time below to enable daily auto-update",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                if (needsConfirmation) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "It's the 29th — please observe the moon and confirm:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { viewModel.confirmDay30() }) { Text("Confirm 30th") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { viewModel.confirmNewMonth() }) { Text("New Month") }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SetDateCard(onSubmit = { d, m, y -> viewModel.setManualDate(d, m, y) })

        Spacer(modifier = Modifier.height(16.dp))

        SetSunsetTimeCard(
            currentSunset = sunsetTime,
            onSubmit = { h, min -> viewModel.setSunsetTime(h, min) }
        )
    }
}