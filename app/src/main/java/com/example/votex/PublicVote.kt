package com.example.votex

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database


private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@Composable
fun PublicVotePage(navController: NavController) {
    var email: String = ""
    var userEmail: String = ""
    var password: String = ""
    var userId: String = ""
    if (!LocalInspectionMode.current) {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            email = currentUser.email.toString()
            userEmail = email.substringBefore("@")
            password = currentUser.providerId.toString()

        }
        database = Firebase.database
    }

    Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(15.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Judul Vote",
                modifier = Modifier
                    .padding(8.dp),
                color = Color(0xFF008753),
                fontSize = 18.sp
            )

            Image(
                painter = painterResource(R.drawable.group_166),
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFFFFFFF))
    ) {

    }
}
