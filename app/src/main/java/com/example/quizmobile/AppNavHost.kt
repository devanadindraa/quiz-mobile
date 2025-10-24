package com.example.quizmobile

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quizmobile.auth.LoginScreen
import com.example.quizmobile.auth.SignupScreen
import com.example.quizmobile.data.FirebaseRepository

@Composable
fun AppNavHost(navController: NavHostController, repo: FirebaseRepository) {
    val startDestination = if (repo.currentUser() != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController, repo)
        }
        composable("signup") {
            SignupScreen(navController, repo)
        }
        composable("home") {
            HomeScreen(navController, repo)
        }
    }
}
