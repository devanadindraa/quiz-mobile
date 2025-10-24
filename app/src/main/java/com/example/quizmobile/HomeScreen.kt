package com.example.quizmobile

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.quizmobile.data.FirebaseRepository
import com.example.quizmobile.ui.components.TaskList

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, repo: FirebaseRepository) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aplikasi Tugas") },
                actions = {
                    TextButton(onClick = {
                        repo.logout()
                        navController.navigate("login") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }) {
                        Text("Logout", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) {
        TaskList(repo = repo)
    }
}