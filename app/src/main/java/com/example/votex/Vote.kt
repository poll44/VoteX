package com.example.votex

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
fun VotePage(navController: NavController) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(mutableListOf("", "", "")) }

    if (!LocalInspectionMode.current) {
        auth = Firebase.auth
        database = Firebase.database
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
                Text(
                    text = "Tim bubur diaduk apa nda?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp).align(Alignment.TopStart)
                )
            }
            item {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFFFFFFF))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.image_23),
                            contentDescription = "Gambar Bubur",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Survei ini dilakukan untuk dijadikan tugas akhir di Jurusan Tataboga Universitas Bulan Sabit Menyala. Mohon untuk dijawab tanpa ada paksaan dari pihak manapun.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        RadioButtonGroup(
                            options = listOf("TIM BUBUR DIADUK LAH", "TIM BUBUR NDA DIADUK"),
                            selectedOption = selectedOption,
                            onOptionSelected = { selectedOption = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if(isDialogOpen){
                        AlertDialog(
                            onDismissRequest = { isDialogOpen = false },
                            title = { Text(text = "Konfirmasi Pilihan Anda!") },
                            text = { Text(text = "Konfirmasi pilihan anda sebelum mengirimkan voting. Jika anda telah yakin dengan pilihan anda, tekan tombol "+"Kirim"+". Anda tidak dapat mengubah pilihan anda setelah mengirimkan voting. ") },
                            confirmButton = {
                                Button(onClick = { navController.navigate("result")}, colors = ButtonDefaults.buttonColors(Color(0xFF008753))) {
                                    Text("Kirim", color = Color.White)
                                }
                            },
                            dismissButton = {
                                Button(onClick = { isDialogOpen = false}, colors = ButtonDefaults.buttonColors(Color(0xFFFF0000)
                                ))  {
                                    Text("Batal", color = Color.White)
                                }
                            }
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { isDialogOpen = true},
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

