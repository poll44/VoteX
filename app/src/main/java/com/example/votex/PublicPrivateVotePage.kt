package com.example.votex

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun PublicPrivateVotePage(navController: NavController, unicId: String?) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf<String>()) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Tambahkan variabel untuk menyimpan current user
    val currentUser = Firebase.auth.currentUser

    if (!LocalInspectionMode.current && unicId != null) {
        val auth = Firebase.auth
        val database = Firebase.database

        // Ambil data dari Firebase menggunakan unicId
        val voteReference = database.getReference("votes/$unicId")
        val userReference = database.getReference("users")

        LaunchedEffect(unicId) {
            voteReference.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Ambil judul dan deskripsi
                    title = snapshot.child("title").getValue(String::class.java) ?: "Tidak ada judul"
                    description = snapshot.child("description").getValue(String::class.java) ?: ""

                    // Ambil opsi-opsi
                    val optionsList = mutableListOf<String>()
                    snapshot.child("options").children.forEach { optionSnapshot ->
                        val option = optionSnapshot.child("pilihan").getValue(String::class.java)
                        if (!option.isNullOrEmpty()) {
                            optionsList.add(option)
                        }
                    }
                    options = optionsList
                }
            }.addOnFailureListener {
                Log.e("FirebaseError", "Gagal mengambil data: ${it.message}")
            }
        }
    }

    // Fungsi untuk mengirim vote
    fun submitVote() {
        if (currentUser == null || selectedOption.isEmpty() || unicId == null) {
            // Handle error - user belum login, belum memilih opsi, atau unicId null
            return
        }

        val uid = currentUser.uid
        val voteReference = Firebase.database.getReference("votes/$unicId")
        val userReference = Firebase.database.getReference("users/$uid")

        // 1. Cek apakah user sudah pernah memberikan suara
        userReference.child("hasBeenVotedAt").get().addOnSuccessListener { snapshot ->
            // Cek apakah unicId sudah ada dalam hasBeenVotedAt
            if (snapshot.exists() && snapshot.hasChild(unicId!!)) {
                // User sudah memberikan suara pada voting ini
                return@addOnSuccessListener
            }

            // 2. Cari index opsi yang dipilih
            val selectedOptionIndex = options.indexOf(selectedOption)

            // 3. Tambahkan UID user ke voters pada opsi yang dipilih
            val votersReference = voteReference.child("options/option${selectedOptionIndex + 1}/voters")
            votersReference.child(uid).setValue(true)
                .addOnSuccessListener {
                    // 4. Tambahkan unicId vote ke dalam hasBeenVotedAt milik user
                    val userVoteDataReference = userReference.child("hasBeenVotedAt")
                    userVoteDataReference.child(unicId!!).setValue(unicId)  // Menyimpan unicId sebagai identifikasi vote
                        .addOnSuccessListener {
                            // 5. Update totalVotes di node vote
                            val totalVotesReference = voteReference.child("totalVotes")
                            totalVotesReference.get().addOnSuccessListener { totalSnapshot ->
                                val currentTotalVotes = totalSnapshot.getValue(Int::class.java) ?: 0
                                totalVotesReference.setValue(currentTotalVotes + 1) // Increment totalVotes
                            }.addOnFailureListener { e ->
                                Log.e("VoteSubmission", "Gagal mengambil totalVotes: ${e.message}")
                            }

                            // Navigasi ke halaman hasil
                            navController.navigate("result/$unicId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("VoteSubmission", "Gagal menambahkan vote ke user: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("VoteSubmission", "Gagal menambahkan voter: ${e.message}")
                }
        }.addOnFailureListener { e ->
            Log.e("VoteSubmission", "Gagal memeriksa status voting: ${e.message}")
        }
    }


    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
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
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(15.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.navigate("search") },
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
                Text(
                    text = title, // Tampilkan judul yang diambil dari Firebase
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp).align(Alignment.TopStart)
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFFFFFFF))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 20.dp, end = 20.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.image_23),
                            contentDescription = "Gambar Bubur",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description, // Tampilkan deskripsi yang diambil dari Firebase
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            items(options) { option ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = {
                                selectedOption = option
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, fontSize = 16.sp)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(15.dp))

                if (isDialogOpen) {
                    AlertDialog(
                        onDismissRequest = { isDialogOpen = false },
                        title = { Text(text = "Konfirmasi Pilihan Anda!") },
                        text = {
                            Text(
                                text = "Konfirmasi pilihan anda sebelum mengirimkan voting. Jika anda telah yakin dengan pilihan anda, tekan tombol 'Kirim'. Anda tidak dapat mengubah pilihan anda setelah mengirimkan voting."
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    submitVote()
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                            ) {
                                Text("Kirim", color = Color.White)
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { isDialogOpen = false },
                                colors = ButtonDefaults.buttonColors(Color(0xFFFF0000))
                            ) {
                                Text("Batal", color = Color.White)
                            }
                        }
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { isDialogOpen = true },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(text = "Next", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    options.forEach { option ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (option == selectedOption),
                onClick = { onOptionSelected(option) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = option, fontSize = 16.sp)
        }
    }
}


