package com.example.votex

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.votex.R
import com.example.votex.ui.theme.VoteXTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var auth: FirebaseAuth

@Preview
@Composable
fun RegisterPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF008753)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_53),
            contentDescription = "Logo",
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Learn Graphic and UI/UX designing in Hindi \n " + "for free live project",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(50.dp)),
            value = "",
            onValueChange = {})
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(50.dp)),
            value = "",
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {  }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(50.dp)),
            value = "",
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { }
        )
        Button(
            colors = ButtonDefaults.buttonColors(Color(0xFFFF9800)),
            onClick = {},
        ) {
            Text(text = "REGISTER")
        }
        Text(
            modifier = Modifier.padding(10.dp),
            text = " Forgot Password?",
            color = Color.White,
            textAlign = TextAlign.Right
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            Text(
                text = "Don't have an account? ",
                color = Color.White,
                fontWeight = FontWeight.Light,
                style = TextStyle.Default
            )
            Text(
                modifier = Modifier.clickable { },
                text = "Register now",
                color = Color.White,
                fontWeight = FontWeight.Light,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Text(
                text = "By signing up, you are agree with our ",
                color = Color.White,
                fontWeight = FontWeight.Light,
                style = TextStyle.Default
            )
            Text(
                modifier = Modifier.clickable { },
                text = "Terms & Conditions",
                color = Color.White,
                fontWeight = FontWeight.Light,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }
    }
}