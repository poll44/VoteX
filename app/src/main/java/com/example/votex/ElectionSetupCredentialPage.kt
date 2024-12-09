package com.example.votex

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase

@Composable
fun ElectionSetupCredentialPage(navController: NavController) {
    val mandatoryCredentials by remember {
        mutableStateOf(listOf(
            VoteElection.Credential.CredentialType("Email", "email", 50, true),
            VoteElection.Credential.CredentialType("Nomor Identitas", "identityNum", 20, true),
            VoteElection.Credential.CredentialType("Nama Lengkap", "fullName", 50, true),
            VoteElection.Credential.CredentialType("Tanggal Lahir", "birthDate", 50, true),
            VoteElection.Credential.CredentialType("Kota/Wilayah","place", 50, true)

        ))
    }

    // State untuk kredensial tambahan
    var additionalCredentials by remember {
        mutableStateOf<List<VoteElection.Credential.CredentialType>>(emptyList())
    }

    // State untuk input dialog
    var showAddCredentialDialog by remember { mutableStateOf(false) }
    var newCredentialName by remember { mutableStateOf("") }
    var newCredentialMaxInput by remember { mutableStateOf("") }

    var selectedOption by remember { mutableStateOf("") }
    var maxInput by remember { mutableStateOf("") }
    val isChecked1 by remember { mutableStateOf(true) }
    val maxAttributes = 5

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F0F0))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)) // Add padding to the whole LazyColumn if needed
        ) {
            Column() {
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
                            onClick = { navController.navigate("created") },
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

                Text(text = "Credential", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))

            }
            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFFFFFFF))
            ){
                item {
                    Box() {
                        Text(
                            text = "Untuk menjaga keamanan dan integritas setiap suara yang diberikan, pengguna diharuskan mengisi beberapa kredensial penting sebelum memberikan suara. Silakan periksa apa saja yang harus diisi oleh pengguna.",
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                            textAlign = TextAlign.Justify
                        )
                    }
                }
                item {
                    var isContentVisible by remember { mutableStateOf(false) }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        mandatoryCredentials.forEach { credential ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 25.dp)
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = null,
                                    enabled = false
                                )
                                Text(
                                    text = credential.nameTagType,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        // Add other rows as needed
                        additionalCredentials.forEachIndexed { index, credential ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = {
                                        // Hapus kredensial tambahan
                                        additionalCredentials = additionalCredentials
                                            .filterIndexed { i, _ -> i != index }
                                    },
                                    enabled = true,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFF008753),
                                        checkmarkColor = Color.White
                                    ),
                                )
                                Text(
                                    text = credential.nameTagType,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.offset(x = (-3).dp, y = 0.dp)
                                )
                            }
                        }

                        // Button to add new attributes
                        if (additionalCredentials.size < maxAttributes) {
                            Button(
                                onClick = { showAddCredentialDialog = true },
                                modifier = Modifier.padding(start = 25.dp, top = 10.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
                            ) {
                                Text(text = "Tambah Atribut Lain")
                            }
                        }
                        if (showAddCredentialDialog) {
                            AlertDialog(
                                onDismissRequest = {
                                    showAddCredentialDialog = false
                                    newCredentialName = ""
                                    newCredentialMaxInput = ""
                                },
                                title = { Text("Tambah Kredensial Baru") },
                                text = {
                                    Column {
                                        OutlinedTextField(
                                            value = newCredentialName,
                                            onValueChange = { newCredentialName = it },
                                            label = { Text("Nama Kredensial") }
                                        )
                                        OutlinedTextField(
                                            value = newCredentialMaxInput,
                                            onValueChange = {
                                                // Hanya terima input numerik
                                                newCredentialMaxInput = it.filter { char -> char.isDigit() }
                                            },
                                            label = { Text("Maksimal Input") }
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            // Validasi input
                                            if (newCredentialName.isNotBlank() &&
                                                newCredentialMaxInput.isNotBlank()) {

                                                val newCredential =
                                                    VoteElection.Credential.CredentialType(
                                                        nameTagType = newCredentialName,
                                                        typeTag = newCredentialName.lowercase()
                                                            .replace(" ", "_"),
                                                        maxInput = newCredentialMaxInput.toInt(),
                                                        isMandatory = true
                                                    )

                                                additionalCredentials = additionalCredentials + newCredential

                                                // Reset dialog
                                                showAddCredentialDialog = false
                                                newCredentialName = ""
                                                newCredentialMaxInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
                                    ) {
                                        Text("Tambah")
                                    }
                                }
                            )
                        }
                    }
                }
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(end = 20.dp, bottom = 20.dp, )) {
                        Button(
                            onClick = {
                                // Gabungkan kredensial wajib dan tambahan
                                val allCredentials = mandatoryCredentials + additionalCredentials

                                // Simpan ke dalam bundle atau kirim sebagai argumen
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("election_credentials", allCredentials.toTypedArray())

                                // Navigasi ke halaman berikutnya
                                navController.navigate("election")
                            },
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
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
