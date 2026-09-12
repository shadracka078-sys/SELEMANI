package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.google.firebase.FirebaseApp

// Import mafile yote ya UI yaliyopo kwenye folda ya ui
import com.example.ui.*

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
                            // Hapa tunaonyesha AdminDashboardScreen kutoka kwenye ui folder
                            AdminDashboardScreen(
                                onLogout = {
                                    isLoggedIn = false
                                    Toast.makeText(this@MainActivity, "Umetoka kwenye akaunti", Toast.LENGTH_SHORT).show()
                                }
                            )
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
