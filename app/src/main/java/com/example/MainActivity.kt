package com.example.selemani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.WakalaMainDashboard // Au jina la Compose function ya Dashboard yako

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // Hapa tunatengeneza Switch ya Login
            var isLoggedIn by remember { mutableStateOf(false) }

            if (!isLoggedIn) {
                // 1. Onyesha Skrini ya Login Kwanza
                LoginScreen(
                    onLoginSuccess = {
                        isLoggedIn = true // Ukibonyeza Login inabadilisha kwenda Dashboard
                    }
                )
            } else {
                // 2. Onyesha Skrini ya Dashboard yako yote hapa
                WakalaMainDashboard()
            }
        }
    }
}
