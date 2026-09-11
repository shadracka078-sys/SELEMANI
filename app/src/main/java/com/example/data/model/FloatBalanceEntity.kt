package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wakala_floats")
data class FloatBalanceEntity(
    @PrimaryKey
    val network: NetworkType,
    val currentBalance: Double,
    val openingBalance: Double = currentBalance,
    val lowBalanceThreshold: Double = 50000.0,
    val updatedAt: Long = System.currentTimeMillis()
)
