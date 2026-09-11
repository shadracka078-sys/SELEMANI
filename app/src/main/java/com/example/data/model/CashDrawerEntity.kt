package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wakala_cash_drawer")
data class CashDrawerEntity(
    @PrimaryKey
    val id: Int = 1,
    val currentCash: Double,
    val openingCash: Double = currentCash,
    val lastClosingDate: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
