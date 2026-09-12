package com.example

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

data class TransactionItem(
    val id: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val network: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    val context = LocalContext.current
    var transactions by remember { mutableStateOf<List<TransactionItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            // Tunasoma Firestore bila kutumia orderBy kwanza ili kuzuia Crash ya Indexing
            db.collection("transactions")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        isLoading = false
                        errorMessage = "Kosa la Firestore: ${error.localizedMessage}"
                        return@addSnapshotListener
                    }
                    
                    if (snapshot != null) {
                        val list = mutableListOf<TransactionItem>()
                        for (doc in snapshot.documents) {
                            try {
                                // Tunasoma data moja baada ya nyingine kwa usalama (Manual parsing)
                                val amountVal = doc.getDouble("amount") ?: doc.getLong("amount")?.toDouble() ?: 0.0
                                val typeVal = doc.getString("type") ?: ""
                                val networkVal = doc.getString("network") ?: ""
                                val timeVal = doc.getLong("timestamp") ?: 0L

                                list.add(
                                    TransactionItem(
                                        id = doc.id,
                                        amount = amountVal,
                                        type = typeVal,
                                        network = networkVal,
                                        timestamp = timeVal
                                    )
                                )
                            } catch (e: Exception) {
                                // Kama document moja ina shida, inarukwa bila kucrashisha app yote
                            }
                        }
                        // Panga kulingana na muda kwa usalama
                        transactions = list.sortedByDescending { it.timestamp }
                        isLoading = false
                    }
                }
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Hitilafu: ${e.localizedMessage}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SELEMANI DASHBOARD") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Toka", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Kosa lisilojulikana",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                transactions.isEmpty() -> {
                    Text(
                        text = "Hakuna miamala iliyopatikana kwenye Firestore bado.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${item.network.ifBlank { "N/A" }.uppercase()} - ${item.type.ifBlank { "N/A" }.uppercase()}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Kiasi: TZS ${item.amount}")
                                

}
                            }
                        }
                    }
                }
            }
        }
    }
}
