package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.util.CurrencyFormatter

@Composable
fun TransactionListItem(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier,
    onDeleteClick: (() -> Unit)? = null
) {
    val isIncomingCash = transaction.type == TransactionType.DEPOSIT || transaction.type == TransactionType.SELL_FLOAT
    val isOutgoingCash = transaction.type == TransactionType.WITHDRAWAL || transaction.type == TransactionType.BUY_FLOAT || transaction.type == TransactionType.EXPENSE

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("tx_item_${transaction.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        when (transaction.type) {
                            TransactionType.DEPOSIT -> Color(0xFFE8F5E9)
                            TransactionType.WITHDRAWAL -> Color(0xFFFFEBEE)
                            TransactionType.BUY_FLOAT -> Color(0xFFE3F2FD)
                            TransactionType.SELL_FLOAT -> Color(0xFFFFF3E0)
                            TransactionType.EXPENSE -> Color(0xFFF3E5F5)
                            TransactionType.COMMISSION -> Color(0xFFE0F2F1)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.type) {
                        TransactionType.DEPOSIT -> Icons.Default.ArrowDownward
                        TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward
                        TransactionType.BUY_FLOAT, TransactionType.SELL_FLOAT -> Icons.Default.Payments
                        TransactionType.EXPENSE -> Icons.Default.TrendingDown
                        TransactionType.COMMISSION -> Icons.Default.TrendingUp
                    },
                    contentDescription = null,
                    tint = when (transaction.type) {
                        TransactionType.DEPOSIT -> Color(0xFF1B5E20)
                        TransactionType.WITHDRAWAL -> Color(0xFFB71C1C)
                        TransactionType.BUY_FLOAT -> Color(0xFF0D47A1)
                        TransactionType.SELL_FLOAT -> Color(0xFFE65100)
                        TransactionType.EXPENSE -> Color(0xFF4A148C)
                        TransactionType.COMMISSION -> Color(0xFF004D40)
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.type.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    NetworkBadge(network = transaction.network, compact = true)
                }

                Spacer(modifier = Modifier.height(2.dp))

                val subtitle = if (transaction.customerPhone.isNotBlank()) {
                    "${transaction.customerPhone} ${if (transaction.customerName.isNotBlank()) "• ${transaction.customerName}" else ""}"
                } else if (transaction.notes.isNotBlank()) {
                    transaction.notes
                } else if (transaction.referenceNumber.isNotBlank()) {
                    "Ref: ${transaction.referenceNumber}"
                } else {
                    CurrencyFormatter.formatTime(transaction.timestamp)
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (transaction.commission > 0) {
                    Text(
                        text = "+Kamisheni: ${CurrencyFormatter.formatTZS(transaction.commission)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00796B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Amount & Time
            Column(horizontalAlignment = Alignment.End) {
                val amountColor = when {
                    isIncomingCash -> Color(0xFF15803D)
                    isOutgoingCash -> Color(0xFFB91C1C)
                    else -> MaterialTheme.colorScheme.onSurface
                }
                val prefix = if (isIncomingCash) "+" else if (isOutgoingCash) "-" else ""

                Text(
                    text = "$prefix${CurrencyFormatter.formatTZS(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )
                Text(
                    text = CurrencyFormatter.formatTime(transaction.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
