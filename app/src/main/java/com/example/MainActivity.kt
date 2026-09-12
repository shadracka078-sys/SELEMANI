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
                        var isLoggedIn by remember { mutableStateOf(false) }

                        if (isLoggedIn) {
                            // Hapa tunaita WakalaApp() badala ya WakalaHomeScreen()
                            WakalaApp()
                        } else {
                            AuthScreen(
                                onAuthSuccess = {
                                    Toast.makeText(this@MainActivity, "Login Imefanikiwa!", Toast.LENGTH_SHORT).show()
                                    isLoggedIn = true
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
