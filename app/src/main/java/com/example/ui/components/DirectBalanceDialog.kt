package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NetworkType
import com.example.ui.util.CurrencyFormatter

@Composable
fun DirectBalanceDialog(
    network: NetworkType?, // null means cash drawer
    currentBalance: Double,
    onDismiss: () -> Unit,
    onConfirm: (newBalance: Double) -> Unit
) {
    var balanceText by remember { mutableStateOf(currentBalance.toInt().toString()) }
    val title = if (network == null) "Rekebisha Fedha Taslimu (Cash)" else "Rekebisha Float ya ${network.displayName}"
    val subtitle = if (network == null) "Ingiza kiasi halisi cha fedha taslimu kwenye droo" else "Ingiza salio halisi la float kutoka kwenye simu/USSD"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Salio la Sasa: ${CurrencyFormatter.formatTZS(currentBalance)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Salio Jipya (TZS)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("TZS ", fontWeight = FontWeight.Bold) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("direct_balance_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_direct_balance")
                    ) {
                        Text("Ghairi")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newAmount = balanceText.toDoubleOrNull() ?: currentBalance
                            onConfirm(newAmount)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("confirm_direct_balance")
                    ) {
                        Text("Sasisha Salio")
                    }
                }
            }
        }
    }
}
