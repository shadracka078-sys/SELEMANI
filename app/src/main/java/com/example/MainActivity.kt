package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Anzisha Firebase kwa usalama kabla ya kufungua screen
            FirebaseApp.initializeApp(this)
            
            setContent {
                MaterialTheme {
                    Surface {
                        AuthScreen(
                            onAuthSuccess = {
                                Toast.makeText(this, "Login Imefanikiwa!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Kama kuna kosa badala ya app kuzima, itaonyesha ujumbe huu
            Toast.makeText(this, "Kosa la kuanza: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
