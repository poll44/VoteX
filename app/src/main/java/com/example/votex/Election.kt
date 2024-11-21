package com.example.votex

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
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
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
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
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(java.util.Calendar.HOUR_OF_DAY),
        calendar.get(java.util.Calendar.MINUTE),
        true
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhoto = uri.toString()
        }
    }
    val mContext = LocalContext.current
    var judul: String = ""
    var deskripsi: String = ""

    if (!LocalInspectionMode.current){
        auth = Firebase.auth
        database = Firebase.database
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
        ) {
            item {
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
            }

            item {
                Text(
                    text = "Detail Pemilu",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Column(
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

                    Text(
                        text = "Deskripsi",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
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
                            onValueChange = { newText -> deskripsi = newText })

                    Text(
                        text = "End date (DD/MM/YYYY)",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
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

                    Text(
                        text = "Waktu Berakhir (Format 24 Jam)",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                    OutlinedTextField(
                        value = selectedTime,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        placeholder = { Text("HH:MM") },
                        enabled = false,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF008753),
                            unfocusedBorderColor = Color(0xFF008753),
                            unfocusedPlaceholderColor = Color(0xFF008753),
                            cursorColor = Color(0xFF008753),
                            ),
                        trailingIcon = {
                            IconButton(onClick = { timePickerDialog.show() }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Select Time"
                                )
                            }
                        }
                    )

                    var isContentVisible1 by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Candidate 1", fontWeight = FontWeight.Bold)
                            IconButton( onClick = {isContentVisible1 = !isContentVisible1}) {
                                Icon(
                                    imageVector = if (isContentVisible1) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Favorite"
                                )
                            }
                        }

                        AnimatedVisibility(visible = isContentVisible1) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))

                                var teamAlias by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = teamAlias,
                                    onValueChange = { teamAlias = it },
                                    label = { Text("Candidate team alias") },
                                    placeholder = { Text("example: Amin, Pragib, GAMA") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                var fullName by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text("Candidate full name") },
                                    placeholder = { Text("for candidate more than one use comma to separate them") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                var motto by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = motto,
                                    onValueChange = { motto = it },
                                    label = { Text("Candidate motto") },
                                    placeholder = { Text("example: Semuanya untuk rakyat") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            launcher.launch("image/*")
                                        },
                                        colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text("Pilih Foto")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedPhoto ?: "Tidak ada file yang dipilih",
                                        modifier = Modifier.padding(start = 8.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    var isContentVisible2 by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Candidate 2", fontWeight = FontWeight.Bold)
                            IconButton( onClick = {isContentVisible2 = !isContentVisible2}) {
                                Icon(
                                    imageVector = if (isContentVisible2) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Favorite"
                                )
                            }
                        }

                        AnimatedVisibility(visible = isContentVisible2) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))

                                var teamAlias by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = teamAlias,
                                    onValueChange = { teamAlias = it },
                                    label = { Text("Candidate team alias") },
                                    placeholder = { Text("example: Amin, Pragib, GAMA") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                var fullName by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = fullName,
                                    onValueChange = { fullName = it },
                                    label = { Text("Candidate full name") },
                                    placeholder = { Text("for candidate more than one use comma to separate them") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                var motto by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = motto,
                                    onValueChange = { motto = it },
                                    label = { Text("Candidate motto") },
                                    placeholder = { Text("example: Semuanya untuk rakyat") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            launcher.launch("image/*")
                                        },
                                        colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text("Pilih Foto")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedPhoto ?: "Tidak ada file yang dipilih",
                                        modifier = Modifier.padding(start = 8.dp),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
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
                                    Toast.LENGTH_SHORT
                                ).show()
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
}