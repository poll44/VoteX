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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@Composable
fun ResultPage(navController: NavController, unicId: String?) {
    var totalVotes by remember { mutableStateOf(0L) }
    var type by remember { mutableStateOf("Unknown") }
    var optionsDisplay by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var title by remember { mutableStateOf("Unknown") }
    var deskripsi by remember { mutableStateOf("Unknown") }
    val sanitizedUnicId = unicId?.replace("{", "")?.replace("}", "")
    var endDate by remember { mutableStateOf("Unknown") }
    var endTime by remember { mutableStateOf("Unknown") }

// Menghitung opsi yang paling banyak dipilih dan persentasenya
    val mostVotedOption = optionsDisplay.maxByOrNull { it.second }
    val mostVotedText = mostVotedOption?.first ?: "Unknown"
    val mostVotedCount = mostVotedOption?.second ?: 0
    val mostVotedPercentage = if (totalVotes > 0) (mostVotedCount.toDouble() / totalVotes * 100) else 0.0

// Membuat teks dengan atribut bold untuk informasi yang relevan
    val formattedText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append("Hingga detik ini ")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
            append("$mostVotedText")
        }
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append(" dengan total ")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
            append("$mostVotedCount")
        }
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append(" orang pemilih dengan persentase ")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
            append("${mostVotedPercentage.toInt()}%. ")
        }
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append("Pemilihan akan selesai di tanggal ")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
            append("$endDate")
        }
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append(" pukul ")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
            append("$endTime.")
        }
    }

// Ambil data dari Firebase
    if (!LocalInspectionMode.current && sanitizedUnicId != null) {
        val voteReference = Firebase.database.getReference("votes/$sanitizedUnicId")

        LaunchedEffect(sanitizedUnicId) {
            voteReference.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    totalVotes = snapshot.child("totalVotes").getValue(Long::class.java) ?: 0L
                    title = snapshot.child("title").getValue(String::class.java) ?: "Unknown"
                    deskripsi = snapshot.child("description").getValue(String::class.java) ?: "Unknown"
                    endDate = snapshot.child("endDate").getValue(String::class.java) ?: "Unknown"
                    endTime = snapshot.child("endTime").getValue(String::class.java) ?: "Unknown"
                    type = snapshot.child("type").getValue(String::class.java) ?: "Unknown"

                    // Menentukan opsi yang akan ditampilkan dan jumlah suaranya
                    val optionsSnapshot = snapshot.child("options")
                    val optionsList = mutableListOf<Pair<String, Int>>() // Data untuk pie chart

                    optionsSnapshot.children.forEach { option ->
                        val optionText = when (type) {
                            "Public", "Private" -> option.child("pilihan").getValue(String::class.java)
                            "Election" -> option.child("teamAlias${option.key?.last()}").getValue(String::class.java)
                            else -> null
                        }

                        val voteCountLong = option.child("voters")?.childrenCount ?: 0L
                        val voteCountInt = voteCountLong.toInt()

                        if (!optionText.isNullOrEmpty()) {
                            optionsList.add(Pair(optionText, voteCountInt))
                        }
                    }

                    optionsDisplay = optionsList

                    // Log tambahan debugging
                    Log.d("DEBUG", "Options List: $optionsDisplay")
                    Log.d("DEBUG", "Total Votes: $totalVotes")

                    if (optionsDisplay.isNotEmpty()) {
                        Log.d("DEBUG", "Setting up PieChart")
                    } else {
                        Log.e("PieChartError", "No data to show in the pie chart.")
                    }
                } else {
                    Log.e("SnapshotError", "No data found for unicId: $sanitizedUnicId")
                }
            }.addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error fetching vote data: ${exception.message}")
            }
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
                    text = "Ringkasan Hasil Pemilihan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
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
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(15.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = deskripsi,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )
                    //grafik kita selesaikan nanti dulu
                    Text(text = "Grafik Langsung", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(15.dp), fontSize = 17.sp,)
                    if (optionsDisplay.isNotEmpty()) {
                        PieChartView(optionsDisplay, totalVotes)
                    } else {
                        Text(
                            text = "Belum ada data untuk ditampilkan",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Ringkasan Langsung", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(15.dp), fontSize = 17.sp)

                    Text(
                        text = formattedText,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Persentase Langsung", fontWeight = FontWeight.Bold, modifier = Modifier.padding(15.dp), fontSize = 17.sp)

                    optionsDisplay.forEach { (optionText, voteCount) ->
                        val percentage = if (totalVotes > 0) {
                            (voteCount.toDouble() / totalVotes * 100).toInt()
                        } else {
                            0
                        }

                        Text(
                            text = "$optionText: $percentage%",
                            modifier = Modifier.padding(start = 15.dp),
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "Total voter: $totalVotes orang",
                        modifier = Modifier.padding(start = 15.dp, bottom = 15.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PieChartView(
    optionsDisplay: List<Pair<String, Int>>,
    totalVotes: Long
) {
    val validOptions = optionsDisplay.filter { it.second > 0 }
    Box(
        modifier = Modifier
            .fillMaxSize(), // Memastikan kita menggunakan seluruh ukuran layar
        contentAlignment = Alignment.Center // Memastikan semua konten berada di tengah
    ){
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    // Hanya tampilkan grafik untuk suara valid
                    setupPieChart(validOptions, totalVotes, this)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }

}

fun setupPieChart(
    optionsDisplay: List<Pair<String, Int>>,
    totalVotes: Long,
    pieChartView: PieChart
) {
    // Tetap tampilkan semua keterangan warna meskipun nilai suaranya nol
    val entries = optionsDisplay
        .filter { it.second > 0 } // Filter hanya tampilkan bagian pie chart yang valid
        .map { PieEntry(it.second.toFloat(), it.first) }

    val pieDataSet = PieDataSet(entries, "").apply {
        colors = ColorTemplate.MATERIAL_COLORS.toList()
    }

    val pieData = PieData(pieDataSet).apply {
        setValueTextSize(12f)
        setValueTextColor(Color.Black.toArgb())
    }

    pieChartView.apply {
        setUsePercentValues(true)
        description.isEnabled = false
        setCenterTextSize(18f) // Mengatur ukuran teks menjadi 18
        setCenterTextColor(Color.Black.toArgb()) // Mengatur warna teks menjadi hitam
        setDrawHoleEnabled(true)
        setDrawSlicesUnderHole(true)
        setHoleColor(Color.White.toArgb())
        setTransparentCircleRadius(58f)
        data = pieData
        invalidate()
    }
}