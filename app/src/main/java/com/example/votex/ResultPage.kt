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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun ResultPage(navController: NavController, unicId: String?) {

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
                    text = "Ringkasan Hasil Pemilihan",
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
                    Text(
                        text = "Pemilihan Presiden Indonesia Tahun 2024-2029",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(15.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Ini merupakan pemilihan umum presiden untuk menentukan bangsa indonesia kedepannya. Pemilihan ini merupakan pemilihan umum pertama yang menggunakan aplikasi ini karena aplikasi ini menggunakan teknologi block chain sebagai keamanan datanya.",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )
                    Text(text = "Grafik Langsung", fontWeight = FontWeight.Bold, modifier = Modifier.padding(15.dp))

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Ringkasan Langsung", fontWeight = FontWeight.Bold, modifier = Modifier.padding(15.dp))

                    Text(
                        text = "Hingga detik ini AFB Team masih mengungguli Pemilihan Presiden Indonesia 2024-2025 dengan total 5 orang pemilih dengan persentase 34%. Pemilihan akan selesai di tanggal 10 September 2024 pukul 20.30.",
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Persentase Langsung", fontWeight = FontWeight.Bold, modifier = Modifier.padding(15.dp))

                    Text(text = "AFB Team: 34%\nSHRREX: 33%\nJumlah pemilih: 10 Orang", modifier = Modifier.padding(start = 15.dp, bottom = 15.dp))

                }
            }
        }
    }
}