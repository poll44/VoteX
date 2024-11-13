package com.example.votex

import RegisterPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) "home" else "login"

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") { LoginPage(navController = navController) }
                composable("register") { RegisterPage(navController = navController) }
                composable("home") { HomePage(navController = navController) }
            }
        }
    }
}
