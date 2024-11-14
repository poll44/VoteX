package com.example.votex

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun SearchPage(navController: NavController) {
    var text by remember { mutableStateOf("") }
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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFFFFFFF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello!",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF008753)
                        )
                        Text(
                            text = "Hi, $userEmail"
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.group_166),
                        contentDescription = null
                    )
                }
            }
            Text(text = "Search", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = text,
                    onValueChange = { newtext -> text = newtext },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    placeholder = { Text("Cari judul atau id") },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedIndicatorColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .height(55.dp)
                )

                Button(
                    onClick = {  },
                    shape = CircleShape,
                    modifier = Modifier
                        .height(50.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                ) {
                    Text(text = "Cari", fontSize = 16.sp, color = Color.White)
                }
            }
            Text(text = "Recommendation", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            /**
             * list yang dibuat
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row {
                        Image(
                            painter = painterResource(R.drawable.pngwing_com),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = CircleShape)
                        )
                        Spacer(modifier = Modifier
                            .width(8.dp)
                            .height(8.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "title", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "id = 1234", color = Color.Gray)
                        }
                        Text(text = "PRIVAT", fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                    Row {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "tersisa 5 menit", color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "1000 votes", color = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.width(90.dp))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(text = "PIN : 1234", color = Color.Gray)
                            Button(
                                onClick = { /* Handle Hasil button click */ },
                                colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(text = "Hasil", color = Color.White)
                            }
                        }
                    }
                }
            }
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
                    onClick = {},
                ) {
                    Image(
                        painter = painterResource(R.drawable.group_55),
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
                    onClick = {navController.navigate("profile")},
                ) {
                    Image(
                        painter = painterResource(R.drawable.group_138),
                        contentDescription = null
                    )
                }
            }
        }
    }
}