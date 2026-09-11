package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkType
import com.example.data.model.TransactionType

@Composable
fun NetworkBadge(
    network: NetworkType?,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    if (network == null) {
        // Pure Cash badge
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE2E8F0))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cash / Taslimu",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF334155),
                fontWeight = FontWeight.SemiBold
            )
        }
        return
    }

    val (bg, fg, label) = when (network) {
        NetworkType.M_PESA -> Triple(Color(0xFFFFEBEE), Color(0xFFE60000), "M-Pesa")
        NetworkType.AIRTEL_MONEY -> Triple(Color(0xFFFFF3F0), Color(0xFFE50000), "Airtel")
        NetworkType.TIGO_PESA -> Triple(Color(0xFFE8F0FE), Color(0xFF00377B), "Tigo Pesa")
        NetworkType.HALOPESA -> Triple(Color(0xFFFFF3E0), Color(0xFFFF6F00), "HaloPesa")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 2.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 6.dp else 8.dp)
                .clip(CircleShape)
                .background(fg)
        )
        Text(
            text = " $label",
            style = if (compact) MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp) else MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TransactionTypeBadge(
    type: TransactionType,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (type) {
        TransactionType.DEPOSIT -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        TransactionType.WITHDRAWAL -> Color(0xFFFFEBEE) to Color(0xFFB71C1C)
        TransactionType.BUY_FLOAT -> Color(0xFFE3F2FD) to Color(0xFF0D47A1)
        TransactionType.SELL_FLOAT -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        TransactionType.EXPENSE -> Color(0xFFF3E5F5) to Color(0xFF4A148C)
        TransactionType.COMMISSION -> Color(0xFFE0F2F1) to Color(0xFF004D40)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${type.title} (${type.swahiliTitle})",
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}
