package com.example.votex

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var stringEmail: String = ""
    var uidAuth: String = ""
    var userName by remember { mutableStateOf("") }
    var userBirthDate by remember { mutableStateOf("") }
    var userPlace by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (!LocalInspectionMode.current) {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            uidAuth = currentUser.uid
            stringEmail = currentUser.email ?: ""
        }
        database = Firebase.database

        // Referensi ke Realtime Database
        val userRef = database.getReference("users").child(uidAuth)

        // Ambil data dari Realtime Database
        userRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val fetchedUser = snapshot.getValue(User::class.java)
                    fetchedUser?.let {
                        userName = it.name
                        userBirthDate = it.birthDate
                        userPlace = it.place
                        password = it.password

                        // Log hasil untuk debugging
                        Log.d("User Data", "Name: $userName, BirthDate: $userBirthDate, Place: $userPlace, Password: $password")
                    }
                } else {
                    Log.w("User Data", "No data found for UID: $uidAuth")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("User Data", "Error getting data", exception)
            }
    }
    fun logout() {
        auth.signOut() // Keluar dari akun Firebase
        navController.navigate("login") // Arahkan kembali ke halaman login
    }
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))) {
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
                    .border((0.5).dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
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
                            text = "Hi, ${userName ?: "User"}"
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.group_166),
                        contentDescription = null
                    )
                }
            }
            Text(text = "Your Profile", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFFFFFFF))
                    .border((0.5).dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
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

                // Nama
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
                    value = userName,
                    enabled = false, // Membuat field tidak bisa diubah
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )

                // Email
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
                    value = stringEmail,
                    enabled = false, // Membuat field tidak bisa diubah
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )

                // Password
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
                    value = password, // Sesuaikan dengan tanggal lahir yang sebenarnya
                    enabled = false,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )

                // Tanggal lahir
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
                    value = "$userBirthDate", // Sesuaikan dengan tanggal lahir yang sebenarnya
                    enabled = false, // Membuat field tidak bisa diubah
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )

                // Kota/Wilayah
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
                    value = "$userPlace", // Sesuaikan dengan kota atau wilayah yang sebenarnya
                    enabled = false, // Membuat field tidak bisa diubah
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF008753),
                        unfocusedBorderColor = Color(0xFF008753),
                        cursorColor = Color(0xFF008753),
                    ),
                    onValueChange = { }
                )

                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 5.dp)) {
                    Button(
                        onClick = { logout() },
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .padding(end = 100.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F)),
                    ) {
                        Text("Logout", color = Color.White)
                    }
                    Button(
                        onClick = { navController.navigate("editProfile") },
                        modifier = Modifier.align(Alignment.BottomEnd),
                        colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(text = "Ubah", color = Color.White)
                    }

                }
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFFFFFF))
                .border((0.5).dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
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
                    onClick = {navController.navigate("created")},
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