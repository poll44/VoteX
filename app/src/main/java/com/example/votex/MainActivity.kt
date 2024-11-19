package com.example.votex

import RegisterPage
import android.os.Build
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

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(android.view.WindowInsets.Type.navigationBars())
                it.hide(android.view.WindowInsets.Type.statusBars())
                it.systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) "election" else "register"

        hideSystemUI()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") { LoginPage(navController = navController) }
                composable("register") { RegisterPage(navController = navController) }
                composable("home") { HomePage(navController = navController) }
                composable("participated") { ParticipatedPage(navController = navController) }
                composable("search") { SearchPage(navController = navController) }
                composable("profile") { ProfilePage(navController = navController) }
                composable("editProfile") { EditProfilePage(navController = navController)}
                composable("credential") { CredentialPage(navController = navController)}
                composable("election") { ElectionPage(navController = navController)}
                composable("publicprivate") { PublicPrivatePage(navController = navController)}
            }
        }
        getActionBar()?.hide()
    }
}
