package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class TodayStats(
    val totalTransactions: Int = 0,
    val totalAmount: Double = 0.0
)

@Composable
fun DashboardScreen() {
    val stats by remember { mutableStateOf(TodayStats()) }
    val recentTransactions = remember { mutableStateListOf<TransactionEntity>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Wakala Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Miamala ya Leo: ${stats.totalTransactions}")
                Text(text = "Jumla ya Kiasi: TSH ${stats.totalAmount}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recentTransactions) { transaction ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = transaction.title)
                        Text(text = transaction.amount)
                    }
                }
            }
        }
    }
}
