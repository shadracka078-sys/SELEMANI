package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wakala_transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val network: NetworkType?, // null for pure cash expense
    val amount: Double,
    val commission: Double = 0.0,
    val customerPhone: String = "",
    val customerName: String = "",
    val referenceNumber: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String // Format: yyyy-MM-dd
)
