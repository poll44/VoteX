package com.example.votex

import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.util.*

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicPrivateCreateVotePage(navController: NavController) {
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = android.icu.util.Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        },
        calendar.get(android.icu.util.Calendar.YEAR),
        calendar.get(android.icu.util.Calendar.MONTH),
        calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)
    )
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhoto = uri.toString()
        }
    }

    var isDialogOpen by remember { mutableStateOf(false) }
    var voteTitle by remember { mutableStateOf("") }
    var voteDescription by remember { mutableStateOf("") }
    var voteType by remember { mutableStateOf(VoteType.Public) }
    var options by remember { mutableStateOf(listOf("", "", "")) }
    var votePin by remember { mutableStateOf("") }
    var isOptionOpen by remember { mutableStateOf(false) }

    // Inisialisasi auth dan database dengan LaunchedEffect
    LaunchedEffect(Unit) {
        auth = Firebase.auth
        database = Firebase.database
    }

    // Fungsi untuk menyimpan vote
    fun saveVote() {
        val currentUser = auth.currentUser
        if (!::auth.isInitialized) {
            Toast.makeText(context, "Firebase Auth belum diinisialisasi.", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUser == null) {
            Toast.makeText(context, "Anda belum masuk. Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }

        if (voteTitle.isBlank() || voteDescription.isBlank()) {
            Toast.makeText(context, "Mohon lengkapi judul dan deskripsi vote!", Toast.LENGTH_SHORT).show()
            return
        }

        val filledOptions = options.filter { it.isNotBlank() }
        if (filledOptions.size < 2) {
            Toast.makeText(context, "Minimal 2 pilihan harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        // Jika tipe vote adalah Public, pastikan votePin kosong
        if (voteType == VoteType.Public && votePin.isNotEmpty()) {
            Toast.makeText(context, "Vote publik tidak membutuhkan PIN!", Toast.LENGTH_SHORT).show()
            return
        }

        val createdBy = currentUser.uid
        val unicID = (10000..99999).random().toString()
        val voteRef = database.getReference("votes")

        // Cek apakah unicID sudah ada
        voteRef.child(unicID).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(context, "Gagal menyimpan vote, coba lagi.", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // Membuat opsi dengan key dinamis berdasarkan index dan nilai pilihan
            val optionsMap = filledOptions.mapIndexed { index, option ->
                val optionKey = "option${index + 1}" // Membuat key seperti "Option1", "Option2", dst.
                optionKey to OptionDetail(
                    pilihan = option,
                    voters = mutableListOf() // Daftar UID pengguna yang memilih opsi ini
                )
            }.toMap()

            val newVote = Vote(
                selectedPhoto = selectedPhoto ?: "",
                endDate = selectedDate,
                endTime = selectedTime,
                title = voteTitle,
                description = voteDescription,
                type = voteType.name,
                options = optionsMap, // Menyimpan opsi yang sudah terisi
                pin = if (voteType == VoteType.Private) votePin else "",
                voteClosed = false,
                unicID = unicID,
                createdBy = createdBy // UID pengguna yang membuat voting
            )

            // Simpan vote baru ke Firebase
            voteRef.child(unicID).setValue(newVote).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Vote berhasil disimpan!", Toast.LENGTH_SHORT).show()

                    // Tambahkan unicID ke tabel users/{uid}/voteCreated
                    val userVotesRef = database.getReference("users")
                        .child(currentUser.uid)
                        .child("voteCreated")
                    userVotesRef.child(unicID).setValue(true).addOnCompleteListener { userTask ->
                        if (userTask.isSuccessful) {
                            Toast.makeText(context, "Vote ditambahkan ke profil pengguna!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Vote tersimpan, tetapi gagal diperbarui di profil pengguna: ${userTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Gagal menyimpan vote: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
                            onClick = { navController.navigate("created") },
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
                Text(text = "Buatlah voting Anda sendiri", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            }

            item {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFFFFFFF))
                ) {
                    Row(modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {

                        RadioButton(
                            selected = voteType == VoteType.Public,
                            onClick = { voteType = VoteType.Public },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF008753),
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Publik",
                            color = if (voteType == VoteType.Public) Color(0xFF008753) else Color.Gray
                        )

                        RadioButton(
                            selected = voteType == VoteType.Private,
                            onClick = { voteType = VoteType.Private },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFFD32F2F),
                                unselectedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Privat",
                            color = if (voteType == VoteType.Private) Color(0xFFD32F2F) else Color.Gray
                        )
                    }

                    Row(modifier = Modifier .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
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
                            placeholder = { Text("contoh: Makanan Favorit") },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

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
                            value = voteDescription,
                            onValueChange = { voteDescription = it },
                            label = { Text("Deskripsi") },
                            placeholder = { Text("contoh: Ini merupakan...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                launcher.launch("image/*")
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
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

                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        Text(text = "Tanggal Berakhir (DD/MM/YYYY)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                        OutlinedTextField(
                            value = selectedDate,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
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
                        Text(text = "Waktu Berakhir (Format 24 Jam)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                        OutlinedTextField(
                            value = selectedTime,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
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
                    }


                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Pilihan Vote",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))  // Memberikan jarak antar elemen

                        options.forEachIndexed { index, option ->
                            OutlinedTextField(
                                value = option,
                                onValueChange = { newValue ->
                                    options = options.toMutableList().apply {
                                        this[index] = newValue
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                label = { Text("Pilihan") },
                                placeholder = { Text("Masukkan Pilihan") },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFF008753),
                                    unfocusedBorderColor = Color.Gray,
                                    cursorColor = Color(0xFF008753),
                                    focusedLabelColor = Color(0xFF008753),
                                    unfocusedLabelColor = Color.Gray
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))  // Memberikan jarak sebelum tombol

                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        if (options.size < 6) {
                                            options = options.toMutableList().apply {
                                                add("")
                                            }
                                        } else {
                                            Toast.makeText(context, "Maksimal 6 pilihan saja!", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("TambahPilihan", "Error saat menambahkan pilihan: ${e.message}")
                                        e.printStackTrace()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Tambah Pilihan")
                            }
                        }
                    }

                    if(isOptionOpen) {
                        AlertDialog(
                            onDismissRequest = { isOptionOpen = false },
                            confirmButton = {
                                TextButton(onClick = { isOptionOpen = false }) {
                                    Text("OK")
                                }
                            },
                            title = {
                                Text("TAMBAH BERHASIL")
                            },
                            text = {
                                Text("Opsi baru telah ditambahkan.")
                            }
                        )
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
                            colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
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
                                        saveVote() // Menyimpan vote
                                        Toast.makeText(context, "Voting Telah Dibuat!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("created")
                                        isDialogOpen = false

                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF008753))
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