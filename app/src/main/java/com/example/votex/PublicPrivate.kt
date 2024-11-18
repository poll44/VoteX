package com.example.votex

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicPrivatePage(navController: NavController) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var voteTitle by remember { mutableStateOf("") }
    var voteDescription by remember { mutableStateOf("") }
    var voteType by remember { mutableStateOf(VoteType.Public) }
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    var endDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    var endHour by remember { mutableStateOf("") }
    var endMinute by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(mutableListOf("Pilihan 1", "Pilihan 2", "Pilihan 3")) }
    var votePin by remember { mutableStateOf("") } // State for Vote PIN

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
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF008753),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFF008753),
                                focusedLabelColor = Color(0xFF008753),
                                unfocusedLabelColor = Color.Gray
                            ),
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
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF008753),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFF008753),
                                focusedLabelColor = Color(0xFF008753),
                                unfocusedLabelColor = Color.Gray
                            ),
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
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF008753),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFF008753),
                                focusedLabelColor = Color(0xFF008753),
                                unfocusedLabelColor = Color.Gray
                            ),
                            value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDateTime.time),
                            onValueChange = { /* handle change */ },
                            label = { Text("Tanggal Berakhir (DD/MM/YYYY)") },
                            leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                            placeholder = { Text("25/11/2022") }
                        )
                    }

                    // End time
                    Row(modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF008753),
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color(0xFF008753),
                                focusedLabelColor = Color(0xFF008753),
                                unfocusedLabelColor = Color.Gray
                            ),
                            value = endHour,
                            onValueChange = { endHour = it },
                            label = { Text("Waktu Berakhir (HH:MM)") },
                            placeholder = { Text("e.g., 14:00") }
                        )
                        IconButton(onClick = { /* handle time picker */ }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Pilih Waktu")
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
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF008753),
                                    unfocusedBorderColor = Color.Gray,
                                    cursorColor = Color(0xFF008753),
                                    focusedLabelColor = Color(0xFF008753),
                                    unfocusedLabelColor = Color.Gray
                                ),
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

                    if (voteType == VoteType.Private) {
                        Row(modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF008753),
                                    unfocusedBorderColor = Color.Gray,
                                    cursorColor = Color(0xFF008753),
                                    focusedLabelColor = Color(0xFF008753),
                                    unfocusedLabelColor = Color.Gray
                                ),
                                value = votePin,
                                onValueChange = { votePin = it },
                                label = { Text("Vote PIN") },
                                placeholder = { Text("Masukkan Vote PIN") }
                            )
                        }

                    }

                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                        Button(onClick = {
                            isDialogOpen = true
                        },  modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                            shape = RoundedCornerShape(50)) {
                            Text("Buat")
                        }
                    }
                    if (isDialogOpen) {
                        AlertDialog(
                            onDismissRequest = { isDialogOpen = false },
                            title = {
                                Text(text = if (voteType == VoteType.Private) "Kirim ke Privat" else "Kirim ke Publik")
                            },
                            text = {
                                Text(text = if (voteType == VoteType.Private)
                                    "Apakah Anda yakin ingin menyimpan dan mengirim vote ini secara privat?"
                                else
                                    "Apakah Anda yakin ingin menyimpan dan mengirim vote ini ke publik?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Voting Telah Dibuat!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("Home")
                                        isDialogOpen = false
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF27AE60))
                                ) {
                                    Text("Kirim")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { isDialogOpen = false },
                                    colors = ButtonDefaults.buttonColors(Color.Red)
                                ) {
                                    Text("Batal")
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}

enum class VoteType {
    Public, Private
}