import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.votex.ForgotPasswordDialog
import com.example.votex.R
import com.example.votex.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

private lateinit var auth: FirebaseAuth
private lateinit var database: FirebaseDatabase
@Composable
fun RegisterPage(navController: NavController) {
    val mContext = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    // Fungsi untuk melakukan registrasi
    fun signup(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        if (password != repeatPassword) {
            Toast.makeText(mContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Mengambil user ID dari FirebaseAuth
                    val userId = auth.currentUser?.uid

                    // Membuat data pengguna baru dengan nilai default
                    val newUser = User(
                        profilePhoto = "",
                        name = "",
                        email = email,
                        birthDate = "",
                        place = "",
                        hasVotedAt = emptyList()
                    )

                    // Menyimpan data user ke Firebase Realtime Database
                    userId?.let {
                        database.reference.child("users").child(it).setValue(newUser)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(mContext, "User registered: $email", Toast.LENGTH_SHORT).show()
                                    navController.navigate("created") // Arahkan ke halaman utama setelah registrasi sukses
                                } else {
                                    Toast.makeText(mContext, "Failed to save user data: ${dbTask.exception}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    // Menangani error saat pendaftaran gagal
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(mContext, "Registration failed: ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    // Konfigurasi untuk Firebase dan Database
    auth = FirebaseAuth.getInstance()
    database = FirebaseDatabase.getInstance()

    // Konfigurasi Google Sign-In
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(mContext.getString(R.string.default_web_client_id)) // Ini harus mengarah ke Web Client ID dari konsol Firebase.
        .requestEmail()
        .build()
    val googleClient = GoogleSignIn.getClient(mContext, googleSignInOptions)
    val signInIntent = googleClient.signInIntent

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            if (task == null) {
                Toast.makeText(mContext, "Google Sign-In failed: task is null", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            val idToken = task?.result?.idToken
            if (idToken.isNullOrEmpty()) {
                Toast.makeText(mContext, "Unable to get ID Token", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }

            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Firebase authentication
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        val user = authResult.result?.user
                        if (user != null) {
                            val userRef = database.reference.child("users").child(user.uid)

                            userRef.get().addOnSuccessListener { snapshot ->
                                if (!snapshot.exists()) {
                                    val newUser = User(
                                        profilePhoto = "",
                                        name = user.displayName ?: "",
                                        email = user.email ?: "",
                                        birthDate = "",
                                        place = "",
                                        hasVotedAt = emptyList()
                                    )
                                    userRef.setValue(newUser)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(mContext, "Google sign-in successful", Toast.LENGTH_SHORT).show()
                                                navController.navigate("created")
                                            }
                                        }
                                } else {
                                    Toast.makeText(mContext, "Welcome back!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("created")
                                }
                            }
                        }
                    } else {
                        Toast.makeText(mContext, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(mContext, "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    Image(
        painter = painterResource(id = R.drawable.ic_main_background),
        contentDescription = "Logo",
        modifier = Modifier.fillMaxSize()
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_53),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )

        Text(
            text = "Fast, easy and secure digital\nvoting platform",
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            value = email,
            label = { Text("Alamat Email") },
            onValueChange = { newText -> email = newText }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            value = password,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { newText -> password = newText }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(10.dp)),
            value = repeatPassword,
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { repeatPassword = it }
        )

        Button(
            modifier = Modifier
                .size(width = 450.dp, height = 60.dp)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .clip(RoundedCornerShape(50.dp)),
            colors = ButtonDefaults.buttonColors(Color(0xFF008753)),
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (password == repeatPassword) {
                        signup(email, password)
                    } else {
                        Toast.makeText(mContext, "Passwords do not match", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(mContext, "Please fill all the fields", Toast.LENGTH_LONG).show()
                }
            },
        ) {
            Text(text = "DAFTAR")
        }

        var isForgotPasswordDialogOpen by remember { mutableStateOf(false) }

        TextButton(
            modifier = Modifier.padding(10.dp),
            onClick = {isForgotPasswordDialogOpen = true }

        ){
            Text("Forgot Password", color = Color.White,textAlign = TextAlign.End)
        }
        ForgotPasswordDialog(
            isDialogOpen = isForgotPasswordDialogOpen,
            onDismiss = { isForgotPasswordDialogOpen = false },
            context = LocalContext.current
        )

        Spacer(modifier = Modifier.height(150.dp))
        Row {
            Text(
                text = "Belum punya akun? ",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                style = TextStyle.Default
            )
            Text(
                modifier = Modifier.clickable { navController.navigate("login") },
                text = "Masuk sekarang!",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                style = TextStyle(textDecoration = TextDecoration.Underline)
            )
        }

        Button(
            onClick = { launcher.launch(signInIntent) },
            modifier = Modifier
                .width(250.dp)
                .height(90.dp)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pngwing_com),
                    contentDescription = "Google logo",
                    modifier = Modifier.size(35.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Google")
            }
        }
    }
}


@Composable
fun ForgotPasswordDialog(
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    context: Context
) {
    fun forgotPassword(email: String, context: Context) {
        if (email.isEmpty()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error sending password reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    var email by remember { mutableStateOf("") }

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Please enter your email to reset your password:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    forgotPassword(email, context)
                    onDismiss()
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
