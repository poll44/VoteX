package com.example.votex

import android.app.DatePickerDialog
import android.widget.DatePicker
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.util.Calendar

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController) {
    var activeEmail by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var residence by remember { mutableStateOf("") }
    var faculty by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

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
    var pinDialog by remember { mutableStateOf(false) }
    var kredensial by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var pin = "1234"
    var userEmail: String = ""
    var userId: String = ""
    val mContext = LocalContext.current


    if (!LocalInspectionMode.current){
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email.toString()
            userEmail = email.substringBefore("@")
        }
        database = Firebase.database
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF0F0F0))
    ) {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
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
                                text = "Hi, $userEmail"
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
                        onValueChange = { newtext -> text = newtext },
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
                    )

                    Button(
                        onClick = { },
                        shape = CircleShape,
                        modifier = Modifier
                            .height(50.dp)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                    ) {
                        Text(text = "Cari", fontSize = 16.sp, color = Color.White)
                    }
                }
                Text(
                    text = "Recommendation",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
                /**
                 * list yang dibuat
                 */


                /**
                 * Publik
                 */
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
                            Spacer(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(8.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "title", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "id = 1234", color = Color.Gray)
                            }
                            Text(
                                text = "PUBLIK",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF27AE60),
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
                                    Text(text = "tersisa 5 menit", color = Color.Gray)
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
                                    Text(text = "1000 votes", color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.width(90.dp))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Button(
                                    onClick = { navController.navigate("vote") },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(text = "Vote", color = Color.White)
                                }
                            }
                        }
                    }
                }

                /**
                 * Privat
                 */
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
                            Spacer(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(8.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "title", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "id = 1234", color = Color.Gray)
                            }
                            Text(
                                text = "PRIVAT",
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
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
                                    Text(text = "tersisa 5 menit", color = Color.Gray)
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
                                    Text(text = "1000 votes", color = Color.Gray)
                                }
                            }
                            if(pinDialog){
                                var pinInput by remember { mutableStateOf("") }
                                AlertDialog(
                                    onDismissRequest = { pinDialog = false },
                                    title = { Text(text = "Masukkan Pin Voting") },
                                    text = {
                                        Column {
                                            Text(text = "Voting ini berjenis privat. Masukkan pin yang benar untuk masuk ke voting ini")
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = pinInput,
                                                onValueChange = { pinInput = it },
                                                label = { Text("PIN") },
                                                singleLine = true,
                                                visualTransformation = PasswordVisualTransformation()
                                            )
                                        }
                                    },

                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                if (pinInput == pin) {
                                                    navController.navigate("vote")
                                                } else {
                                                    Toast.makeText(
                                                        mContext,
                                                        "Pin anda salah",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                                        ) {
                                            Text("Lanjut", color = Color.White)
                                        }
                                    },
                                )
                            }
                            Spacer(modifier = Modifier.width(90.dp))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(text = "PIN : $pin", color = Color.Gray)
                                Button(
                                    onClick = { pinDialog= true },
                                    colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(text = "Vote", color = Color.White)
                                }
                            }
                        }
                    }
                }
                /**
                 * Election
                 */
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
                            Spacer(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(8.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "title", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(text = "id = 1234", color = Color.Gray)
                            }
                            Text(
                                text = "PEMILU",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9500),
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
                                    Text(text = "tersisa 5 menit", color = Color.Gray)
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
                                    Text(text = "1000 votes", color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.width(90.dp))
                            var confirm by remember { mutableStateOf(false) }
                            if (confirm){
                                AlertDialog(
                                    onDismissRequest = { confirm = false },
                                    title = { Text(text = "PERINGATAN!") },
                                    text = {
                                        Column(modifier = Modifier.fillMaxWidth()) {

                                            Text(
                                                text = "Is the Data You Fill In Real Data?",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = "Before proceeding, we want to ensure that all information you provide is accurate and in accordance with reality. Invalid or inappropriate data may result in your failure to participate in this voting process.",
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Justify,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )


                                            Text(
                                                text = "Why is it important?",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = "In a blockchain-based system, every data you enter will be recorded permanently, immutable, and become part of a digital footprint that cannot be faked. Data accuracy is essential to maintain the integrity, fairness, and transparency of the entire process.",
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Justify,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )


                                            Text(
                                                text = "Remember!",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = "Any data you provide is your personal responsibility. By continuing, you represent that the information provided is correct, and you are fully aware that this data will be used in a final and irreversible voting process.\n\nThe trust you build starts with the accuracy of the data you provide. Don't let this important decision be based on the wrong data!",
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Justify,
                                                color = Color.Gray
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))


                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Checkbox(
                                                    checked = isChecked,
                                                    onCheckedChange = { isChecked = it }
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "I declare that the data I have entered is correct and accountable",
                                                    fontSize = 12.sp,
                                                    color = Color.Black,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Button(
                                                onClick = { if (isChecked) navController.navigate("electionVote") },
                                                colors = ButtonDefaults.buttonColors(Color(0xFF008753)
                                                ),
                                                enabled = isChecked
                                            ) {
                                                Text("Next", color = Color.White)
                                            }
                                        }
                                    },
                                    dismissButton = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Button(
                                                onClick = { kredensial = true },
                                                colors = ButtonDefaults.buttonColors(Color.Red),
                                                enabled = isChecked
                                            ) {
                                                Text("Batal", color = Color.White)
                                            }
                                        }
                                    }
                                )
                            }
                            if(kredensial){
                                AlertDialog(
                                    onDismissRequest = { kredensial = false },
                                    title = { Text(text = "Isi Data Kredensial") },
                                    text = {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = "Sebelum Anda melanjutkan ke proses pemungutan suara, Anda harus mengisi beberapa identitas Anda, karena ini penting untuk memastikan keamanan dan keabsahan setiap suara yang diberikan. Identitas ini akan diverifikasi secara otomatis untuk mencegah pemalsuan atau duplikasi suara.",
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                            )

                                            OutlinedTextField(
                                                value = activeEmail,
                                                onValueChange = { activeEmail = it },
                                                label = { Text("Alamat Email") },
                                                placeholder = { Text("Contoh: username@gmail.com") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = fullName,
                                                onValueChange = { fullName = it },
                                                label = { Text("Nama Lengkap") },
                                                placeholder = { Text("Contoh: Username bin Khodam") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = birthDate,
                                                onValueChange = { },
                                                label = { Text("Tanggal lahir") },
                                                placeholder = { Text("25/11/2022") },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { datePickerDialog.show() },
                                                readOnly = true,
                                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                                    focusedBorderColor = Color(0xFF008753),
                                                    unfocusedBorderColor = Color(0xFF008753),
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
                                            Spacer(modifier = Modifier.height(8.dp))

                                            OutlinedTextField(
                                                value = residence,
                                                onValueChange = { residence = it },
                                                label = { Text("Alamat") },
                                                placeholder = { Text("Contoh: Jl. Gunung Harta No. 5") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = { navController.navigate("electionVote") },
                                            colors = ButtonDefaults.buttonColors(Color(0xFF008753))
                                        ) {
                                            Text("Lanjut", color = Color.White)
                                        }
                                    },
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Button(
                                    onClick = {kredensial = true},
                                    colors = ButtonDefaults.buttonColors(Color(0xFF27AE60)),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(text = "Vote", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
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
                    onClick = {navController.navigate("home")},
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