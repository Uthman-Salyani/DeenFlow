package com.uthman.deenflow.ui.tasbih

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

@Composable
fun TasbihScreen(viewModel: TasbihViewModel = viewModel()) {
    val count by viewModel.count.collectAsState()
    val incrementStep by viewModel.incrementStep.collectAsState()
    val goal by viewModel.goal.collectAsState()

    var isMinimalist by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showStepMenu by remember { mutableStateOf(false) }
    val goalReachedEvent by viewModel.goalReachedEvent.collectAsState()
    var showGoalReachedBanner by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(goalReachedEvent) {
        if (goalReachedEvent) {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(VibratorManager::class.java)
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            }
            vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))

            showGoalReachedBanner = true
            delay(2000)
            showGoalReachedBanner = false
            viewModel.consumeGoalReachedEvent()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        if (!isMinimalist) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { showGoalDialog = true }) {
                    Text(if (goal != null) "Goal: $goal" else "Set Goal")
                }

                Box {
                    TextButton(onClick = { showStepMenu = true }) {
                        Text("+$incrementStep")
                    }
                    DropdownMenu(expanded = showStepMenu, onDismissRequest = { showStepMenu = false }) {
                        listOf(1, 2, 3, 5, 10, 33, 100).forEach { step ->
                            DropdownMenuItem(
                                text = { Text("+$step") },
                                onClick = {
                                    viewModel.setIncrementStep(step)
                                    showStepMenu = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit count")
                }

                TextButton(onClick = { viewModel.reset() }) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "COUNT", style = MaterialTheme.typography.labelLarge)
            Text(text = count.toString(), style = MaterialTheme.typography.displayLarge)
            if (goal != null) {
                Text(text = "Goal: $goal", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary), CircleShape)
                    .clickable { viewModel.increment() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", style = MaterialTheme.typography.displayMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Tap the button to count", style = MaterialTheme.typography.bodySmall)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.decrement() }) {
                Icon(Icons.Default.Remove, contentDescription = "Undo one")
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { isMinimalist = !isMinimalist }) {
                Icon(
                    imageVector = if (isMinimalist) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                    contentDescription = "Toggle minimalist view"
                )
            }
        }
    }

    AnimatedVisibility(
        visible = showGoalReachedBanner,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card {
                Text(
                    text = "Goal reached! 🎉",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showGoalDialog) {
        var goalText by remember { mutableStateOf(goal?.toString() ?: "") }
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Set Goal") },
            text = {
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { input -> goalText = input.filter { it.isDigit() } },
                    label = { Text("Target count") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setGoal(goalText.toIntOrNull())
                    showGoalDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.setGoal(null)
                    showGoalDialog = false
                }) { Text("Clear") }
            }
        )
    }

    if (showEditDialog) {
        var editText by remember { mutableStateOf(count.toString()) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Count") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { input -> editText = input.filter { it.isDigit() } },
                    label = { Text("Count") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setCountDirectly(editText.toIntOrNull() ?: count)
                    showEditDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }
}