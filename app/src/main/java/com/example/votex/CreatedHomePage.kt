package com.example.votex

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@Composable
fun CreatedHomePage(navController: NavController) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val voteList = remember { mutableStateListOf<Map<String, Any>>() }
    val context = LocalContext.current

    if (currentUser != null) {
        // Load user name and votes
        LaunchedEffect(key1 = currentUser.uid) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(currentUser.uid)
            val voteRef = database.getReference("votes")

            // Load user name
            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val fetchedUser = snapshot.getValue(User::class.java)
                    fetchedUser?.let {
                        userName = it.name ?: "User"
                    }
                }
            }

            // Load votes created by the user
            userRef.child("voteCreated").get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val voteIds = snapshot.children.mapNotNull { it.key }
                    voteIds.forEach { voteId ->
                        voteRef.child(voteId).get().addOnSuccessListener { voteSnapshot ->
                            if (voteSnapshot.exists()) {
                                val data = voteSnapshot.value
                                if (data is Map<*, *>) {
                                    // Validasi data dan konversi
                                    val validatedData = mapOf(
                                        "title" to (data["title"] as? String ?: "Tidak Ada Judul"),
                                        "unicID" to (data["unicID"] as? String ?: "Tidak Ada ID"),
                                        "type" to (data["type"] as? String ?: "Unknown"),
                                        "endDate" to (data["endDate"] as? String ?: "01/01/2000"),
                                        "endTime" to (data["endTime"] as? String ?: "00:00"),
                                        "totalVotes" to ((data["totalVotes"] as? Long)?.toInt() ?: 0),
                                        "pin" to (data["pin"] as? String ?: ""),
                                        "createdBy" to (data["createdBy"] as? String ?: "")
                                    )
                                    voteList.add(validatedData)
                                } else {
                                    Log.e("VoteDebug", "Invalid data format for voteId: $voteId")
                                }
                            }
                        }.addOnFailureListener { error ->
                            Log.e("VoteDebug", "Error fetching voteId $voteId: ${error.message}")
                        }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Gagal memuat data votes: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border((0.5).dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFFFFF))


            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(35.dp)
                        .background(Color.White)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = "Dibuat",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF008753)
                    )

                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(16.dp)
                            .width(2.dp)
                    )

                    Text(
                        modifier = Modifier.clickable { navController.navigate("participated")},
                        text = "Berpartisipasi",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            /**
             * list yang dibuat
             */
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(voteList) { vote ->
                    val title = vote["title"] as? String ?: "Tidak Ada Judul"
                    val unicId = vote["unicID"] as? String ?: "Tidak Ada ID"
                    val voteType = vote["type"] as? String ?: "Unknown"
                    val endDate = vote["endDate"] as? String ?: "01/01/2000" // Format: "dd/MM/yyyy"
                    val endTime = vote["endTime"] as? String ?: "00:00" // Format: "HH:mm"
                    val totalVotes = vote["totalVotes"] as? Int ?: 0
                    val pin = vote["pin"]

                    // Warna dan label berdasarkan tipe vote
                    val typeLabel = when (voteType) {
                        "Private" -> "PRIVAT" to Color(0xFFD32F2F)
                        "Public" -> "PUBLIK" to Color(0xFF008753)
                        "Election" -> "PEMILU" to Color(0xFFFF9500)
                        else -> "Unknown" to Color.Gray
                    }

                    // Menghitung waktu tersisa
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        // Header Row
                        Row() {
                            Image(
                                painter = painterResource(R.drawable.pngwing_com),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "ID: $unicId", color = Color.Gray)
                            }
                            Text(
                                text = typeLabel.first,
                                fontWeight = FontWeight.ExtraBold,
                                color = typeLabel.second
                            )
                        }

                        // Remaining time and total votes row
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
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
                            // Right-side Column for PIN and Button
                            Column(
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.End
                            ) {
                                if (voteType == "Public" || voteType == "Election") {
                                    // Empty space for alignment
                                    Text(text = "", color = Color.Gray)
                                } else {
                                    Text(text = "PIN: $pin", color = Color.Gray)
                                }
                                Button(
                                    onClick = { navController.navigate("result/{$unicId}") },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(text = "Hasil", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
        if(isDialogOpen){
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text(text = "Jenis Vote") },
                text = { Text(text = "Silahkan pilih jenis voting anda!") },
                confirmButton = {
                    Button(onClick = { navController.navigate("publicPrivate")}, colors = ButtonDefaults.buttonColors(Color(0xFF008753))) {
                        Text("Public/Private", color = Color.White)
                    }
                    Button(onClick = { navController.navigate("credential")}, colors = ButtonDefaults.buttonColors(Color(0xFFFF9500)
                    )) {
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
                modifier = Modifier.fillMaxSize().background(color = Color.White, shape = CircleShape)
            )
        }
        /**
         * Bottom Navigation Bar
         */
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
                    onClick = {},
                ) {
                    Image(
                        painter = painterResource(R.drawable.group_54),
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
                    onClick = {navController.navigate("profile")},
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
