package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            FirebaseApp.initializeApp(this)
            
            setContent {
                MaterialTheme {
                    Surface {
                        // Variable ya kuangalia kama mtumiaji ameingia (logged in)
                        var isLoggedIn by remember { mutableStateOf(false) }

                        if (isLoggedIn) {
                            // Onyesha Skrini ya Kuu/Dashboard baada ya Login
                            AdminDashboardScreen() 
                        } else {
                            // Onyesha Skrini ya Login
                            AuthScreen(
                                onAuthSuccess = {
                                    Toast.makeText(this@MainActivity, "Login Imefanikiwa!", Toast.LENGTH_SHORT).show()
                                    isLoggedIn = true // Hii inahamisha skrini kwenda mbele
                                }
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Hitilafu: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
