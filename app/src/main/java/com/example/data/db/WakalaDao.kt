package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CashDrawerEntity
import com.example.data.model.DailyClosingEntity
import com.example.data.model.FloatBalanceEntity
import com.example.data.model.NetworkType
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WakalaDao {

    // --- Transactions ---
    @Query("SELECT * FROM wakala_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM wakala_transactions WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getTransactionsByDate(dateString: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM wakala_transactions WHERE network = :network ORDER BY timestamp DESC")
    fun getTransactionsByNetwork(network: NetworkType): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM wakala_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    // --- Float Balances ---
    @Query("SELECT * FROM wakala_floats")
    fun getAllFloats(): Flow<List<FloatBalanceEntity>>

    @Query("SELECT * FROM wakala_floats WHERE network = :network LIMIT 1")
    suspend fun getFloat(network: NetworkType): FloatBalanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFloat(floatBalance: FloatBalanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFloats(floats: List<FloatBalanceEntity>)

    @Query("UPDATE wakala_floats SET currentBalance = :newBalance, updatedAt = :timestamp WHERE network = :network")
    suspend fun updateFloatBalance(network: NetworkType, newBalance: Double, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE wakala_floats SET openingBalance = currentBalance")
    suspend fun resetFloatOpeningBalances()

    // --- Cash Drawer ---
    @Query("SELECT * FROM wakala_cash_drawer WHERE id = 1 LIMIT 1")
    fun getCashDrawerFlow(): Flow<CashDrawerEntity?>

    @Query("SELECT * FROM wakala_cash_drawer WHERE id = 1 LIMIT 1")
    suspend fun getCashDrawer(): CashDrawerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCashDrawer(drawer: CashDrawerEntity)

    @Query("UPDATE wakala_cash_drawer SET currentCash = :newCash, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateCashDrawer(newCash: Double, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE wakala_cash_drawer SET openingCash = currentCash")
    suspend fun resetCashOpeningBalance()

    // --- Daily Closing ---
    @Query("SELECT * FROM wakala_daily_closings ORDER BY closedAt DESC")
    fun getAllDailyClosings(): Flow<List<DailyClosingEntity>>

    @Query("SELECT * FROM wakala_daily_closings WHERE dateString = :dateString LIMIT 1")
    suspend fun getDailyClosingByDate(dateString: String): DailyClosingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyClosing(closing: DailyClosingEntity): Long
}
