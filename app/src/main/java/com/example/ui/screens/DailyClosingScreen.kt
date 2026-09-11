package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DailyClosingEntity
import com.example.data.model.NetworkType
import com.example.ui.WakalaViewModel
import com.example.ui.components.NetworkBadge
import com.example.ui.util.CurrencyFormatter
import kotlin.math.abs

@Composable
fun DailyClosingScreen(
    viewModel: WakalaViewModel,
    modifier: Modifier = Modifier
) {
    val expectedClosing by viewModel.expectedClosing.collectAsStateWithLifecycle()
    val todayStats by viewModel.todayStats.collectAsStateWithLifecycle()
    val pastClosings by viewModel.allDailyClosings.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = Funga Leo, 1 = Historia

    // Counted Actual Inputs
    var actualCashText by remember { mutableStateOf("") }
    var actualMpesaText by remember { mutableStateOf("") }
    var actualAirtelText by remember { mutableStateOf("") }
    var actualTigoText by remember { mutableStateOf("") }
    var actualHaloText by remember { mutableStateOf("") }
    var closingNotes by remember { mutableStateOf("") }

    // Prefill with expected amounts on first load
    LaunchedEffect(expectedClosing) {
        if (actualCashText.isEmpty() && expectedClosing.expectedCash > 0) {
            actualCashText = expectedClosing.expectedCash.toInt().toString()
        }
        val mpesaExp = expectedClosing.expectedFloats[NetworkType.M_PESA] ?: 0.0
        if (actualMpesaText.isEmpty() && mpesaExp > 0) {
            actualMpesaText = mpesaExp.toInt().toString()
        }
        val airtelExp = expectedClosing.expectedFloats[NetworkType.AIRTEL_MONEY] ?: 0.0
        if (actualAirtelText.isEmpty() && airtelExp > 0) {
            actualAirtelText = airtelExp.toInt().toString()
        }
        val tigoExp = expectedClosing.expectedFloats[NetworkType.TIGO_PESA] ?: 0.0
        if (actualTigoText.isEmpty() && tigoExp > 0) {
            actualTigoText = tigoExp.toInt().toString()
        }
        val haloExp = expectedClosing.expectedFloats[NetworkType.HALOPESA] ?: 0.0
        if (actualHaloText.isEmpty() && haloExp > 0) {
            actualHaloText = haloExp.toInt().toString()
        }
    }

    // Variances calculations
    val actualCashVal = actualCashText.toDoubleOrNull() ?: 0.0
    val cashVariance = actualCashVal - expectedClosing.expectedCash

    val mpesaExp = expectedClosing.expectedFloats[NetworkType.M_PESA] ?: 0.0
    val actualMpesaVal = actualMpesaText.toDoubleOrNull() ?: 0.0
    val mpesaVariance = actualMpesaVal - mpesaExp

    val airtelExp = expectedClosing.expectedFloats[NetworkType.AIRTEL_MONEY] ?: 0.0
    val actualAirtelVal = actualAirtelText.toDoubleOrNull() ?: 0.0
    val airtelVariance = actualAirtelVal - airtelExp

    val tigoExp = expectedClosing.expectedFloats[NetworkType.TIGO_PESA] ?: 0.0
    val actualTigoVal = actualTigoText.toDoubleOrNull() ?: 0.0
    val tigoVariance = actualTigoVal - tigoExp

    val haloExp = expectedClosing.expectedFloats[NetworkType.HALOPESA] ?: 0.0
    val actualHaloVal = actualHaloText.toDoubleOrNull() ?: 0.0
    val haloVariance = actualHaloVal - haloExp

    val totalVariance = cashVariance + mpesaVariance + airtelVariance + tigoVariance + haloVariance
    val isBalanced = abs(totalVariance) < 1.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Kufunga Hesabu ya Siku",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Daily Balance Closing & Reconciliation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = expectedClosing.dateString,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: Funga Leo vs Historia ya Kufunga
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Hesabu ya Leo", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.LockClock, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_closing_today")
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Historia ya Kufunga", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_closing_history")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTabIndex == 0) {
            // Screen 1: Reconcile and Close Day
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Summary Card of Today's Movement
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "1. Mienendo ya Leo (Today's Activity)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Miamala Yote:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${todayStats.totalTransactions} (Kuweka ${todayStats.depositsCount} | Kutoa ${todayStats.withdrawalsCount})",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Jumla ya Kuweka (Deposits In):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormatter.formatTZS(todayStats.depositsAmount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Jumla ya Kutoa (Withdraws Out):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormatter.formatTZS(todayStats.withdrawalsAmount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Kamisheni Uliyopata Leo:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormatter.formatTZS(todayStats.commissionsAmount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E)
                                )
                            }
                        }
                    }
                }

                // Section 2: Expected vs Actual Input
                item {
                    Text(
                        text = "2. Linganisha Hesabu Halisi (Count vs Expected)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Cash in drawer row
                item {
                    ClosingBalanceRow(
                        title = "Fedha Taslimu (Cash in Drawer)",
                        subtitle = "Hesabu noti na sarafu zilizopo kwenye droo",
                        expected = expectedClosing.expectedCash,
                        actualText = actualCashText,
                        variance = cashVariance,
                        onActualChange = { actualCashText = it },
                        brandColor = Color(0xFF15803D),
                        testTag = "cash_actual_input"
                    )
                }

                // M-Pesa Float row
                item {
                    ClosingBalanceRow(
                        title = "M-Pesa Float (Vodacom)",
                        subtitle = "Angalia salio la Wakala kupitia *150*00#",
                        expected = mpesaExp,
                        actualText = actualMpesaText,
                        variance = mpesaVariance,
                        onActualChange = { actualMpesaText = it },
                        brandColor = Color(0xFFE60000),
                        testTag = "mpesa_actual_input"
                    )
                }

                // Airtel Money Float row
                item {
                    ClosingBalanceRow(
                        title = "Airtel Money Float",
                        subtitle = "Angalia salio la Wakala kupitia *150*60#",
                        expected = airtelExp,
                        actualText = actualAirtelText,
                        variance = airtelVariance,
                        onActualChange = { actualAirtelText = it },
                        brandColor = Color(0xFFE50000),
                        testTag = "airtel_actual_input"
                    )
                }

                // Tigo Pesa Float row
                item {
                    ClosingBalanceRow(
                        title = "Tigo Pesa Float (Mixx/Yas)",
                        subtitle = "Angalia salio la Wakala kupitia *150*01#",
                        expected = tigoExp,
                        actualText = actualTigoText,
                        variance = tigoVariance,
                        onActualChange = { actualTigoText = it },
                        brandColor = Color(0xFF00377B),
                        testTag = "tigo_actual_input"
                    )
                }

                // HaloPesa Float row
                item {
                    ClosingBalanceRow(
                        title = "HaloPesa Float (Halotel)",
                        subtitle = "Angalia salio la Wakala kupitia *150*88#",
                        expected = haloExp,
                        actualText = actualHaloText,
                        variance = haloVariance,
                        onActualChange = { actualHaloText = it },
                        brandColor = Color(0xFFFF6F00),
                        testTag = "halo_actual_input"
                    )
                }

                // Section 3: Total Net Variance Banner
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isBalanced -> Color(0xFFE8F5E9)
                                totalVariance < 0 -> Color(0xFFFFEBEE)
                                else -> Color(0xFFFFF3E0)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("net_variance_card")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when {
                                            isBalanced -> Icons.Default.CheckCircle
                                            totalVariance < 0 -> Icons.Default.Error
                                            else -> Icons.Default.Warning
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            isBalanced -> Color(0xFF1B5E20)
                                            totalVariance < 0 -> Color(0xFFB71C1C)
                                            else -> Color(0xFFE65100)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            isBalanced -> "Hesabu Imelingana! (Balanced)"
                                            totalVariance < 0 -> "Kuna Upungufu (Shortage)"
                                            else -> "Kuna Ziada (Excess)"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isBalanced -> Color(0xFF1B5E20)
                                            totalVariance < 0 -> Color(0xFFB71C1C)
                                            else -> Color(0xFFE65100)
                                        }
                                    )
                                }

                                Text(
                                    text = CurrencyFormatter.formatSignedTZS(totalVariance),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        isBalanced -> Color(0xFF1B5E20)
                                        totalVariance < 0 -> Color(0xFFB71C1C)
                                        else -> Color(0xFFE65100)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = when {
                                    isBalanced -> "Fedha zote taslimu na float za simu ziko sawa kabisa kulingana na miamala yote iliyofanyika leo."
                                    totalVariance < 0 -> "Kuna upungufu wa jumla ya ${CurrencyFormatter.formatTZS(abs(totalVariance))}. Tafadhali kagua miamala ya leo kabla ya kufunga."
                                    else -> "Kuna ziada ya jumla ya ${CurrencyFormatter.formatTZS(totalVariance)} kwenye hesabu."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    isBalanced -> Color(0xFF2E7D32)
                                    totalVariance < 0 -> Color(0xFFC62828)
                                    else -> Color(0xFFEF6C00)
                                }
                            )
                        }
                    }
                }

                // Section 4: Notes and Submit Closing
                item {
                    OutlinedTextField(
                        value = closingNotes,
                        onValueChange = { closingNotes = it },
                        label = { Text("Maelezo ya Kufunga Siku (Notes / Reason)") },
                        placeholder = { Text("Mfano: Hesabu imefungwa salama bila upungufu...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("closing_notes_input"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val actualFloats = mapOf(
                                NetworkType.M_PESA to actualMpesaVal,
                                NetworkType.AIRTEL_MONEY to actualAirtelVal,
                                NetworkType.TIGO_PESA to actualTigoVal,
                                NetworkType.HALOPESA to actualHaloVal
                            )
                            viewModel.submitDailyClosing(
                                actualCash = actualCashVal,
                                actualFloats = actualFloats,
                                notes = closingNotes.ifBlank { if (isBalanced) "Hesabu imelingana kikamilifu" else "Hesabu imefungwa na tofauti ya ${CurrencyFormatter.formatSignedTZS(totalVariance)}" }
                            )
                            selectedTabIndex = 1 // Switch to history tab to view saved report
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_daily_closing_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007953)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Funga na Hifadhi Hesabu ya Siku",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        } else {
            // Screen 2: History of Past Daily Closings
            if (pastClosings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hakuna kumbukumbu za kufunga zilizohifadhiwa bado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kamilisha hesabu ya leo kwenye kichupo cha 'Hesabu ya Leo' ili uone ripoti hapa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pastClosings, key = { it.id }) { closing ->
                        DailyClosingHistoryCard(closing = closing)
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ClosingBalanceRow(
    title: String,
    subtitle: String,
    expected: Double,
    actualText: String,
    variance: Double,
    onActualChange: (String) -> Unit,
    brandColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val isZeroVariance = abs(variance) < 1.0

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = brandColor
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quick button to sync expected to actual
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onActualChange(expected.toInt().toString()) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sawazisha",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expected Amount Display
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Inayotarajiwa (Expected):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyFormatter.formatTZS(expected),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Actual Counted Input
                OutlinedTextField(
                    value = actualText,
                    onValueChange = { onActualChange(it.filter { ch -> ch.isDigit() || ch == '.' }) },
                    label = { Text("Iliyopo (Actual)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag(testTag)
                )
            }

            // Variance Pill
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tofauti (Variance):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isZeroVariance) Color(0xFFE8F5E9) else if (variance < 0) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = if (isZeroVariance) "Sawa (0 TZS)" else CurrencyFormatter.formatSignedTZS(variance),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isZeroVariance) Color(0xFF1B5E20) else if (variance < 0) Color(0xFFB71C1C) else Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyClosingHistoryCard(
    closing: DailyClosingEntity,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("closing_card_${closing.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hesabu ya: ${closing.dateString}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ilifungwa: ${CurrencyFormatter.formatDate(closing.closedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (closing.isBalanced) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = if (closing.isBalanced) "Sawa Kabisa" else CurrencyFormatter.formatSignedTZS(closing.totalVariance),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (closing.isBalanced) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cash Drooni: ${CurrencyFormatter.formatTZS(closing.actualCash)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Kamisheni: ${CurrencyFormatter.formatTZS(closing.totalCommissionsEarned)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00796B)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Mgawanyo wa Salio la Mitandao (Float Breakdown):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    HistoryFloatRow(name = "M-Pesa", exp = closing.mpesaExpected, act = closing.mpesaActual, varr = closing.mpesaVariance)
                    HistoryFloatRow(name = "Airtel Money", exp = closing.airtelExpected, act = closing.airtelActual, varr = closing.airtelVariance)
                    HistoryFloatRow(name = "Tigo Pesa", exp = closing.tigoExpected, act = closing.tigoActual, varr = closing.tigoVariance)
                    HistoryFloatRow(name = "HaloPesa", exp = closing.halopesaExpected, act = closing.halopesaActual, varr = closing.halopesaVariance)

                    if (closing.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Maelezo: ${closing.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HistoryFloatRow(
    name: String,
    exp: Double,
    act: Double,
    varr: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "• $name:", style = MaterialTheme.typography.bodySmall)
        Text(
            text = "${CurrencyFormatter.formatTZS(act)} (Diff: ${CurrencyFormatter.formatSignedTZS(varr)})",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (abs(varr) < 1.0) MaterialTheme.colorScheme.onSurface else if (varr < 0) Color(0xFFB71C1C) else Color(0xFFE65100)
        )
    }
}
