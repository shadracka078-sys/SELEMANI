package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.abs

data class DailyClosingEntity(
    val id: Int = 0,
    val date: String,
    val amount: Double
)

@Composable
fun DailyClosingScreen() {
    var closingAmount by remember { mutableStateOf("0.0") }
    val closings = remember { mutableStateListOf<DailyClosingEntity>() }

    val amountValue = closingAmount.toDoubleOrNull() ?: 0.0
    // Kurekebisha abs() ambiguity kwa kuhakikisha aina ya data ni Double
    val difference = abs(amountValue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Funga Hesabu Za Siku",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = closingAmount,
            onValueChange = { closingAmount = it },
            label = { Text("Kiasi cha kufunga") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Tofauti: TSH $difference")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(closings) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${item.date}: TSH ${item.amount}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
