package com.example.votex

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@Composable
fun ProfilePage(navController: NavController, modifier: Modifier = Modifier) {
    var userEmail: String = ""
    var userId: String = ""
    val mContext = LocalContext.current

    if (!LocalInspectionMode.current){
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email.toString()
            userEmail = email.substringBefore("@")
        }
        database = Firebase.database
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
        ) {

        }
        /**
         * Bottom Navigation Bar
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFFFFFF))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White)
                    .clip(RoundedCornerShape(15.dp))
            ) {
                IconButton(
                    onClick = {navController.navigate("home")},
                ) {
                    Image(
                        painter = painterResource(R.drawable.vector),
                        contentDescription = null
                    )
                }
                Divider(
                    color = Color.White,
                    modifier = Modifier
                        .height(16.dp)
                        .width(2.dp)
                )
                IconButton(
                    onClick = {navController.navigate("search")},
                ) {
                    Image(
                        painter = painterResource(R.drawable.group_51),
                        contentDescription = null
                    )
                }
                Divider(
                    color = Color.White,
                    modifier = Modifier
                        .height(16.dp)
                        .width(2.dp)
                )
                IconButton(
                    onClick = {},
                ) {
                    Image(
                        painter = painterResource(R.drawable.user_square),
                        contentDescription = null
                    )
                }
            }
        }
    }
}