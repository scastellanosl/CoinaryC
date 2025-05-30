package com.example.coinary


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home" // La pantalla inicial dentro de MainScreen
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                onLogout = {
                    // lógica de logout: puede ser navegar hacia login, cerrar sesión, etc.
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }

        composable("stats") {
                StatsScreen(navController = navController)
        }
        composable("movement") {
            AddMovementScreen(navController = navController)
        }
        composable("reminder") {
            ReminderScreen(navController = navController)
        }
    }
}
