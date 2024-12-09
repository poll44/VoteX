package com.example.votex

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var isSearchAppear by remember { mutableStateOf(false) }
    var activeEmail by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            birthDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )

    var isChecked by remember { mutableStateOf(false) }
    var kredensial by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val mContext = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser != null) {
        LaunchedEffect(key1 = currentUser.uid) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(currentUser.uid)
            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val fetchedUser = snapshot.getValue(User::class.java)
                    fetchedUser?.let {
                        userName = it.name ?: "User"
                        birthDate = it.birthDate ?: ""
                        place = it.place ?: ""
                        activeEmail = it.email ?: ""
                    }
                }
            }
        }
    }

    var voteList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

// Ambil data votes dari Firebase Realtime Database
    val database = FirebaseDatabase.getInstance()
    val voteRef = database.getReference("votes")
    var selectedVote by remember { mutableStateOf<Map<String, Any>?>(null) }
    var pinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }

    voteRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val votes = snapshot.children.mapNotNull { childSnapshot ->
                    val voteMap = childSnapshot.getValue() as? Map<String, Any>
                    voteMap?.let { vote ->
                        mapOf(
                            "title" to (vote["title"] as? String ?: "Tidak Ada Judul"),
                            "unicID" to (vote["unicID"] as? String ?: "Tidak Ada ID"),
                            "type" to (vote["type"] as? String ?: "Unknown"),
                            "endDate" to (vote["endDate"] as? String ?: "01/01/2000"),
                            "endTime" to (vote["endTime"] as? String ?: "00:00"),
                            "totalVotes" to ((vote["totalVotes"] as? Long)?.toInt() ?: 0),
                            "pin" to (vote["pin"] as? String ?: ""),
                            "createdBy" to (vote["createdBy"] as? String ?: "")
                        )
                    }
                }
                voteList = votes
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("VoteDebug", "Failed to read value: ${error.message}")
        }
    })

// Misalnya referensi ke node kredensial
    val credentials = remember { mutableStateOf(listOf<Map<String, Any>>()) }

// Mengambil data dari Firebase
    LaunchedEffect(key1 = currentUser) {
        if (currentUser != null) {
            val voteRef = FirebaseDatabase.getInstance().getReference("votes")

            voteRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val parsedCredentials = mutableListOf<Map<String, Any>>()

                    snapshot.children.forEach { voteSnapshot ->
                        // Memeriksa bagian kredensial dalam setiap vote
                        val credentialsSnapshot =
                            voteSnapshot.child("credential/voterCredentialType")

                        credentialsSnapshot.children.forEach { credentialSnapshot ->
                            val credentialMap = mutableMapOf<String, Any>()

                            // Parsing data untuk setiap kredensial
                            val nameTagType =
                                credentialSnapshot.child("nameTagType").getValue(String::class.java)
                                    ?: ""
                            val typeTag =
                                credentialSnapshot.child("typeTag").getValue(String::class.java)
                                    ?: ""
                            val isMandatory = credentialSnapshot.child("isMandatory")
                                .getValue(Boolean::class.java) ?: false
                            val maxInput =
                                credentialSnapshot.child("maxInput").getValue(Long::class.java)
                                    ?: 0L

                            // Menambahkan data kredensial ke dalam map
                            credentialMap["nameTagType"] = nameTagType
                            credentialMap["typeTag"] = typeTag
                            credentialMap["isMandatory"] = isMandatory
                            credentialMap["maxInput"] = maxInput

                            // Menambahkan kredensial yang diparse ke dalam list
                            parsedCredentials.add(credentialMap)
                        }
                    }

                    // Menyimpan kredensial yang telah diparse ke dalam state
                    credentials.value = parsedCredentials

                    // Log untuk debugging
                    Log.d("KredensialDebug", "Kredensial berhasil diparse: $parsedCredentials")
                } else {
                    Log.d("KredensialDebug", "Data vote tidak ditemukan")
                }
            }.addOnFailureListener {
                Log.d("KredensialDebug", "Gagal mengambil data votes: ${it.message}")
            }
        }
    }

    var filteredVotes by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Tombol Cari untuk memicu filter
    fun filterVotes() {
        if (text.isEmpty()) {
            filteredVotes = emptyList();
            isSearchAppear = false// Jika input kosong, kosongkan daftar hasil
            return
        }

        filteredVotes = voteList.filter { vote ->
            val title = vote["title"] as? String ?: ""
            val unicId = vote["unicID"] as? String ?: ""
            title.contains(text, ignoreCase = true) || unicId.contains(text, ignoreCase = true)
        }.take(5) // Ambil hanya 5 hasil paling sesuai
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)),
            contentPadding = PaddingValues(bottom = 100.dp)  // Tambahkan padding bawah yang lebih besar
        ) {
            item {
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
                                text = "Hi, $userName"
                            )
                        }
                        Image(
                            painter = painterResource(R.drawable.group_166),
                            contentDescription = null
                        )
                    }
                }
            }
            item {
                Text(
                    text = "Search",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = text,
                        onValueChange = { newText -> text = newText },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        },
                        placeholder = { Text("Cari judul atau id") },
                        singleLine = true,
                        shape = RoundedCornerShape(15.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .height(55.dp)
                            .border((0.5).dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
                    )

                    Button(
                        onClick = { isSearchAppear = true; filterVotes()},
                        shape = CircleShape,
                        modifier = Modifier
                            .height(50.dp)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                    ) {
                        Text(text = "Cari", fontSize = 16.sp, color = Color.White)
                    }
                }

            }
            item {
                if (isSearchAppear == true){
                    Text(
                        text = "Searched Item",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
            }
            items(filteredVotes) { vote ->
                val title = vote["title"] as? String ?: "Tidak Ada Judul"
                val unicId = vote["unicID"] as? String ?: "Tidak Ada ID"
                val voteType = vote["type"] as? String ?: "Unknown"
                val endDate = vote["endDate"] as? String ?: "01/01/2000"
                val endTime = vote["endTime"] as? String ?: "00:00"
                val totalVotes = vote["totalVotes"] as? Int ?: 0
                Log.d("VoteDebug", "Displaying Total Votes Integer: $totalVotes")
                val createdBy = vote["createdBy"] as? String ?: ""
                val pin = vote["pin"] as? String ?: ""
                val isCreator = currentUser?.uid == createdBy

                // Menentukan label dan warna berdasarkan voteType
                val (typeLabel, typeColor) = when (voteType) {
                    "Private" -> "PRIVAT" to Color(0xFFD32F2F)
                    "Public" -> "PUBLIK" to Color(0xFF008753)
                    "Election" -> "PEMILU" to Color(0xFFFF9500)
                    else -> "Unknown" to Color.Gray
                }

                val remainingTime = run {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val endDateTimeString = "$endDate $endTime"
                    val endDateTime = try {
                        sdf.parse(endDateTimeString)
                    } catch (e: Exception) {
                        null
                    }

                    if (endDateTime != null) {
                        val currentTime = Date()
                        val diff = endDateTime.time - currentTime.time
                        when {
                            diff < 60 * 1000 -> "Kurang dari 1 menit" // Kurang dari 1 menit
                            diff < 24 * 60 * 60 * 1000 -> { // Kurang dari 1 hari
                                val hours = diff / (60 * 60 * 1000)
                                "Tersisa $hours jam"
                            }

                            diff < 30L * 24 * 60 * 60 * 1000 -> { // Kurang dari 1 bulan
                                val days = diff / (24 * 60 * 60 * 1000)
                                "Tersisa $days hari"
                            }

                            else -> "Tidak diketahui"
                        }
                    } else {
                        "Format waktu salah"
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row {
                            Image(
                                painter = painterResource(R.drawable.pngwing_com),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        Color.Gray.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "ID: $unicId", color = Color.Gray)
                            }
                            Text(
                                text = typeLabel, // Menampilkan label tipe
                                fontWeight = FontWeight.ExtraBold,
                                color = typeColor, // Menampilkan warna sesuai tipe
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }

                        Row {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = remainingTime, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "$totalVotes votes", color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.width(90.dp))

                            // Tombol Vote atau Hasil
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                var currentDialogUnicId by remember { mutableStateOf<String?>(null) } // Untuk melacak dialog yang aktif
                                var hasVoted = remember { mutableStateOf(false) }

                                // Cek apakah pengguna sudah memberikan suara
                                val userReference =
                                    Firebase.database.getReference("users/${currentUser?.uid}")
                                userReference.child("hasBeenVotedAt").get()
                                    .addOnSuccessListener { snapshot ->
                                        if (snapshot.exists() && snapshot.hasChild(unicId)) {
                                            // Jika sudah memberikan suara
                                            hasVoted.value = true
                                        }
                                    }

                                Button(
                                    onClick = {
                                        when {
                                            isCreator -> {
                                                navController.navigate("result/$unicId")
                                            }

                                            hasVoted.value -> {
                                                // Jika sudah memberikan suara, tampilkan hasil
                                                navController.navigate("result/$unicId")
                                            }

                                            voteType == "Private" -> {
                                                // Aktifkan dialog PIN untuk Private
                                                selectedVote = vote
                                                currentDialogUnicId = unicId
                                                pinDialog = true
                                            }

                                            voteType == "Public" -> {
                                                currentDialogUnicId = unicId
                                                navController.navigate("vote/$unicId")
                                            }

                                            voteType == "Election" -> {
                                                // Aktifkan dialog kredensial untuk Election
                                                currentDialogUnicId = unicId
                                                kredensial = true
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.padding(start = 55.dp, top = 20.dp)
                                ) {
                                    Text(
                                        text = if (hasVoted.value || isCreator) "Hasil" else "Vote",
                                        color = Color.White
                                    )
                                    val userInputs =
                                        remember { mutableStateMapOf<String, String>() }

                                    if (currentUser != null) {
                                        var fetchedUser by remember { mutableStateOf<User?>(null) }
                                        var displayedCredentials by remember {
                                            mutableStateOf<List<Map<String, Any>>>(
                                                emptyList()
                                            )
                                        }

                                        // Ambil data pengguna
                                        LaunchedEffect(currentUser.uid) {
                                            val userRef = database.getReference("users")
                                                .child(currentUser.uid)
                                            userRef.get().addOnSuccessListener { snapshot ->
                                                if (snapshot.exists()) {
                                                    val user =
                                                        snapshot.getValue(User::class.java)
                                                    fetchedUser = user
                                                }
                                            }
                                        }

                                        // Ambil data kredensial dari votes
                                        LaunchedEffect(unicId) {
                                            val voteRef =
                                                database.getReference("votes").child(unicId)
                                            voteRef.get().addOnSuccessListener { snapshot ->
                                                val credentialList =
                                                    snapshot.child("credential/voterCredentialType")
                                                        .children.mapNotNull { it.value as? Map<String, Any> }
                                                displayedCredentials = credentialList
                                            }
                                        }

                                        if (kredensial && currentDialogUnicId == unicId) {
                                            AlertDialog(
                                                onDismissRequest = {
                                                    kredensial = false
                                                    currentDialogUnicId = null
                                                },
                                                title = {
                                                    Text(
                                                        text = "Isi Data Kredensial",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp
                                                    )
                                                },
                                                text = {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .heightIn(
                                                                min = 200.dp,
                                                                max = 400.dp
                                                            )
                                                            .padding(8.dp)
                                                    ) {
                                                        Text(
                                                            text = "Sebelum Anda melanjutkan ke proses pemungutan suara, Anda harus mengisi beberapa identitas Anda.",
                                                            fontSize = 12.sp,
                                                            color = Color.Gray,
                                                            modifier = Modifier.padding(bottom = 16.dp)
                                                        )

                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .weight(1f)
                                                                .verticalScroll(
                                                                    rememberScrollState()
                                                                )
                                                        ) {
                                                            Column {
                                                                if (displayedCredentials.isNotEmpty()) {
                                                                    displayedCredentials.forEach { credential ->
                                                                        val nameTagType =
                                                                            credential["nameTagType"] as? String
                                                                                ?: "Unknown"
                                                                        val typeTag =
                                                                            credential["typeTag"] as? String
                                                                                ?: "text"
                                                                        val isMandatory =
                                                                            credential["isMandatory"] as? Boolean
                                                                                ?: false

                                                                        // Ambil nilai yang ada di fetchedUser dengan aman dan validasi null check
                                                                        val fetchedValue =
                                                                            when (typeTag) {
                                                                                "fullName" -> fetchedUser?.name
                                                                                    ?: ""

                                                                                "email" -> fetchedUser?.email
                                                                                    ?: ""

                                                                                "birthDate" -> fetchedUser?.birthDate
                                                                                    ?: ""

                                                                                "place" -> fetchedUser?.place
                                                                                    ?: ""

                                                                                else -> userInputs[typeTag].orEmpty()
                                                                            }

                                                                        // Tetapkan nilai jika belum diisi oleh user
                                                                        if ((userInputs[typeTag]?.isEmpty() == true) && fetchedValue?.isNotEmpty() == true) {
                                                                            userInputs[typeTag] =
                                                                                fetchedValue
                                                                                    ?: ""
                                                                        }

                                                                        // Berikan fleksibilitas kepada user untuk mengisi ulang jika diinginkan
                                                                        OutlinedTextField(
                                                                            value = userInputs[typeTag].orEmpty(),
                                                                            onValueChange = {
                                                                                userInputs[typeTag] =
                                                                                    it
                                                                            },
                                                                            label = {
                                                                                Text(
                                                                                    nameTagType
                                                                                )
                                                                            },
                                                                            placeholder = {
                                                                                Text(
                                                                                    "Masukkan $nameTagType"
                                                                                )
                                                                            },
                                                                            modifier = Modifier.fillMaxWidth(),
                                                                            singleLine = true,
                                                                            isError = isMandatory && userInputs[typeTag]?.isEmpty() == true,
                                                                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                                                                focusedBorderColor = Color(
                                                                                    0xFF008753
                                                                                ),
                                                                                unfocusedBorderColor = Color(
                                                                                    0xFF008753
                                                                                ),
                                                                                cursorColor = Color(
                                                                                    0xFF008753
                                                                                ),
                                                                            ),
                                                                        )
                                                                        Spacer(
                                                                            modifier = Modifier.height(
                                                                                8.dp
                                                                            )
                                                                        )
                                                                    }
                                                                } else {
                                                                    Text(
                                                                        text = "Tidak ada kredensial yang perlu diisi.",
                                                                        color = Color.Gray,
                                                                        modifier = Modifier.align(
                                                                            Alignment.CenterHorizontally
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                confirmButton = {
                                                    Button(
                                                        onClick = {
                                                            displayedCredentials.forEach { credential ->
                                                                val typeTag =
                                                                    credential["typeTag"]
                                                                        ?: "unknown"
                                                                Log.d(
                                                                    "CredentialDebug",
                                                                    "typeTag: $typeTag, value: ${userInputs[typeTag]}"
                                                                )
                                                            }

                                                            val isValid =
                                                                displayedCredentials.all { credential ->
                                                                    val typeTag =
                                                                        credential["typeTag"]
                                                                            ?: "unknown"
                                                                    val isMandatory =
                                                                        credential["isMandatory"] as? Boolean
                                                                            ?: false
                                                                    if (isMandatory) {
                                                                        userInputs[typeTag]?.isNotEmpty() == true
                                                                    } else {
                                                                        true
                                                                    }
                                                                }

                                                            if (isValid) {
                                                                val combinedCredential =
                                                                    displayedCredentials.joinToString(
                                                                        separator = ","
                                                                    ) { credential ->
                                                                        val typeTag =
                                                                            credential["typeTag"]
                                                                                ?: "unknown"
                                                                        val value =
                                                                            userInputs[typeTag].orEmpty()
                                                                        "$typeTag:$value"
                                                                    }

                                                                Log.d(
                                                                    "SendingCombinedCredential",
                                                                    "combinedCredential sebelum disimpan: $combinedCredential"
                                                                )

                                                                if (combinedCredential.isNotEmpty()) {
                                                                    // Simpan nilai dengan `currentBackStackEntry`
                                                                    navController.currentBackStackEntry
                                                                        ?.savedStateHandle
                                                                        ?.set(
                                                                            "combinedCredential",
                                                                            combinedCredential
                                                                        )

                                                                    Log.d(
                                                                        "SavedStateHandle",
                                                                        "combinedCredential disimpan dengan nilai: $combinedCredential"
                                                                    )

                                                                    // Navigasi ke halaman voting
                                                                    navController.navigate("electionVote/$unicId")
                                                                } else {
                                                                    Log.e(
                                                                        "SendingCombinedCredential",
                                                                        "combinedCredential kosong, tidak disimpan atau dinavigasikan"
                                                                    )
                                                                }
                                                            } else {
                                                                Toast.makeText(
                                                                    mContext,
                                                                    "Harap isi semua data wajib.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            Color(0xFF008753)
                                                        )
                                                    ) {
                                                        Text("Lanjut", color = Color.White)
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

            item {
                Text(
                    text = "Recommendation",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            // Tampilkan daftar vote dari Firebase
            items(voteList) { vote ->
                val title = vote["title"] as? String ?: "Tidak Ada Judul"
                val unicId = vote["unicID"] as? String ?: "Tidak Ada ID"
                val voteType = vote["type"] as? String ?: "Unknown"
                val endDate = vote["endDate"] as? String ?: "01/01/2000"
                val endTime = vote["endTime"] as? String ?: "00:00"
                val totalVotes = vote["totalVotes"] as? Int ?: 0
                Log.d("VoteDebug", "Displaying Total Votes Integer: $totalVotes")
                val createdBy = vote["createdBy"] as? String ?: ""
                val pin = vote["pin"] as? String ?: ""
                val isCreator = currentUser?.uid == createdBy

                // Menentukan label dan warna berdasarkan voteType
                val (typeLabel, typeColor) = when (voteType) {
                    "Private" -> "PRIVAT" to Color(0xFFD32F2F)
                    "Public" -> "PUBLIK" to Color(0xFF008753)
                    "Election" -> "PEMILU" to Color(0xFFFF9500)
                    else -> "Unknown" to Color.Gray
                }

                val remainingTime = run {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val endDateTimeString = "$endDate $endTime"
                    val endDateTime = try {
                        sdf.parse(endDateTimeString)
                    } catch (e: Exception) {
                        null
                    }

                    if (endDateTime != null) {
                        val currentTime = Date()
                        val diff = endDateTime.time - currentTime.time
                        when {
                            diff < 60 * 1000 -> "Kurang dari 1 menit" // Kurang dari 1 menit
                            diff < 24 * 60 * 60 * 1000 -> { // Kurang dari 1 hari
                                val hours = diff / (60 * 60 * 1000)
                                "Tersisa $hours jam"
                            }

                            diff < 30L * 24 * 60 * 60 * 1000 -> { // Kurang dari 1 bulan
                                val days = diff / (24 * 60 * 60 * 1000)
                                "Tersisa $days hari"
                            }

                            else -> "Tidak diketahui"
                        }
                    } else {
                        "Format waktu salah"
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row {
                            Image(
                                painter = painterResource(R.drawable.pngwing_com),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        Color.Gray.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "ID: $unicId", color = Color.Gray)
                            }
                            Text(
                                text = typeLabel, // Menampilkan label tipe
                                fontWeight = FontWeight.ExtraBold,
                                color = typeColor, // Menampilkan warna sesuai tipe
                                modifier = Modifier.padding(end = 10.dp, top = 5.dp)
                            )
                        }

                        Row {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = remainingTime, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "$totalVotes votes", color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.width(90.dp))

                            // Tombol Vote atau Hasil
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                var currentDialogUnicId by remember { mutableStateOf<String?>(null) } // Untuk melacak dialog yang aktif
                                var hasVoted = remember { mutableStateOf(false) }

                                // Cek apakah pengguna sudah memberikan suara
                                val userReference =
                                    Firebase.database.getReference("users/${currentUser?.uid}")
                                userReference.child("hasBeenVotedAt").get()
                                    .addOnSuccessListener { snapshot ->
                                        if (snapshot.exists() && snapshot.hasChild(unicId)) {
                                            // Jika sudah memberikan suara
                                            hasVoted.value = true
                                        }
                                    }

                                Button(
                                    onClick = {
                                        when {
                                            isCreator -> {
                                                navController.navigate("result/$unicId")
                                            }

                                            hasVoted.value -> {
                                                // Jika sudah memberikan suara, tampilkan hasil
                                                navController.navigate("result/$unicId")
                                            }

                                            voteType == "Private" -> {
                                                // Aktifkan dialog PIN untuk Private
                                                selectedVote = vote
                                                currentDialogUnicId = unicId
                                                pinDialog = true
                                            }

                                            voteType == "Public" -> {
                                                currentDialogUnicId = unicId
                                                navController.navigate("vote/$unicId")
                                            }

                                            voteType == "Election" -> {
                                                // Aktifkan dialog kredensial untuk Election
                                                currentDialogUnicId = unicId
                                                kredensial = true
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.padding(start = 55.dp, top = 20.dp)
                                ) {
                                    Text(
                                        text = if (hasVoted.value || isCreator) "Hasil" else "Vote",
                                        color = Color.White
                                    )

                                    val userInputs =
                                        remember { mutableStateMapOf<String, String>() }

                                    if (currentUser != null) {
                                        var fetchedUser by remember { mutableStateOf<User?>(null) }
                                        var displayedCredentials by remember {
                                            mutableStateOf<List<Map<String, Any>>>(
                                                emptyList()
                                            )
                                        }

                                        // Ambil data pengguna
                                        LaunchedEffect(currentUser.uid) {
                                            val userRef = database.getReference("users")
                                                .child(currentUser.uid)
                                            userRef.get().addOnSuccessListener { snapshot ->
                                                if (snapshot.exists()) {
                                                    val user =
                                                        snapshot.getValue(User::class.java)
                                                    fetchedUser = user
                                                }
                                            }
                                        }

                                        // Ambil data kredensial dari votes
                                        LaunchedEffect(unicId) {
                                            val voteRef =
                                                database.getReference("votes").child(unicId)
                                            voteRef.get().addOnSuccessListener { snapshot ->
                                                val credentialList =
                                                    snapshot.child("credential/voterCredentialType")
                                                        .children.mapNotNull { it.value as? Map<String, Any> }
                                                displayedCredentials = credentialList
                                            }
                                        }

                                        if (kredensial && currentDialogUnicId == unicId) {
                                            AlertDialog(
                                                onDismissRequest = {
                                                    kredensial = false
                                                    currentDialogUnicId = null
                                                },
                                                title = {
                                                    Text(
                                                        text = "Isi Data Kredensial",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp
                                                    )
                                                },
                                                text = {
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .heightIn(
                                                                min = 200.dp,
                                                                max = 400.dp
                                                            )
                                                            .padding(8.dp)
                                                    ) {
                                                        Text(
                                                            text = "Sebelum Anda melanjutkan ke proses pemungutan suara, Anda harus mengisi beberapa identitas Anda.",
                                                            fontSize = 12.sp,
                                                            color = Color.Gray,
                                                            modifier = Modifier.padding(bottom = 16.dp)
                                                        )

                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .weight(1f)
                                                                .verticalScroll(
                                                                    rememberScrollState()
                                                                )
                                                        ) {
                                                            Column {
                                                                if (displayedCredentials.isNotEmpty()) {
                                                                    displayedCredentials.forEach { credential ->
                                                                        val nameTagType =
                                                                            credential["nameTagType"] as? String
                                                                                ?: "Unknown"
                                                                        val typeTag =
                                                                            credential["typeTag"] as? String
                                                                                ?: "text"
                                                                        val isMandatory =
                                                                            credential["isMandatory"] as? Boolean
                                                                                ?: false

                                                                        // Ambil nilai yang ada di fetchedUser dengan aman dan validasi null check
                                                                        val fetchedValue =
                                                                            when (typeTag) {
                                                                                "fullName" -> fetchedUser?.name
                                                                                    ?: ""

                                                                                "email" -> fetchedUser?.email
                                                                                    ?: ""

                                                                                "birthDate" -> fetchedUser?.birthDate
                                                                                    ?: ""

                                                                                "place" -> fetchedUser?.place
                                                                                    ?: ""

                                                                                else -> userInputs[typeTag].orEmpty()
                                                                            }

                                                                        // Tetapkan nilai jika belum diisi oleh user
                                                                        if ((userInputs[typeTag]?.isEmpty() == true) && fetchedValue?.isNotEmpty() == true) {
                                                                            userInputs[typeTag] =
                                                                                fetchedValue
                                                                                    ?: ""
                                                                        }

                                                                        // Berikan fleksibilitas kepada user untuk mengisi ulang jika diinginkan
                                                                        OutlinedTextField(
                                                                            value = userInputs[typeTag].orEmpty(),
                                                                            onValueChange = {
                                                                                userInputs[typeTag] =
                                                                                    it
                                                                            },
                                                                            label = {
                                                                                Text(
                                                                                    nameTagType
                                                                                )
                                                                            },
                                                                            placeholder = {
                                                                                Text(
                                                                                    "Masukkan $nameTagType"
                                                                                )
                                                                            },
                                                                            modifier = Modifier.fillMaxWidth(),
                                                                            singleLine = true,
                                                                            isError = isMandatory && userInputs[typeTag]?.isEmpty() == true,
                                                                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                                                                focusedBorderColor = Color(
                                                                                    0xFF008753
                                                                                ),
                                                                                unfocusedBorderColor = Color(
                                                                                    0xFF008753
                                                                                ),
                                                                                cursorColor = Color(
                                                                                    0xFF008753
                                                                                ),
                                                                            ),
                                                                        )
                                                                        Spacer(
                                                                            modifier = Modifier.height(
                                                                                8.dp
                                                                            )
                                                                        )
                                                                    }
                                                                } else {
                                                                    Text(
                                                                        text = "Tidak ada kredensial yang perlu diisi.",
                                                                        color = Color.Gray,
                                                                        modifier = Modifier.align(
                                                                            Alignment.CenterHorizontally
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                confirmButton = {
                                                    Button(
                                                        onClick = {
                                                            displayedCredentials.forEach { credential ->
                                                                val typeTag =
                                                                    credential["typeTag"]
                                                                        ?: "unknown"
                                                                Log.d(
                                                                    "CredentialDebug",
                                                                    "typeTag: $typeTag, value: ${userInputs[typeTag]}"
                                                                )
                                                            }

                                                            val isValid =
                                                                displayedCredentials.all { credential ->
                                                                    val typeTag =
                                                                        credential["typeTag"]
                                                                            ?: "unknown"
                                                                    val isMandatory =
                                                                        credential["isMandatory"] as? Boolean
                                                                            ?: false
                                                                    if (isMandatory) {
                                                                        userInputs[typeTag]?.isNotEmpty() == true
                                                                    } else {
                                                                        true
                                                                    }
                                                                }

                                                            if (isValid) {
                                                                val combinedCredential =
                                                                    displayedCredentials.joinToString(
                                                                        separator = ","
                                                                    ) { credential ->
                                                                        val typeTag =
                                                                            credential["typeTag"]
                                                                                ?: "unknown"
                                                                        val value =
                                                                            userInputs[typeTag].orEmpty()
                                                                        "$typeTag:$value"
                                                                    }

                                                                Log.d(
                                                                    "SendingCombinedCredential",
                                                                    "combinedCredential sebelum disimpan: $combinedCredential"
                                                                )

                                                                if (combinedCredential.isNotEmpty()) {
                                                                    // Simpan nilai dengan `currentBackStackEntry`
                                                                    navController.currentBackStackEntry
                                                                        ?.savedStateHandle
                                                                        ?.set(
                                                                            "combinedCredential",
                                                                            combinedCredential
                                                                        )

                                                                    Log.d(
                                                                        "SavedStateHandle",
                                                                        "combinedCredential disimpan dengan nilai: $combinedCredential"
                                                                    )

                                                                    // Navigasi ke halaman voting
                                                                    navController.navigate("electionVote/$unicId")
                                                                } else {
                                                                    Log.e(
                                                                        "SendingCombinedCredential",
                                                                        "combinedCredential kosong, tidak disimpan atau dinavigasikan"
                                                                    )
                                                                }
                                                            } else {
                                                                Toast.makeText(
                                                                    mContext,
                                                                    "Harap isi semua data wajib.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            Color(0xFF008753)
                                                        )
                                                    ) {
                                                        Text("Lanjut", color = Color.White)
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


        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text(text = "Jenis Vote") },
                text = { Text(text = "Silahkan pilih jenis voting anda!") },
                confirmButton = {
                    Button(
                        onClick = { navController.navigate("publicPrivate") },
                        colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                    ) {
                        Text("Public/Private", color = Color.White)
                    }
                    Button(
                        onClick = { navController.navigate("credential") },
                        colors = ButtonDefaults.buttonColors(
                            Color(0xFFFF9500)
                        )
                    ) {
                        Text("Election")
                    }
                },
            )
        }
        IconButton(
            onClick = { isDialogOpen = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Icon",
                tint = Color(0xFF008753),
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White, shape = CircleShape)
                    .border((0.5).dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
            )
        }
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
                    onClick = { navController.navigate("created") },
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
                    onClick = {},
                ) {
                    Image(
                        painter = painterResource(R.drawable.group_55),
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
                    onClick = { navController.navigate("profile") },
                ) {
                    Image(
                        painter = painterResource(R.drawable.group_138),
                        contentDescription = null
                    )
                }
            }
        }
    }
}