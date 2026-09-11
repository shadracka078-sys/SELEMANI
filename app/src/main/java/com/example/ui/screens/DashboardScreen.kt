package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FloatBalanceEntity
import com.example.data.model.NetworkType
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.TodayStats
import com.example.ui.WakalaViewModel
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.DirectBalanceDialog
import com.example.ui.components.NetworkBadge
import com.example.ui.components.TransactionListItem
import com.example.ui.components.TransactionTypeBadge
import com.example.ui.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WakalaViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToDailyClosing: () -> Unit,
    onNavigateToFloatManagement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cashDrawer by viewModel.cashDrawer.collectAsStateWithLifecycle()
    val floatBalances by viewModel.floatBalances.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val todayStats by viewModel.todayStats.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var dialogInitialType by remember { mutableStateOf(TransactionType.DEPOSIT) }
    var dialogInitialNetwork by remember { mutableStateOf(NetworkType.M_PESA) }

    var showDirectBalanceDialog by remember { mutableStateOf(false) }
    var directEditNetwork by remember { mutableStateOf<NetworkType?>(null) }
    var directEditCurrentBalance by remember { mutableStateOf(0.0) }

    val currentCash = cashDrawer?.currentCash ?: 0.0
    val totalFloat = floatBalances.sumOf { it.currentBalance }
    val totalMtaji = currentCash + totalFloat

    val recentTransactions = allTransactions.take(5)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Wakala Hero Card - Mtaji Wote (Total Capital)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_total_capital_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF005234),
                                    Color(0xFF007953),
                                    Color(0xFF0F382C)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "MTAJI WOTE WA WAKALA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFA7F3D0),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "Total Working Capital (Cash + Floats)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFD1FAE5).copy(alpha = 0.8f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF004D34).copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Hai",
                                        color = Color(0xFF34D399),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = CurrencyFormatter.formatTZS(totalMtaji),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.testTag("total_capital_amount")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub-breakdown: Cash in drawer vs Total Float
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cash in Drawer pill
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        directEditNetwork = null
                                        directEditCurrentBalance = currentCash
                                        showDirectBalanceDialog = true
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Payments,
                                            contentDescription = null,
                                            tint = Color(0xFFFDE68A),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Fedha Taslimu (Cash)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFDE68A),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyFormatter.formatTZS(currentCash),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Total Float pill
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToFloatManagement() },
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            tint = Color(0xFF93C5FD),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Float Yote (Digital)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF93C5FD),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyFormatter.formatTZS(totalFloat),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Column {
                Text(
                    text = "Miamala ya Haraka (Quick Actions)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Deposit Button
                    Button(
                        onClick = {
                            dialogInitialType = TransactionType.DEPOSIT
                            dialogInitialNetwork = NetworkType.M_PESA
                            showAddDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("quick_deposit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007953)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Kuweka", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Deposit", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Withdrawal Button
                    Button(
                        onClick = {
                            dialogInitialType = TransactionType.WITHDRAWAL
                            dialogInitialNetwork = NetworkType.M_PESA
                            showAddDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("quick_withdrawal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Kutoa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Withdrawal", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Buy Float Button
                    OutlinedButton(
                        onClick = {
                            dialogInitialType = TransactionType.BUY_FLOAT
                            showAddDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("quick_buy_float_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nunua Float", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    // Daily Closing Button
                    Button(
                        onClick = onNavigateToDailyClosing,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("quick_daily_closing_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.LockClock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Funga Siku", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Network Float Balances Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Salio la Float kwa Mitandao",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "M-Pesa, Airtel Money, Tigo Pesa, HaloPesa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(
                    onClick = onNavigateToFloatManagement,
                    modifier = Modifier.testTag("manage_floats_button")
                ) {
                    Text("Dhibiti", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Float Cards for each network
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NetworkType.entries.forEach { network ->
                    val floatItem = floatBalances.find { it.network == network }
                    val balance = floatItem?.currentBalance ?: 0.0
                    val threshold = floatItem?.lowBalanceThreshold ?: 50000.0
                    val isLow = balance < threshold

                    NetworkFloatCard(
                        network = network,
                        balance = balance,
                        isLowBalance = isLow,
                        threshold = threshold,
                        onDepositClick = {
                            dialogInitialType = TransactionType.DEPOSIT
                            dialogInitialNetwork = network
                            showAddDialog = true
                        },
                        onWithdrawClick = {
                            dialogInitialType = TransactionType.WITHDRAWAL
                            dialogInitialNetwork = network
                            showAddDialog = true
                        },
                        onCardClick = {
                            directEditNetwork = network
                            directEditCurrentBalance = balance
                            showDirectBalanceDialog = true
                        }
                    )
                }
            }
        }

        // Today's Performance Summary Row
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Muhtasari wa Leo (Today's Summary)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${todayStats.totalTransactions} Miamala",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Deposits Sum
                        StatTile(
                            title = "Weka (Deposits)",
                            amount = CurrencyFormatter.formatTZS(todayStats.depositsAmount),
                            subtitle = "${todayStats.depositsCount} Miamala",
                            tint = Color(0xFF15803D),
                            modifier = Modifier.weight(1f)
                        )
                        // Withdrawals Sum
                        StatTile(
                            title = "Toa (Withdraws)",
                            amount = CurrencyFormatter.formatTZS(todayStats.withdrawalsAmount),
                            subtitle = "${todayStats.withdrawalsCount} Miamala",
                            tint = Color(0xFFB91C1C),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Commission
                        StatTile(
                            title = "Kamisheni (Profit)",
                            amount = CurrencyFormatter.formatTZS(todayStats.commissionsAmount),
                            subtitle = "Faida ya leo",
                            tint = Color(0xFF0F766E),
                            modifier = Modifier.weight(1f)
                        )
                        // Expenses
                        StatTile(
                            title = "Matumizi (Expense)",
                            amount = CurrencyFormatter.formatTZS(todayStats.expensesAmount),
                            subtitle = "Gharama za leo",
                            tint = Color(0xFF7E22CE),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Daily Closing Action Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDailyClosing() }
                    .testTag("banner_daily_closing")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD97706)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LockClock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kufunga Hesabu ya Siku",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "Linganisha fedha taslimu na float kulingana na miamala yote.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF92400E)
                        )
                    }
                    Icon(
                        Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Recent Transactions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Miamala ya Hivi Karibuni",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = onNavigateToTransactions,
                    modifier = Modifier.testTag("view_all_transactions_button")
                ) {
                    Text("Tazama Zote", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (recentTransactions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hakuna muamala uliorekodiwa bado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Rekodi Muamala wa Kwanza")
                        }
                    }
                }
            }
        } else {
            items(recentTransactions, key = { it.id }) { tx ->
                TransactionListItem(transaction = tx)
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
            initialNetwork = dialogInitialNetwork,
            onDismiss = { showAddDialog = false },
            onSubmit = { type, network, amount, commission, phone, name, ref, notes ->
                viewModel.recordTransaction(type, network, amount, commission, phone, name, ref, notes)
            }
        )
    }

    // Direct Edit Balance Dialog
    if (showDirectBalanceDialog) {
        DirectBalanceDialog(
            network = directEditNetwork,
            currentBalance = directEditCurrentBalance,
            onDismiss = { showDirectBalanceDialog = false },
            onConfirm = { newBal ->
                if (directEditNetwork == null) {
                    viewModel.adjustCashDrawerDirectly(newBal)
                } else {
                    viewModel.adjustFloatDirectly(directEditNetwork!!, newBal)
                }
            }
        )
    }
}

@Composable
fun NetworkFloatCard(
    network: NetworkType,
    balance: Double,
    isLowBalance: Boolean,
    threshold: Double,
    onDepositClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandColor = Color(network.brandColorHex)
    val tagBg = Color(network.tagColorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("float_card_${network.name}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(brandColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = network.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tagBg
                    ) {
                        Text(
                            text = network.brandName,
                            style = MaterialTheme.typography.labelSmall,
                            color = brandColor,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLowBalance) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEBEE)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Float Ndogo!",
                                    color = Color(0xFFC62828),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = onCardClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Badilisha",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Salio la Float:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.formatTZS(balance),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLowBalance) Color(0xFFC62828) else brandColor
                    )
                }

                // Quick buttons for this specific network
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.clickable { onDepositClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = Color(0xFF1B5E20),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Weka",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.clickable { onWithdrawClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color(0xFFB71C1C),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Toa",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFB71C1C),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatTile(
    title: String,
    amount: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

