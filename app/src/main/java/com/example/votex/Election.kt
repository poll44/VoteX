package com.example.votex

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
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
fun ElectionPage(navController: NavController) {
    var selectedDate by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    val mContext = LocalContext.current
    var judul: String = ""
    var deskripsi: String = ""

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.navigate("credential") },
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "back Icon",
                            tint = Color(0xFF008753),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.group_166),
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            Text(text = "Detail Pemilu", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFFFFFFF))
            ) {
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        text = "Header",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 20.dp, vertical = 5.dp),
                        value = judul,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF008753),
                            unfocusedBorderColor = Color(0xFF008753),
                            cursorColor = Color(0xFF008753),
                        ),
                        onValueChange = { newText -> judul = newText }
                    )
                }
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        text = "Deskripsi",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 20.dp, vertical = 5.dp),
                        value = deskripsi,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF008753),
                            unfocusedBorderColor = Color(0xFF008753),
                            cursorColor = Color(0xFF008753),
                        ),
                        onValueChange = { newText -> deskripsi = newText }
                    )
                }
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(text = "End date (DD/MM/YYYY)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        placeholder = { Text("DD/MM/YYYY") },
                        enabled = false,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF008753),
                            unfocusedBorderColor = Color(0xFF008753),
                            unfocusedPlaceholderColor = Color(0xFF008753),
                            cursorColor = Color(0xFF008753),
                        ),
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date"
                                )
                            }
                        }
                    )
                }

                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(text = "End time (0-24 format)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = hour,
                            onValueChange = {
                                if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                                    val hourValue = it.toIntOrNull() ?: 0
                                    if (hourValue in 0..23) hour = it
                                }
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                            placeholder = { Text("hour") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF008753),
                                unfocusedBorderColor = Color(0xFF008753),
                                unfocusedPlaceholderColor = Color(0xFF008753),
                                cursorColor = Color(0xFF008753),
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        Text(":")
                        OutlinedTextField(
                            value = minute,
                            onValueChange = {
                                if (it.length <= 2 && it.all { char -> char.isDigit() }) {
                                    val minuteValue = it.toIntOrNull() ?: 0
                                    if (minuteValue in 0..59) minute = it
                                }
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                            placeholder = { Text("minutes") },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF008753),
                                unfocusedBorderColor = Color(0xFF008753),
                                unfocusedPlaceholderColor = Color(0xFF008753),
                                cursorColor = Color(0xFF008753),
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                var isContentVisible by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { isContentVisible = !isContentVisible },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isContentVisible) "Hide Candidate Form" else "Show Candidate Form")
                    }

                    // Content Area
                    AnimatedVisibility(visible = isContentVisible) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Team Alias TextField
                            var teamAlias by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = teamAlias,
                                onValueChange = { teamAlias = it },
                                label = { Text("Candidate team alias") },
                                placeholder = { Text("example: Amin, Pragib, GAMA") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Full Name TextField
                            var fullName by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Candidate full name") },
                                placeholder = { Text("for candidate more than one use comma to separate them") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Motto TextField
                            var motto by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = motto,
                                onValueChange = { motto = it },
                                label = { Text("Candidate motto") },
                                placeholder = { Text("example: Semuanya untuk rakyat") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Photo Picker
                            var selectedPhoto by remember { mutableStateOf("") }
                            OutlinedButton(
                                onClick = { /* TODO: Implement photo picker logic */ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (selectedPhoto.isEmpty()) "Choose Photo" else selectedPhoto)
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(
                            mContext,
                            "Voting pemilu telah berhasil dibuat",
                            Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                                  },
                        modifier = Modifier.align(Alignment.BottomEnd),
                        colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(text = "Lanjut", color = Color.White)
                    }
                }
            }
        }
    }
}