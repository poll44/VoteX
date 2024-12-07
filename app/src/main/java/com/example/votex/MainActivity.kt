package com.example.votex

import RegisterPage
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) "created" else "register"
        hideSystemUI()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") { LoginPage(navController = navController) }
                composable("register") { RegisterPage(navController = navController) }
                composable("created") { CreatedHomePage(navController = navController) }
                composable("participated") { ParticipatedHomePage(navController = navController) }
                composable("search") { SearchPage(navController = navController) }
                composable("profile") { ProfilePage(navController = navController) }
                composable("editProfile") { EditProfilePage(navController = navController) }
                composable("credential") { ElectionSetupCredentialPage(navController = navController) }
                composable("election") { ElectionCreateVotePage(navController = navController) }
                composable("publicPrivate") { PublicPrivateCreateVotePage(navController = navController) }
                composable("vote/{unicId}") { backStackEntry ->
                    val unicId = backStackEntry.arguments?.getString("unicId")
                    PublicPrivateVotePage(navController = navController, unicId = unicId)
                }
                composable("Result/{unicId}") { backStackEntry ->
                    val unicId = backStackEntry.arguments?.getString("unicId")
                    ResultPage(navController = navController, unicId = unicId)
                }
                composable("electionVote/{unicId}") { backStackEntry ->
                    Log.d("ElectionVotePage", "backStackEntry arguments: ${backStackEntry.arguments}")
                    val unicId = backStackEntry.arguments?.getString("unicId")
                    if (unicId != null) {
                        Log.d("ElectionVotePage", "unicId berhasil ditemukan: $unicId")
                        ElectionVotePage(navController = navController, unicId = unicId)
                    } else {
                        Log.e("ElectionVotePage", "unicId kosong!")
                    }
                }

            }
        }
        getActionBar()?.hide()
    }
}
