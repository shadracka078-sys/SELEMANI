package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Model rahisi ya Miamala
data class TransactionEntity(
    val id: Int,
    val title: String,
    val amount: String
)

@Composable
fun TransactionsScreen() {
    var searchText by remember { mutableStateOf("") }
    
    val transactions = remember {
        mutableStateListOf(
            TransactionEntity(1, "Kutoa Helo - Vodacom", "TSH 50,000"),
            TransactionEntity(2, "Kuweka - Tigo Pesa", "TSH 100,000")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Miamala Ya Wakala",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // OutlinedTextField iliyorekebishwa kutumia String badala ya Variable mismatch error
        OutlinedTextField(
            value = searchText,
            onValueChange = { newValue -> searchText = newValue },
            label = { Text("Tafuta Muamala") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
