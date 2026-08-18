package com.uthman.deenflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.uthman.deenflow.data.local.seed.DatabaseSeeder
import com.uthman.deenflow.navigation.DeenFlowApp
import com.uthman.deenflow.ui.theme.DeenFlowTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            DatabaseSeeder.seedIfNeeded(applicationContext)
        }

        setContent {
            DeenFlowTheme {
                DeenFlowApp()
            }
        }
    }
}