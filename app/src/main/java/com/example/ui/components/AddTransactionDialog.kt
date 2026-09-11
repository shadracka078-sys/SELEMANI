package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.NetworkType
import com.example.data.model.TransactionType
import com.example.ui.util.CurrencyFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTransactionDialog(
    initialType: TransactionType = TransactionType.DEPOSIT,
    initialNetwork: NetworkType = NetworkType.M_PESA,
    onDismiss: () -> Unit,
    onSubmit: (
        type: TransactionType,
        network: NetworkType?,
        amount: Double,
        commission: Double,
        customerPhone: String,
        customerName: String,
        referenceNumber: String,
        notes: String
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var selectedNetwork by remember { mutableStateOf<NetworkType?>(initialNetwork) }
    var amountText by remember { mutableStateOf("") }
    var commissionText by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var referenceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val amountValue = amountText.toDoubleOrNull() ?: 0.0

    // Auto-calculate suggested commission based on amount if not manually set
    fun updateAmount(newAmount: String) {
        amountText = newAmount
        val num = newAmount.toDoubleOrNull() ?: 0.0
        if (commissionText.isEmpty() || commissionText == "0") {
            val suggestedCommission = when (selectedType) {
                TransactionType.DEPOSIT -> when {
                    num in 1.0..10000.0 -> 150.0
                    num in 10001.0..50000.0 -> 350.0
                    num in 50001.0..100000.0 -> 600.0
                    num in 100001.0..300000.0 -> 1200.0
                    num > 300000.0 -> 2000.0
                    else -> 0.0
                }
                TransactionType.WITHDRAWAL -> when {
                    num in 1.0..10000.0 -> 250.0
                    num in 10001.0..50000.0 -> 550.0
                    num in 50001.0..100000.0 -> 900.0
                    num in 10001.0..300000.0 -> 1800.0
                    num > 300000.0 -> 2800.0
                    else -> 0.0
                }
                else -> 0.0
            }
            if (suggestedCommission > 0) {
                commissionText = suggestedCommission.toInt().toString()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rekodi Muamala",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Record Mobile Money Transaction",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_transaction_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Funga")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transaction Type Selector
                Text(
                    text = "Aina ya Muamala (Type)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TransactionType.entries.forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedType = type
                                if (type == TransactionType.EXPENSE) {
                                    selectedNetwork = null
                                } else if (selectedNetwork == null) {
                                    selectedNetwork = NetworkType.M_PESA
                                }
                            },
                            label = {
                                Text(
                                    text = "${type.title} (${type.swahiliTitle})",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                val icon = when (type) {
                                    TransactionType.DEPOSIT -> Icons.Default.ArrowDownward
                                    TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward
                                    TransactionType.BUY_FLOAT, TransactionType.SELL_FLOAT -> Icons.Default.SyncAlt
                                    else -> Icons.Default.Info
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (type) {
                                    TransactionType.DEPOSIT -> Color(0xFFE8F5E9)
                                    TransactionType.WITHDRAWAL -> Color(0xFFFFEBEE)
                                    TransactionType.BUY_FLOAT -> Color(0xFFE3F2FD)
                                    TransactionType.SELL_FLOAT -> Color(0xFFFFF3E0)
                                    TransactionType.EXPENSE -> Color(0xFFF3E5F5)
                                    TransactionType.COMMISSION -> Color(0xFFE0F2F1)
                                },
                                selectedLabelColor = when (type) {
                                    TransactionType.DEPOSIT -> Color(0xFF1B5E20)
                                    TransactionType.WITHDRAWAL -> Color(0xFFB71C1C)
                                    TransactionType.BUY_FLOAT -> Color(0xFF0D47A1)
                                    TransactionType.SELL_FLOAT -> Color(0xFFE65100)
                                    TransactionType.EXPENSE -> Color(0xFF4A148C)
                                    TransactionType.COMMISSION -> Color(0xFF004D40)
                                }
                            ),
                            modifier = Modifier.testTag("type_chip_${type.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Network Selector (hidden for pure expense)
                if (selectedType != TransactionType.EXPENSE) {
                    Text(
                        text = "Mtandao wa Pesa (Network)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NetworkType.entries.forEach { net ->
                            val isSelected = selectedNetwork == net
                            val brandColor = Color(net.brandColorHex)
                            val tagColor = Color(net.tagColorHex)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) brandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .background(if (isSelected) tagColor else MaterialTheme.colorScheme.surface)
                                    .clickable { selectedNetwork = net }
                                    .padding(vertical = 10.dp, horizontal = 4.dp)
                                    .testTag("network_button_${net.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(brandColor)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = net.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) brandColor else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { updateAmount(it.filter { ch -> ch.isDigit() || ch == '.' }) },
                    label = { Text("Kiasi cha Pesa (Amount in TZS) *") },
                    placeholder = { Text("Mfano: 50,000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    prefix = { Text("TZS ", fontWeight = FontWeight.Bold) }
                )

                // Quick Amount Presets
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("10000", "20000", "50000", "100000", "200000", "500000").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { updateAmount(preset) }
                        ) {
                            Text(
                                text = "+${CurrencyFormatter.formatNumber(preset.toDouble())}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Commission (for Deposit / Withdrawal / Commission)
                if (selectedType == TransactionType.DEPOSIT || selectedType == TransactionType.WITHDRAWAL || selectedType == TransactionType.COMMISSION) {
                    OutlinedTextField(
                        value = commissionText,
                        onValueChange = { commissionText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Kamisheni / Ada Uliyopata (Commission TZS)") },
                        placeholder = { Text("0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("commission_input"),
                        prefix = { Text("TZS ", color = Color(0xFF00796B), fontWeight = FontWeight.SemiBold) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Customer Phone & Name (for deposits/withdrawals)
                if (selectedType == TransactionType.DEPOSIT || selectedType == TransactionType.WITHDRAWAL) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Simu ya Mteja") },
                            placeholder = { Text("0712 345 678") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("customer_phone_input")
                        )
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Jina la Mteja") },
                            placeholder = { Text("Juma Bakari") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("customer_name_input")
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Reference Number & Notes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = referenceNumber,
                        onValueChange = { referenceNumber = it.uppercase() },
                        label = { Text("Namba ya Muamala / Ref") },
                        placeholder = { Text("MP892341") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reference_input")
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Maelezo (Notes)") },
                        placeholder = { Text("Hiari") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("notes_input")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Impact Preview
                if (amountValue > 0) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Mabadiliko ya Salio (Balance Effect):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val netName = selectedNetwork?.displayName ?: "Network"
                            when (selectedType) {
                                TransactionType.DEPOSIT -> {
                                    Text(
                                        text = "• Fedha Taslimu (Cash in drawer): +${CurrencyFormatter.formatTZS(amountValue)} (Umeongeza)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1B5E20),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "• Salio la Float ($netName): -${CurrencyFormatter.formatTZS(amountValue)} (Limepungua)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB71C1C),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                TransactionType.WITHDRAWAL -> {
                                    Text(
                                        text = "• Fedha Taslimu (Cash in drawer): -${CurrencyFormatter.formatTZS(amountValue)} (Umetoa kwa mteja)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB71C1C),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "• Salio la Float ($netName): +${CurrencyFormatter.formatTZS(amountValue)} (Limeongezeka)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1B5E20),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                TransactionType.BUY_FLOAT -> {
                                    Text(
                                        text = "• Fedha Taslimu (Cash in drawer): -${CurrencyFormatter.formatTZS(amountValue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB71C1C)
                                    )
                                    Text(
                                        text = "• Salio la Float ($netName): +${CurrencyFormatter.formatTZS(amountValue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                                TransactionType.SELL_FLOAT -> {
                                    Text(
                                        text = "• Fedha Taslimu (Cash in drawer): +${CurrencyFormatter.formatTZS(amountValue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "• Salio la Float ($netName): -${CurrencyFormatter.formatTZS(amountValue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB71C1C)
                                    )
                                }
                                TransactionType.EXPENSE -> {
                                    Text(
                                        text = "• Fedha Taslimu (Cash in drawer): -${CurrencyFormatter.formatTZS(amountValue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB71C1C)
                                    )
                                }
                                TransactionType.COMMISSION -> {
                                    Text(
                                        text = "• Kamisheni: +${CurrencyFormatter.formatTZS(amountValue)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF004D40)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Error message
                AnimatedVisibility(visible = errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_transaction_button")
                    ) {
                        Text("Ghairi")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (amountValue <= 0) {
                                errorMessage = "Tafadhali weka kiasi halali cha fedha."
                                return@Button
                            }
                            val commissionVal = commissionText.toDoubleOrNull() ?: 0.0
                            onSubmit(
                                selectedType,
                                selectedNetwork,
                                amountValue,
                                commissionVal,
                                customerPhone,
                                customerName,
                                referenceNumber,
                                notes
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (selectedType) {
                                TransactionType.DEPOSIT -> Color(0xFF007953)
                                TransactionType.WITHDRAWAL -> Color(0xFFC62828)
                                TransactionType.BUY_FLOAT -> Color(0xFF1565C0)
                                TransactionType.SELL_FLOAT -> Color(0xFFE65100)
                                TransactionType.EXPENSE -> Color(0xFF6A1B9A)
                                TransactionType.COMMISSION -> Color(0xFF00695C)
                            }
                        ),
                        modifier = Modifier.testTag("save_transaction_button")
                    ) {
                        Text("Hifadhi Muamala", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
