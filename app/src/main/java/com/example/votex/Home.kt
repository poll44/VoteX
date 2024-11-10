//package com.example.myapplication
//
//import android.accounts.OnAccountsUpdateListener
//import android.widget.Checkable
//import android.widget.Toast
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.CheckboxDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.modifier.modifierLocalMapOf
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalInspectionMode
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.auth
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.database
//
//private lateinit var auth: FirebaseAuth
//private lateinit var database: FirebaseDatabase
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Home(navController: NavController, modifier: Modifier = Modifier) {
//    var taskName by remember { mutableStateOf("") }
//    var tasks by remember { mutableStateOf(listOf<Task>()) }
//    var userEmail: String = ""
//    var userId: String = ""
//    val mContext = LocalContext.current
//
//    fun saveTask(){
//        val newTask = Task(name = taskName, isCompleted = false)
//        val myRef = database.getReference("notes").child(auth.uid!!).push()
//        myRef.setValue(newTask).addOnSuccessListener{
//            taskName = ""
//            Toast
//                .makeText(mContext, "New task added successfully", Toast.LENGTH_LONG)
//                .show()
//        }
//    }
//    if (!LocalInspectionMode.current){
//        auth = Firebase.auth
//        val currentUser = auth.currentUser
//        if(currentUser != null){
//            userEmail = currentUser.email.toString()
//            userId = currentUser.uid
//        }
//        database = Firebase.database
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xffcb0074)),
//                title = {
//                    Text(
//                        text = "TODO APP",
//                        color = Color.White,
//                        textAlign = TextAlign.Center,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.fillMaxWidth(),
//                    )
//                }
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            Text(
//                text = "Selamat datang.",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 20.dp, top = 10.dp), color = Color.Gray
//            )
//            Text(
//                text = userEmail,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp),
//                fontSize = 25.sp,
//                color = Color.Gray
//            )
//            Text(
//                text = userId,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp),
//                fontSize = 25.sp,
//                color = Color.Gray
//            )
//            Button(
//                onClick = {
//                    Firebase.auth.signOut()
//                    navController.navigate("login")
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp, vertical = 5.dp)
//                    .height(40.dp),
//                shape = RoundedCornerShape(8.dp),
//                colors = ButtonDefaults.buttonColors(Color(0xffcb0074))
//            ) {
//                Text(text = "Logout", fontWeight = FontWeight.Bold)
//            }
//            TextField(modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp, vertical = 10.dp)
//                .height(50.dp)
//                .clip(RoundedCornerShape(8.dp)),
//                value = taskName, onValueChange = {taskName = it })
//            Button(
//                onClick = {
//                    if(taskName.isNotEmpty()){
//                        saveTask()
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp, vertical = 5.dp)
//                    .height(40.dp),
//                shape = RoundedCornerShape(8.dp),
//                colors = ButtonDefaults.buttonColors(Color(0xffcb0072))
//            ){
//                Text(text = "Save", fontWeight = FontWeight.Bold)
//            }
//
//            LazyColumn (
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp, vertical = 5.dp)
//                    .weight(1f)
//            ){
////                items(tasks.size) { task ->
////                    TaskItem(tasks.get(task)) { isCompleted ->
////                        // Update tasks list here
////                        tasks =
////                            tasks.map {it.copy(isCompleted = id (it.id == tasks.get(task).id) isCompleted else it.isCompleted)}
////
////                    } // Update with logic to update completion
////                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TaskItem(task: Task, onUpdateTask:(Boolean) -> Unit, onEditTask: ()-> Unit, onDeleteTask: ()-> Unit){
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically
//    ){
//        Checkbox(
//            checked = task.isCompleted,
//            onCheckedChange = { isChecked -> onUpdateTask(isChecked)},
//            colors = CheckboxDefaults.colors(Color(0xffcb0074))
//        )
//        Text(text = task.name,
//            modifier = Modifier
//                .padding(start = 8.dp)
//                .weight(1f))
//        IconButton(onEditTask) {
//            Icon(
//                painter = painterResource(id =
//
//                R.drawable.baseline_edit_24),
//
//                contentDescription = "Update"
//            )
//        }
//        IconButton(onClick = onDeleteTask) {
//            Icon(
//                painter = painterResource(id =
//
//                R.drawable.baseline_delete_24),
//
//                contentDescription = "Delete"
//            )
//        }
//    }
//
//}