package com.example.coinary.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// package com.example.coinary.view (asegúrate de que el paquete sea correcto)

// ... otras importaciones

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreen.route
    ) {
        composable(Routes.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.LoginScreen.route) {
                        popUpTo(Routes.HomeScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LoginScreen.route) {
            GoogleLoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HomeScreen.route) {
                        popUpTo(Routes.LoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    // Puedes manejar el registro si lo necesitas
                }
            )
        }

        composable(Routes.StatsScreen.route) {
            StatsScreen(navController = navController)
        }
        composable(Routes.MovementScreen.route) {
            AddMovementScreen(navController = navController)
        }
        composable(Routes.ReminderScreen.route) {
            ReminderScreen(navController = navController)
        }
        composable(Routes.NotificationsScreen.route){
            NotificationsScreen(navController = navController)
        }
        composable(Routes.ProfileScreen.route) {
            ProfileScreen(
                navController = navController,
                onLogout = { // Cierre de sesión
                    navController.navigate(Routes.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.RealtimeTransactionsScreen.route) {
            RealtimeTransactionsScreen(navController = navController)
        }
        composable(Routes.FreelanceIncomeListScreen.route) {
            FreelanceIncomeListScreen(navController = navController)
        }
        composable(Routes.ExpenseListScreen.route) {
            ExpenseListScreen(navController = navController)
        }
        composable(Routes.ReportScreen.route) { // <-- ¡NUEVA RUTA!
            ReportScreen(navController = navController)
        }
    }
}

// ... (resto de tu clase Routes) ...

sealed class Routes(val route: String) {
    object LoginScreen : Routes("login")
    object HomeScreen : Routes("home")
    object StatsScreen : Routes("stats")
    object MovementScreen : Routes("movement")
    object ReminderScreen : Routes("reminder")
    object NotificationsScreen : Routes("notifications")
    object ProfileScreen : Routes("profile")
    object RealtimeTransactionsScreen : Routes("realtime_transactions")
    object FreelanceIncomeListScreen : Routes("freelance_income_list")
    object ExpenseListScreen : Routes("expense_list")
    object ReportScreen : Routes("report_screen") // <-- ¡NUEVA RUTA!
}