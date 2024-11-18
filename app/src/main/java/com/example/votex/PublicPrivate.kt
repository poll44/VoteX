package com.example.votex

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase
private lateinit var userId: String

@Composable
fun PublicPrivatePage(navController: NavController) {
    var voteTitle by remember { mutableStateOf("") }
    var voteDescription by remember { mutableStateOf("") }
    var voteType by remember { mutableStateOf(VoteType.Public) }
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    var endDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    var endHour by remember { mutableStateOf("") }
    var endMinute by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(mutableListOf("Pilihan 1", "Pilihan 2", "Pilihan 3")) }

    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF0F0F0))
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)),
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp).clip(RoundedCornerShape(15.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.navigate("home") },
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

                Text(text = "Lakukan voting Anda sendiri", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            }

            item {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFFFFFFF))
                ) {
                    // Type of voting
                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = voteType == VoteType.Public,
                            onClick = { voteType = VoteType.Public }
                        )
                        Text(text = "Publik")
                        RadioButton(
                            selected = voteType == VoteType.Private,
                            onClick = { voteType = VoteType.Private }
                        )
                        Text(text = "Privat")
                    }
                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        // Header
                        OutlinedTextField(
                            value = voteTitle,
                            onValueChange = { voteTitle = it },
                            label = { Text("Judul") },
                            placeholder = { Text("contoh: Makanan Favorit") }
                        )
                    }

                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        // Description
                        OutlinedTextField(
                            value = voteDescription,
                            onValueChange = { voteDescription = it },
                            label = { Text("Deskripsi") },
                            placeholder = { Text("contoh: Ini merupakan...") }
                        )
                    }

                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        // Add Photo
                        Button(onClick = { /* handle photo selection */ },
                            colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                            shape = RoundedCornerShape(50)) {
                            Text("Pilih Foto")
                        }
                        Text(selectedPhoto ?: "Tidak ada file yang dipilih")
                    }

                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        // End date
                        OutlinedTextField(
                            value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDateTime.time),
                            onValueChange = { /* handle change */ },
                            label = { Text("Tanggal Berakhir (DD/MM/YYYY)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                            placeholder = { Text("25/11/2022") }
                        )
                    }

                    // End time

                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = endHour,
                            onValueChange = { endHour = it },
                            label = { Text("Waktu Berakhir (HH:MM)") },
                            placeholder = { Text("e.g., 14:00") }
                        )
                        IconButton(onClick = { /* handle time picker */ }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pilih Waktu")
                        }
                    }

                    Text(
                        text = "Pilihan Vote",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(10.dp)
                    )
                    Column (modifier = Modifier .padding(10.dp)) {
                        options.forEachIndexed { index, option ->
                            OutlinedTextField(
                                value = option,
                                onValueChange = { options[index] = it },
                                label = { Text("Pilihan") },
                                placeholder = { Text("Masukkan Pilihan") }
                            )
                        }
                    }

                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { options.add("Tambah Pilihan") },
                            colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                            shape = RoundedCornerShape(50)) {
                            Text("Tambah Pilihan")
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                        Button(onClick = {
                            // Handle form submission
                            Toast.makeText(context, "Voting Telah Dibuat!", Toast.LENGTH_SHORT).show()
                        },  modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                            shape = RoundedCornerShape(50)) {
                            Text("Buat")
                        }
                    }
                }
            }
        }
    }
}

enum class VoteType {
    Public, Private
}