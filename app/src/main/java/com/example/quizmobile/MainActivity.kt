package com.example.quizmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.quizmobile.data.FirebaseRepository
import com.example.quizmobile.ui.theme.QuizMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repo = FirebaseRepository()

        setContent {
            QuizMobileTheme {
                val navController = rememberNavController()
                AppNavHost(navController, repo)
            }
        }
    }
}
