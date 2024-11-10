package com.example.votex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.votex.ui.theme.VoteXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoteXTheme {
                Test()
            }
        }
    }
}

@Composable
fun Test() {
    Image(
        painter = painterResource(id = R.drawable.convomfs_on_twitter),
        contentDescription = null,
        modifier = Modifier.size(500.dp),
        alignment = Alignment.Center
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VoteXTheme {
        Test()
    }
}