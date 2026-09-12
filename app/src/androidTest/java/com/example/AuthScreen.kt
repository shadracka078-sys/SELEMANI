package com.example

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

enum class AuthMode { LOGIN, SIGN_UP, FORGOT_PASSWORD }

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current

    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (mode) {
                    AuthMode.LOGIN -> "SELEMANI WAKALA - LOGIN"
                    AuthMode.SIGN_UP -> "AJILI WAKALA MPYA (SIGN UP)"
                    AuthMode.FORGOT_PASSWORD -> "REJESHA NENOSIRI"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Barua Pepe (Email)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (mode != AuthMode.FORGOT_PASSWORD) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Nenosiri (Password)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Tafadhali weka Email", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        when (mode) {
                            AuthMode.LOGIN -> {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Umeingia salama!", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Kosa: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                            }
                            AuthMode.SIGN_UP -> {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Akaunti imetengenezwa!", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Kosa: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                            }
                            AuthMode.FORGOT_PASSWORD -> {
                                auth.sendPasswordResetEmail(email)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Link ya kubadili password imetumwa kwenye Email yako!", Toast.LENGTH_LONG).show()
                                        mode = AuthMode.LOGIN
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Kosa: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (mode) {
                            AuthMode.LOGIN -> "Ingia (Log In)"
                            AuthMode.SIGN_UP -> "Sajili Akaunti"
                            AuthMode.FORGOT_PASSWORD -> "Tuma Link ya Password"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tumia chaguzi hizi kubadilisha kati ya Login, Sign Up na Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (mode == AuthMode.LOGIN) {
                    TextButton(onClick = { mode = AuthMode.FORGOT_PASSWORD }) {
                        Text("Umesahau Password?")
                    }
                    TextButton(onClick = { mode = AuthMode.SIGN_UP }) {
                        Text("Sajili Akaunti")
                    }
                } else {
                    TextButton(onClick = { mode = AuthMode.LOGIN }) {
                        Text("Rudi Kuingia (Log In)")
                    }
                }
            }
        }
    }
}
