package com.uthman.deenflow.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.uthman.deenflow.ui.home.HomeScreen
import com.uthman.deenflow.ui.quran.QuranScreen
import com.uthman.deenflow.ui.hadith.HadithScreen
import com.uthman.deenflow.ui.tasbih.TasbihScreen
import com.uthman.deenflow.ui.calendar.CalendarScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.uthman.deenflow.ui.quran.AyahReaderScreen
import com.uthman.deenflow.ui.hadith.HadithReaderScreen

@Composable
fun DeenFlowApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Quran.route) {
                QuranScreen(
                    onSurahClick = { surahNumber ->
                        navController.navigate("ayah_reader/$surahNumber")
                    }
                )
            }
            composable(Screen.Hadith.route) {
                HadithScreen(
                    onBookClick = { bookId ->
                        navController.navigate("hadith_reader/$bookId")
                    }
                )
            }
            composable(Screen.Tasbih.route) { TasbihScreen() }
            composable(Screen.Calendar.route) { CalendarScreen() }

            composable(
                route = "ayah_reader/{surahNumber}",
                arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
            ) { backStackEntry ->
                val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                AyahReaderScreen(surahNumber = surahNumber)
            }

            composable(
                route = "hadith_reader/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
                HadithReaderScreen(bookId = bookId)
            }
        }
    }
}