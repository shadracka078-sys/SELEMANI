package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.admin.AdminLoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Hapa tunatengeneza hali (state) ya kuangalia kama ameingia au bado
                    var isAdminLoggedIn by remember { mutableStateOf(false) }

                    if (!isAdminLoggedIn) {
                        // Kama bado hajaingia, mnyeshee skrini ya PIN
                        AdminLoginScreen(
                            onLoginSuccess = {
                                isAdminLoggedIn = true
                            }
                        )
                    } else {
                        // Kama ameingia kwa mafanikio, kwa sasa tumwekee ujumbe wa Karibu kwenye Dashboard
                        // Hapa baadaye tutaweka ile Dashboard ya Firestore inayovuta data za miamala
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text(
                                text = "KARIBU SELEMANI ADMIN DASHBOARD!",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
