package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wakala_daily_closings")
data class DailyClosingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // "yyyy-MM-dd"
    val closedAt: Long = System.currentTimeMillis(),
    val openingCash: Double,
    val expectedCash: Double,
    val actualCash: Double,
    val cashVariance: Double, // actualCash - expectedCash (negative is shortage)
    
    // M-Pesa
    val mpesaOpening: Double,
    val mpesaExpected: Double,
    val mpesaActual: Double,
    val mpesaVariance: Double,
    
    // Airtel Money
    val airtelOpening: Double,
    val airtelExpected: Double,
    val airtelActual: Double,
    val airtelVariance: Double,
    
    // Tigo Pesa
    val tigoOpening: Double,
    val tigoExpected: Double,
    val tigoActual: Double,
    val tigoVariance: Double,
    
    // HaloPesa
    val halopesaOpening: Double,
    val halopesaExpected: Double,
    val halopesaActual: Double,
    val halopesaVariance: Double,
    
    // Daily summary statistics
    val totalDepositsCount: Int = 0,
    val totalDepositsAmount: Double = 0.0,
    val totalWithdrawalsCount: Int = 0,
    val totalWithdrawalsAmount: Double = 0.0,
    val totalCommissionsEarned: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val notes: String = "",
    val isClosed: Boolean = true
) {
    val totalVariance: Double
        get() = cashVariance + mpesaVariance + airtelVariance + tigoVariance + halopesaVariance
        
    val isBalanced: Boolean
        get() = kotlin.math.abs(totalVariance) < 1.0
}
