package com.example.votex

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.votex.ui.theme.VoteXTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoteXTheme {
                SplashScreen()
            }
        }
        getActionBar()?.hide()
    }
}

@Composable
fun SplashScreen() {
    val alpha = remember { Animatable(0f) }
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        alpha.animateTo(2f, animationSpec = tween(durationMillis = 2000))
        delay(1000)
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        (context as? ComponentActivity)?.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF008753)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.ic_main_background),
            contentDescription = "Background",
            alignment = Alignment.Center
        )

        Image(
            modifier = Modifier
                .size(150.dp)
                .alpha(alpha.value),
            painter = painterResource(id = R.drawable.group_53),
            contentDescription = "Logo",
            alignment = Alignment.Center,
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    VoteXTheme {
        SplashScreen()
    }
}
