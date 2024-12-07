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
fun ElectionCreateVotePage(navController: NavController) {
    // State for tracking election details
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    // State for candidates
    var candidates by remember {
        mutableStateOf(
            listOf(
                CandidateState(),
                CandidateState()
            )
        )
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Firebase authentication and database
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    // Check if not in inspection mode
    if (!LocalInspectionMode.current) {
        auth = Firebase.auth
        database = Firebase.database
    }

    // Date Picker Dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time Picker Dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(java.util.Calendar.HOUR_OF_DAY),
        calendar.get(java.util.Calendar.MINUTE),
        true
    )

    // Image picker launcher
    val imageLaunchers = (0 until 10).map { index ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                candidates = candidates.mapIndexed { candidateIndex, candidate ->
                    if (candidateIndex == index) {
                        candidate.copy(selectedPhoto = uri.toString())
                    } else candidate
                }
            }
        }
    }

    // Retrieve credentials from previous screen
    val credentials = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Array<VoteElection.Credential.CredentialType>>("election_credentials")
        ?: emptyArray()

    // Function to generate a unique 7-digit ID
    fun generateUniqueId(): String {
        return (100000..9999999).random().toString()
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
                    // Election Title
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
                            onValueChange = { judul = it }
                        )
                    }

                    // Description
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
                        onValueChange = { deskripsi = it }
                    )

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

                    candidates.forEachIndexed { index, candidate ->
                        var isContentVisible by remember { mutableStateOf(index < 2) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Candidate ${index + 1}",
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { isContentVisible = !isContentVisible }
                                ) {
                                    Icon(
                                        imageVector = if (isContentVisible)
                                            Icons.Filled.KeyboardArrowUp
                                        else
                                            Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "Toggle Candidate Details"
                                    )
                                }
                            }

                            AnimatedVisibility(visible = isContentVisible) {
                                Column {
                                    // Team Alias
                                    OutlinedTextField(
                                        value = candidate.teamAlias,
                                        onValueChange = {
                                            candidates = candidates.mapIndexed { candidateIndex, candidateState ->
                                                if (candidateIndex == index) {
                                                    candidateState.copy(teamAlias = it)
                                                } else candidateState
                                            }
                                        },
                                        label = { Text("Candidate team alias") },
                                        placeholder = { Text("example: Amin, Pragib, GAMA") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Full Name
                                    OutlinedTextField(
                                        value = candidate.candidateFullName,
                                        onValueChange = {
                                            candidates = candidates.mapIndexed { candidateIndex, candidateState ->
                                                if (candidateIndex == index) {
                                                    candidateState.copy(candidateFullName = it)
                                                } else candidateState
                                            }
                                        },
                                        label = { Text("Candidate full name") },
                                        placeholder = { Text("for candidate more than one use comma to separate them") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Motto
                                    OutlinedTextField(
                                        value = candidate.motto,
                                        onValueChange = {
                                            candidates = candidates.mapIndexed { candidateIndex, candidateState ->
                                                if (candidateIndex == index) {
                                                    candidateState.copy(motto = it)
                                                } else candidateState
                                            }
                                        },
                                        label = { Text("Candidate motto") },
                                        placeholder = { Text("example: Semuanya untuk rakyat") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Photo Selection
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { imageLaunchers[index].launch("image/*") },
                                            colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text("Pilih Foto")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = candidate.selectedPhoto ?: "Tidak ada file yang dipilih",
                                            modifier = Modifier.padding(start = 8.dp),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Add Candidate Button
                    if (candidates.size < 10) {
                        Button(
                            onClick = {
                                if (candidates.size < 10) {
                                    candidates = candidates + CandidateState()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text("Tambah Kandidat")
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                        Button(
                            onClick = {
                                // Validate inputs
                                if (judul.isBlank() || deskripsi.isBlank() ||
                                    selectedDate.isBlank() || selectedTime.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Harap lengkapi informasi dasar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                // Validate candidates
                                val validCandidates = candidates.filter {
                                    it.teamAlias.isNotBlank() &&
                                            it.candidateFullName.isNotBlank()
                                }

                                if (validCandidates.size < 2) {
                                    Toast.makeText(
                                        context,
                                        "Minimal 2 kandidat harus diisi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                // Get current authenticated user
                                val currentUser = auth.currentUser
                                if (currentUser == null) {
                                    Toast.makeText(
                                        context,
                                        "Pengguna tidak terautentikasi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                // Generate unique vote document ID
                                val uniqueId = generateUniqueId()
                                val userVotesRef = database.reference
                                    .child("users")
                                    .child(currentUser.uid)
                                    .child("voteCreated")
                                userVotesRef.child(uniqueId).setValue(true)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Voting pemilu berhasil dibuat dan ditambahkan ke profil pengguna",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("created")
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Voting dibuat, tetapi gagal ditambahkan ke profil pengguna: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                // Prepare vote data structure
                                val optionsMap = validCandidates.mapIndexed { index, candidate ->
                                    "option${index + 1}" to hashMapOf(
                                        "teamAlias${index + 1}" to candidate.teamAlias,
                                        "candidateFullName${index + 1}" to candidate.candidateFullName,
                                        "motto${index + 1}" to candidate.motto,
                                        "selectedPhoto${index + 1}" to (candidate.selectedPhoto ?: ""),
                                        "voter${index + 1}" to emptyList<String>()
                                    )
                                }.toMap()

                                val voteData = hashMapOf(
                                    "createdBy" to currentUser.uid,
                                    "endDate" to selectedDate,
                                    "endTime" to selectedTime,
                                    "title" to judul,
                                    "description" to deskripsi,
                                    "type" to "Election",
                                    "unicID" to uniqueId,
                                    "voteClosed" to false,
                                    "options" to optionsMap,
                                    "credential" to hashMapOf(
                                        "voterCredentialType" to credentials.mapIndexed { index, credential ->
                                            hashMapOf(
                                                "nameTagType" to credential.nameTagType,
                                                "maxInput" to credential.maxInput,
                                                "typeTag" to credential.typeTag,
                                                "isMandatory" to credential.isMandatory
                                            )
                                        },
                                        "voterCredential" to emptyList<Map<String, Any>>()
                                    )
                                )

                                // Save to Firebase Realtime Database
                                val votesRef = database.reference.child("votes").child(uniqueId)
                                votesRef.setValue(voteData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Voting pemilu telah berhasil dibuat",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("created")
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Gagal membuat voting: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
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

data class CandidateState(
    val teamAlias: String = "",
    val candidateFullName: String = "",
    val motto: String = "",
    val selectedPhoto: String? = null
)