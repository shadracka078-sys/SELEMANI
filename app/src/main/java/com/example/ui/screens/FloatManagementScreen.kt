package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NetworkType
import com.example.data.model.TransactionType
import com.example.ui.WakalaViewModel
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.DirectBalanceDialog
import com.example.ui.util.CurrencyFormatter

@Composable
fun FloatManagementScreen(
    viewModel: WakalaViewModel,
    modifier: Modifier = Modifier
) {
    val cashDrawer by viewModel.cashDrawer.collectAsStateWithLifecycle()
    val floatBalances by viewModel.floatBalances.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var dialogInitialType by remember { mutableStateOf(TransactionType.BUY_FLOAT) }

    var showDirectEditDialog by remember { mutableStateOf(false) }
    var editNetwork by remember { mutableStateOf<NetworkType?>(null) }
    var editCurrentBalance by remember { mutableStateOf(0.0) }

    val currentCash = cashDrawer?.currentCash ?: 0.0
    val totalFloat = floatBalances.sumOf { it.currentBalance }
    val totalCapital = currentCash + totalFloat

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Usimamizi wa Float & Mtaji",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Float Balances & Capital Allocation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Total Capital Allocation Breakdown
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Mgawanyo wa Mtaji (Capital Distribution)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Jumla: ${CurrencyFormatter.formatTZS(totalCapital)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visual Progress ratio
                    if (totalCapital > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            // Cash bar
                            val cashRatio = (currentCash / totalCapital).toFloat().coerceIn(0f, 1f)
                            if (cashRatio > 0.01f) {
                                Box(
                                    modifier = Modifier
                                        .weight(cashRatio)
                                        .fillMaxSize()
                                        .background(Color(0xFF15803D))
                                )
                            }
                            // Networks
                            floatBalances.forEach { f ->
                                val ratio = (f.currentBalance / totalCapital).toFloat().coerceIn(0f, 1f)
                                if (ratio > 0.01f) {
                                    Box(
                                        modifier = Modifier
                                            .weight(ratio)
                                            .fillMaxSize()
                                            .background(Color(f.network.brandColorHex))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LegendItem(label = "Cash", color = Color(0xFF15803D), amount = currentCash, total = totalCapital)
                        floatBalances.forEach { f ->
                            LegendItem(
                                label = f.network.displayName,
                                color = Color(f.network.brandColorHex),
                                amount = f.currentBalance,
                                total = totalCapital
                            )
                        }
                    }
                }
            }
        }

        // Quick Rebalancing Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Buy Float Button
                Button(
                    onClick = {
                        dialogInitialType = TransactionType.BUY_FLOAT
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("float_mgmt_buy_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nunua Float Benki", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Float to Cash
                OutlinedButton(
                    onClick = {
                        dialogInitialType = TransactionType.SELL_FLOAT
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("float_mgmt_sell_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Float Kuwa Cash", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Cash Drawer Balance Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fedha Taslimu (Cash in Drawer)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Salio la Kuanzia: ${CurrencyFormatter.formatTZS(cashDrawer?.openingCash ?: 0.0)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyFormatter.formatTZS(currentCash),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF15803D)
                        )
                    }

                    IconButton(
                        onClick = {
                            editNetwork = null
                            editCurrentBalance = currentCash
                            showDirectEditDialog = true
                        },
                        modifier = Modifier.testTag("edit_cash_drawer_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Rekebisha", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Section Title: Float per Network
        item {
            Text(
                text = "Salio la Float kwa Kila Mtandao",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // List of Float Cards
        items(floatBalances.size) { index ->
            val f = floatBalances[index]
            val brandColor = Color(f.network.brandColorHex)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(brandColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = f.network.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = {
                                editNetwork = f.network
                                editCurrentBalance = f.currentBalance
                                showDirectEditDialog = true
                            },
                            modifier = Modifier.testTag("edit_float_${f.network.name}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Rekebisha", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Salio la Kuanzia Leo:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormatter.formatTZS(f.openingBalance),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Salio Lililopo Sasa:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormatter.formatTZS(f.currentBalance),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = brandColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Difference from opening
                    val diff = f.currentBalance - f.openingBalance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mabadiliko ya leo:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (diff >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = CurrencyFormatter.formatSignedTZS(diff),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (diff >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Reset Balances Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weka Upya Salio la Kuanzia Shift (Reset Opening)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ikiwa unataka kuanza shift mpya bila kufunga siku rasmi, unaweza kusawazisha salio la kuanzia liwe sawa na salio lililopo sasa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.resetDayBalances() },
                        modifier = Modifier.testTag("reset_shift_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sawazisha Salio la Kuanzia na Sasa")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Transaction Dialog
    if (showAddDialog) {
        AddTransactionDialog(
            initialType = dialogInitialType,
            onDismiss = { showAddDialog = false },
            onSubmit = { type, network, amount, commission, phone, name, ref, notes ->
                viewModel.recordTransaction(type, network, amount, commission, phone, name, ref, notes)
            }
        )
    }

    // Direct Edit Balance Dialog
    if (showDirectEditDialog) {
        DirectBalanceDialog(
            network = editNetwork,
            currentBalance = editCurrentBalance,
            onDismiss = { showDirectEditDialog = false },
            onConfirm = { newAmount ->
                if (editNetwork == null) {
                    viewModel.adjustCashDrawerDirectly(newAmount)
                } else {
                    viewModel.adjustFloatDirectly(editNetwork!!, newAmount)
                }
            }
        )
    }
}

@Composable
fun LegendItem(
    label: String,
    color: Color,
    amount: Double,
    total: Double
) {
    val percentage = if (total > 0) ((amount / total) * 100).toInt() else 0
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
        }
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
