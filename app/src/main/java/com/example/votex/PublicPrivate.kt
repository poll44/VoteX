package com.example.votex

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    var options by remember { mutableStateOf(mutableListOf("Option 1", "Option 2", "Option 3")) }

    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)),
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

            Text(text = "Make your own voting", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFFFFFFF))
            ) {
                // Type of voting
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = voteType == VoteType.Public,
                        onClick = { voteType = VoteType.Public }
                    )
                    Text(text = "Public")
                    RadioButton(
                        selected = voteType == VoteType.Private,
                        onClick = { voteType = VoteType.Private }
                    )
                    Text(text = "Private")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Header
                    OutlinedTextField(
                        value = voteTitle,
                        onValueChange = { voteTitle = it },
                        label = { Text("Header") },
                        placeholder = { Text("example: PEMILIHAN KETUA BEM FILKOM 2024") }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Description
                    OutlinedTextField(
                        value = voteDescription,
                        onValueChange = { voteDescription = it },
                        label = { Text("Description") },
                        placeholder = { Text("example: Ini merupakan...") }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Add Photo
                    Button(onClick = { /* handle photo selection */ }) {
                        Text("Choose Photo")
                    }
                    Text(selectedPhoto ?: "No file selected")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // End date
                    OutlinedTextField(
                        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDateTime.time),
                        onValueChange = { /* handle change */ },
                        label = { Text("End date (DD/MM/YYYY)") },
                        leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                        placeholder = { Text("25/11/2022") }
                    )
                }


                // End time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = endHour,
                        onValueChange = { endHour = it },
                        label = { Text("End time (HH:MM)") },
                        placeholder = { Text("e.g., 14:00") }
                    )
                    IconButton(onClick = { /* handle time picker */ }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Time")
                    }
                }

                // Vote Options
                Text("Vote Options", style = MaterialTheme.typography.bodyMedium)
                options.forEachIndexed { index, option ->
                    OutlinedTextField(
                        value = option,
                        onValueChange = { options[index] = it },
                        label = { Text("Option") },
                        placeholder = { Text("Enter option") }
                    )
                }

                Button(onClick = { options.add("New Option") }) {
                    Text("Add Option")
                }
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Button(onClick = {
                        // Handle form submission
                        Toast.makeText(context, "Voting form submitted", Toast.LENGTH_SHORT).show()
                    },  modifier = Modifier.align(Alignment.BottomEnd),
                        colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                        shape = RoundedCornerShape(50)) {
                        Text("Submit")
                    }
                }
            }

        }
    }
}

enum class VoteType {
    Public, Private
}