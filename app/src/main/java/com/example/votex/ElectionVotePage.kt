package com.example.votex

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

// Fungsi untuk mengacak gambar dari sumber daya lokal
fun getRandomDrawable(): Int {
    val imageList = listOf(
        R.drawable.image_20,
        R.drawable.image_21,
        R.drawable.image_22,
        R.drawable.image_24,
        R.drawable.image_25,
        R.drawable.image_26,
    )
    return imageList.random()
}

fun getDrawableResId(photoName: String?): Int {
    return when (photoName) {
        "image_1" -> R.drawable.image_20
        "image_2" -> R.drawable.image_21
        "image_3" -> R.drawable.image_22
        "image_4" -> R.drawable.image_24
        "image_5" -> R.drawable.image_25
        "image_6" -> R.drawable.image_26
        else -> getRandomDrawable()
    }
}

fun parseStringToList(input: String): List<String> {
    return input.split(",")
        .map { it.trim() } // Memangkas ruang kosong di sekitar setiap elemen
        .filter { it.isNotEmpty() } // Mengabaikan elemen kosong jika ada
}

// Halaman utama dengan Firebase dan opsional gambar
@Composable
fun ElectionVotePage(navController: NavController, unicId: String) {
    Log.d("tes berapa kali NGIRIM", "ini alertdialog '$unicId'")
    var optionsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle

    val combinedCredential = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("combinedCredential") ?: ""

    Log.d("ElectionVotePage", "combinedCredential diakses: $combinedCredential")


    var selectedCandidateIndex by remember { mutableStateOf<Int?>(null) }

    if (!LocalInspectionMode.current) {
        auth = Firebase.auth
        database = Firebase.database
    }

    fun handleVoting(index: Int, unicId: String, combinedCredential: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("VotingError", "User not authenticated")
            return
        }

        val uid = currentUser.uid
        val voteReference = Firebase.database.getReference("votes/$unicId")
        val userReference = Firebase.database.getReference("users/$uid")

        // 1. Periksa apakah user sudah pernah memberikan suara
        userReference.child("hasBeenVotedAt").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists() && snapshot.hasChild(unicId)) {
                Log.e("VotingError", "User sudah pernah memberikan suara pada voting ini")
                return@addOnSuccessListener
            }

            // 2. Tambahkan UID user sebagai voter ke dalam opsi yang dipilih
            val selectedOptionPath = "options/option${index + 1}/voters"
            val voterReference = voteReference.child(selectedOptionPath).child(uid)

            voterReference.setValue(true).addOnSuccessListener {
                userReference.child("hasBeenVotedAt").child(unicId).setValue(unicId).addOnSuccessListener {
                    voteReference.child("credential/voterCredential").child(uid).setValue(combinedCredential)
                        .addOnSuccessListener {
                            val totalVotesReference = voteReference.child("totalVotes")
                            totalVotesReference.get().addOnSuccessListener { totalSnapshot ->
                                val currentTotalVotes = totalSnapshot.getValue(Long::class.java) ?: 0L
                                totalVotesReference.setValue(currentTotalVotes + 1).addOnSuccessListener {
                                    Log.d("VoteSubmission", "Voting successful")
                                    navController.navigate("result/{$unicId}")
                                }
                            }
                        }
                }
            }
        }
    }

    LaunchedEffect(unicId) {
        try {
            val snapshot = database.reference.child("votes").child(unicId).get().await()
            val optionsData = snapshot.child("options").value as? Map<String, Map<String, Any>> ?: emptyMap()
            Log.d("ElectionVotePage", "Raw options data: $optionsData")

            optionsList = optionsData.entries.map { entry ->
                val optionDetails = entry.value

                mapOf(
                    "name" to (optionDetails["candidateFullName${entry.key.last()}"] as? String ?: "Kandidat"),
                    "motto" to (optionDetails["motto${entry.key.last()}"] as? String ?: "Motto"),
                    "teamAlias" to (optionDetails["teamAlias${entry.key.last()}"] as? String ?: "Tim"),
                    "photo" to ((optionDetails["selectedPhoto${entry.key.last()}"] as? String)?.takeIf { it.isNotEmpty() } ?: "default_photo_url")
                )
            }

            Log.d("ElectionVotePage", "Processed optionsList: $optionsList")
            isLoading = false
        } catch (e: Exception) {
            Log.e("ElectionVotePage", "Error fetching data", e)
            errorMessage = "Gagal memuat data: ${e.localizedMessage}"
            isLoading = false
        }
    }

    val database = Firebase.database.reference
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("Memuat...") }

    LaunchedEffect(unicId) {
        coroutineScope.launch {
            try {
                // Ambil data dari Firebase
                val snapshot = database.child("votes").child(unicId).get().await()
                title = snapshot.child("title").getValue(String::class.java) ?: "Gagal memuat title"
            } catch (e: Exception) {
                title = "Kesalahan saat memuat"
                Log.e("ElectionVotePage", "Error fetching title", e)
            }
        }
    }

    /**
     * Fungsi aman untuk memisahkan string berdasarkan koma
     * Menghapus whitespace yang tidak perlu dari setiap bagian.
     */




    // Tampilan utama dengan penanganan loading dan error
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))
    ) {
        when {

            isLoading -> {
                Log.d("tes loading ", "ini alertdialog '$unicId'")
                // Tampilan loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Memuat data...",
                        color = Color(0xFF008753)
                    )
                }
            }
            errorMessage != null -> {
                // Tampilan error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Terjadi kesalahan",
                        color = Color.Red
                    )
                }
            }
            optionsList.isEmpty() -> {
                // Tampilan ketika data kosong
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada kandidat ditemukan",
                        color = Color(0xFF008753)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF0F0F0)),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Log.d("tes masuk ", "ini alertdialog '$unicId'")
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
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 20.dp).align(Alignment.TopStart)
                        )
                    }

                    itemsIndexed(optionsList) { index, option ->
                        Log.d("tes id", "tes dari id mana yang dikirm '$unicId'")

                        val name = option["name"]
                        val motto = option["motto"]
                        val teamAlias = option["teamAlias"]
                        val photoUrl = option["photo"]
                        val photoResId = getDrawableResId(photoUrl.toString())

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFFFFFFF))
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF008753),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Text(
                                text = "Kandidat ${index + 1}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF008753),
                                modifier = Modifier.padding(20.dp),
                                fontSize = 20.sp
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = photoResId),
                                    contentDescription = "Gambar Kandidat",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .align(Alignment.Center)
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Text(
                                text = "Nama Tim Kandidat : ",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Text(
                                text = "$teamAlias",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Anggota Kandidat : ",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Text(
                                text = "$name",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Motto Kandidat : ",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Text(
                                text = "$motto",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        selectedCandidateIndex = index
                                        savedStateHandle?.set("selected_candidate_index", index)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text(text = "Pilih", color = Color.White)
                                }
                                if (selectedCandidateIndex == index) {
                                    AlertDialog(
                                        onDismissRequest = {
                                            selectedCandidateIndex = null
                                            savedStateHandle?.remove<String>("selected_candidate_index")
                                        },
                                        title = { Text(text = "Konfirmasi Pilihan Anda!") },
                                        text = {
                                            Text(
                                                text = "Konfirmasi pilihan anda sebelum mengirimkan voting. Jika anda telah yakin dengan pilihan anda, tekan tombol \"Kirim\"."
                                            )
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = {
                                                    if (combinedCredential.isEmpty()) {
                                                        Log.e("VotingError", "combinedCredential kosong")
                                                        return@Button
                                                    }

                                                    if (selectedCandidateIndex != null) {
                                                        handleVoting(
                                                            selectedCandidateIndex!!,
                                                            unicId,
                                                            combinedCredential
                                                        )
                                                        selectedCandidateIndex = null
                                                        savedStateHandle?.remove<Int>("selected_candidate_index")
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                                            ) {
                                                Text("Kirim", color = Color.White)
                                            }
                                        },
                                        dismissButton = {
                                            Button(
                                                onClick = {
                                                    selectedCandidateIndex = null
                                                    savedStateHandle?.remove<String>("selected_candidate_index")
                                                },
                                                colors = ButtonDefaults.buttonColors(Color(0xFFFF0000))
                                            ) {
                                                Text("Batal", color = Color.White)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

