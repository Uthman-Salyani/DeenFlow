package com.uthman.deenflow.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CalendarMonth

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Quran : Screen("quran", "Quran", Icons.AutoMirrored.Filled.MenuBook)
    object Hadith : Screen("hadith", "Hadith", Icons.Default.Book)
    object Tasbih : Screen("tasbih", "Tasbih", Icons.Default.Circle)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
}

val bottomNavItems = listOf(
    Screen.Home, Screen.Quran, Screen.Hadith, Screen.Tasbih, Screen.Calendar
)