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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialPage(navController: NavController) {
    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("NIM", "NIK", "Passport ID")
    var maxInput by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isChecked1 by remember { mutableStateOf(false) }
    var isChecked2 by remember { mutableStateOf(false) }
    var isChecked3 by remember { mutableStateOf(false) }
    var isChecked4 by remember { mutableStateOf(false) }
    var isChecked5 by remember { mutableStateOf(false) }

    if (!LocalInspectionMode.current){
        auth = Firebase.auth
        database = Firebase.database
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

            Text(text = "Kredensial", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFFFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFFFFFFFF))
                ) {
                    Text(
                        text = "Untuk menjaga keamanan dan integritas setiap suara yang diberikan, pengguna diharuskan mengisi beberapa kredensial penting sebelum memberikan suara. Silakan periksa apa saja yang harus diisi oleh pengguna.",
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                        textAlign = TextAlign.Justify
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isChecked1,
                        onCheckedChange = { isChecked1 = it}
                        )
                    Text(text = "Alamat Email", fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked2,
                                onCheckedChange = { isChecked2 = it }
                            )
                            Text(
                                text = "Nomor Identitas", fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp)
                            )
                        }

                        if(isChecked2) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedOption,
                                onValueChange = { selectedOption = it },
                                readOnly = true,
                                label = { Text("Jenis nomor identitas") },
                                placeholder = { Text("Contoh: NIM, NIK, nomor ID paspor") },
                                modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedOption = option
                                            expanded = false
                                        },
                                        text = { Text(option) }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = maxInput,
                            onValueChange = { maxInput = it },
                            label = { Text("Input maksimal") },
                            placeholder = { Text("Contoh: 12") },
                            modifier = Modifier.fillMaxWidth().padding(start = 40.dp)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isChecked3,
                        onCheckedChange = { isChecked3 = it}
                    )
                    Text(text = "Nama Lengkap", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isChecked4,
                        onCheckedChange = { isChecked4 = it}
                    )
                    Text(text = "Tanggal Lahir", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isChecked5,
                        onCheckedChange = { isChecked5 = it}
                    )
                    Text(text = "Alamat", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Button(
                        onClick = { navController.navigate("election") },
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