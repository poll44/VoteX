package com.example.votex

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun HomePage(navController: NavController, modifier: Modifier = Modifier) {
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
    Spacer(modifier = Modifier.height(10.dp))
    Column (
        modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF))
    ){
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFFFFFF))
        ) {
            Row (
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ){
                Column (modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hello!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF008753)
                    )
                    Text(
                        text = "Hi,$userEmail"
                    )
                }
                Image(painter = painterResource(R.drawable.group_54), contentDescription = null)
            }
        }
    }
}
