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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.votex.R
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth

@Composable
fun LoginPage(navController: NavController, modifier: Modifier = Modifier) {
    val mContext = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Fungsi untuk login menggunakan Firebase
    fun signin(email: String, password: String) {
        auth = FirebaseAuth.getInstance() // Inisialisasi FirebaseAuth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser?.email
                    Toast.makeText(
                        mContext,
                        "Logged in as $user",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate("home")
                } else {
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        mContext,
                        "Authentication failed: ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    Image(
        painter = painterResource(id = R.drawable.ic_main_background),
        contentDescription = "Logo",
        modifier = Modifier.fillMaxSize()
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_53),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )

        Text(
            text = "Fast, easy and secure digital\nvoting platform",
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            value = email,
            label = { Text("Alamat Email") },
            onValueChange = { email = it }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            value = password,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { password = it }
        )

        Button(
            modifier = Modifier
                .size(width = 450.dp, height = 60.dp)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signin(email, password)
                } else {
                    Toast.makeText(
                        mContext, "Please fill all the fields above", Toast.LENGTH_LONG
                    ).show()
                }
            },
        ) {
            Text(text = "MASUK")
        }

        Text(
            modifier = Modifier.padding(10.dp),
            text = "Forgot Password?",
            color = Color.White,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(210.dp))

        Row {
            Text(
                text = "Belum punya akun? ",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                style = TextStyle.Default
            )
            Text(
                modifier = Modifier.clickable { navController.navigate("register") },
                text = "Daftar sekarang!",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }

        Button(
            onClick = { /* Add Google sign-in logic here */ },
            modifier = Modifier
                .width(250.dp)
                .height(90.dp)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pngwing_com),
                    contentDescription = "Google logo",
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log in with Google")
            }
        }
    }
}