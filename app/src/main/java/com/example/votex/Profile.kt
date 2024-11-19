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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(navController: NavController) {
    val email: String = ""
    val userEmail: String = ""

    if (!LocalInspectionMode.current){
        auth = Firebase.auth
        database = Firebase.database
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
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
            Text(text = "Your Profile", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFFFFFFF))
            ) {
                Box(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.pngwing_com),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = CircleShape)
                    )
                }
                Text(
                    text = "Nama",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp),
                    value = userEmail,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )
                Text(
                    text = "Email",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp),
                    value = email,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )
                Text(
                    text = "Password",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp),
                    value = "12345678",
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )
                Text(
                    text = "Tanggal lahir",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp),
                    value = "12/12/2024",
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )
                Text(
                    text = "Kota/Wilayah",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp, vertical = 5.dp),
                    value = "Malang",
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )

                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Button(
                        onClick = { navController.navigate("editProfile") },
                        modifier = Modifier.align(Alignment.BottomEnd),
                        colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(text = "Ubah", color = Color.White)
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