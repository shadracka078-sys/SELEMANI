package com.example.data.repository

import com.example.data.db.WakalaDao
import com.example.data.model.CashDrawerEntity
import com.example.data.model.DailyClosingEntity
import com.example.data.model.FloatBalanceEntity
import com.example.data.model.NetworkType
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WakalaRepository(private val wakalaDao: WakalaDao) {

    val allTransactions: Flow<List<TransactionEntity>> = wakalaDao.getAllTransactions()
    val allFloats: Flow<List<FloatBalanceEntity>> = wakalaDao.getAllFloats()
    val cashDrawerFlow: Flow<CashDrawerEntity?> = wakalaDao.getCashDrawerFlow()
    val allDailyClosings: Flow<List<DailyClosingEntity>> = wakalaDao.getAllDailyClosings()

    fun getTransactionsByDate(dateString: String): Flow<List<TransactionEntity>> =
        wakalaDao.getTransactionsByDate(dateString)

    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentDrawer = wakalaDao.getCashDrawer()
        if (currentDrawer == null) {
            // Seed Cash in Drawer
            val initialCash = 450000.0
            wakalaDao.insertOrUpdateCashDrawer(
                CashDrawerEntity(
                    id = 1,
                    currentCash = initialCash,
                    openingCash = initialCash,
                    lastClosingDate = "",
                    updatedAt = System.currentTimeMillis()
                )
            )

            // Seed Initial Floats for all networks
            val initialFloats = listOf(
                FloatBalanceEntity(NetworkType.M_PESA, currentBalance = 650000.0, openingBalance = 650000.0, lowBalanceThreshold = 100000.0),
                FloatBalanceEntity(NetworkType.AIRTEL_MONEY, currentBalance = 400000.0, openingBalance = 400000.0, lowBalanceThreshold = 80000.0),
                FloatBalanceEntity(NetworkType.TIGO_PESA, currentBalance = 350000.0, openingBalance = 350000.0, lowBalanceThreshold = 80000.0),
                FloatBalanceEntity(NetworkType.HALOPESA, currentBalance = 150000.0, openingBalance = 150000.0, lowBalanceThreshold = 50000.0)
            )
            wakalaDao.insertAllFloats(initialFloats)

            // Seed a few realistic initial transactions for today
            val todayStr = getTodayDateString()
            val now = System.currentTimeMillis()

            val seedTransactions = listOf(
                TransactionEntity(
                    type = TransactionType.DEPOSIT,
                    network = NetworkType.M_PESA,
                    amount = 50000.0,
                    commission = 450.0,
                    customerPhone = "0754 123 456",
                    customerName = "Juma Bakari",
                    referenceNumber = "MP892341",
                    notes = "Mteja ameweka pesa M-Pesa",
                    timestamp = now - 3600000 * 3,
                    dateString = todayStr
                ),
                TransactionEntity(
                    type = TransactionType.WITHDRAWAL,
                    network = NetworkType.AIRTEL_MONEY,
                    amount = 30000.0,
                    commission = 650.0,
                    customerPhone = "0784 987 654",
                    customerName = "Amina Salim",
                    referenceNumber = "AM551203",
                    notes = "Mteja ametoa Airtel Money",
                    timestamp = now - 3600000 * 2,
                    dateString = todayStr
                ),
                TransactionEntity(
                    type = TransactionType.DEPOSIT,
                    network = NetworkType.TIGO_PESA,
                    amount = 25000.0,
                    commission = 300.0,
                    customerPhone = "0712 345 678",
                    customerName = "Emmanuel Kimaro",
                    referenceNumber = "TG774812",
                    notes = "Deposit ya Tigo Pesa",
                    timestamp = now - 3600000 * 1,
                    dateString = todayStr
                )
            )

            // Adjust the drawer and float for the seeded transactions
            var cash = initialCash
            var mpesa = 650000.0
            var airtel = 400000.0
            var tigo = 350000.0

            for (tx in seedTransactions) {
                wakalaDao.insertTransaction(tx)
                when (tx.type) {
                    TransactionType.DEPOSIT -> {
                        cash += tx.amount
                        if (tx.network == NetworkType.M_PESA) mpesa -= tx.amount
                        if (tx.network == NetworkType.TIGO_PESA) tigo -= tx.amount
                    }
                    TransactionType.WITHDRAWAL -> {
                        cash -= tx.amount
                        if (tx.network == NetworkType.AIRTEL_MONEY) airtel += tx.amount
                    }
                    else -> {}
                }
            }

            wakalaDao.updateCashDrawer(cash)
            wakalaDao.updateFloatBalance(NetworkType.M_PESA, mpesa)
            wakalaDao.updateFloatBalance(NetworkType.AIRTEL_MONEY, airtel)
            wakalaDao.updateFloatBalance(NetworkType.TIGO_PESA, tigo)
        }
    }

    suspend fun recordTransaction(
        type: TransactionType,
        network: NetworkType?,
        amount: Double,
        commission: Double = 0.0,
        customerPhone: String = "",
        customerName: String = "",
        referenceNumber: String = "",
        notes: String = ""
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val drawer = wakalaDao.getCashDrawer() ?: CashDrawerEntity(currentCash = 0.0)
            var updatedCash = drawer.currentCash

            val targetNetwork = network ?: NetworkType.M_PESA
            val floatEntity = wakalaDao.getFloat(targetNetwork)
                ?: FloatBalanceEntity(targetNetwork, currentBalance = 0.0)
            var updatedFloat = floatEntity.currentBalance

            when (type) {
                TransactionType.DEPOSIT -> {
                    // Customer pays cash -> Agent receives Cash into drawer (+Cash)
                    // Agent sends float from SIM to customer's phone (-Float)
                    updatedCash += amount
                    updatedFloat -= amount
                }
                TransactionType.WITHDRAWAL -> {
                    // Customer sends float from phone to Agent till (+Float)
                    // Agent gives physical cash to customer (-Cash)
                    updatedCash -= amount
                    updatedFloat += amount
                }
                TransactionType.BUY_FLOAT -> {
                    // Wakala purchases float using cash from drawer or bank
                    // Agent pays cash (-Cash), Agent receives float (+Float)
                    updatedCash -= amount
                    updatedFloat += amount
                }
                TransactionType.SELL_FLOAT -> {
                    // Wakala converts excess float back into cash
                    // Agent receives cash (+Cash), Agent gives/transfers float (-Float)
                    updatedCash += amount
                    updatedFloat -= amount
                }
                TransactionType.EXPENSE -> {
                    // Operational expense paid from drawer cash
                    updatedCash -= amount
                }
                TransactionType.COMMISSION -> {
                    // Commission earned
                    if (network != null) {
                        updatedFloat += (commission.takeIf { it > 0 } ?: amount)
                    } else {
                        updatedCash += (commission.takeIf { it > 0 } ?: amount)
                    }
                }
            }

            // Save transaction record
            val tx = TransactionEntity(
                type = type,
                network = network,
                amount = amount,
                commission = commission,
                customerPhone = customerPhone.trim(),
                customerName = customerName.trim(),
                referenceNumber = referenceNumber.trim(),
                notes = notes.trim(),
                timestamp = System.currentTimeMillis(),
                dateString = getTodayDateString()
            )
            val txId = wakalaDao.insertTransaction(tx)

            // Update drawer and float
            wakalaDao.updateCashDrawer(updatedCash)
            if (network != null && type != TransactionType.EXPENSE) {
                wakalaDao.updateFloatBalance(targetNetwork, updatedFloat)
            }

            Result.success(txId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFloatBalanceDirectly(network: NetworkType, newBalance: Double) = withContext(Dispatchers.IO) {
        wakalaDao.updateFloatBalance(network, newBalance)
    }

    suspend fun updateCashDrawerDirectly(newCash: Double) = withContext(Dispatchers.IO) {
        wakalaDao.updateCashDrawer(newCash)
    }

    suspend fun resetDrawerAndFloatsToOpening() = withContext(Dispatchers.IO) {
        wakalaDao.resetCashOpeningBalance()
        wakalaDao.resetFloatOpeningBalances()
    }

    suspend fun saveDailyClosing(closing: DailyClosingEntity) = withContext(Dispatchers.IO) {
        wakalaDao.insertDailyClosing(closing)
        
        // After successfully closing, set the actual counts as the new opening balances for the next day
        wakalaDao.updateCashDrawer(closing.actualCash)
        wakalaDao.updateFloatBalance(NetworkType.M_PESA, closing.mpesaActual)
        wakalaDao.updateFloatBalance(NetworkType.AIRTEL_MONEY, closing.airtelActual)
        wakalaDao.updateFloatBalance(NetworkType.TIGO_PESA, closing.tigoActual)
        wakalaDao.updateFloatBalance(NetworkType.HALOPESA, closing.halopesaActual)

        // Set opening balances equal to these new current balances
        val currentDrawer = wakalaDao.getCashDrawer()
        if (currentDrawer != null) {
            wakalaDao.insertOrUpdateCashDrawer(
                currentDrawer.copy(
                    currentCash = closing.actualCash,
                    openingCash = closing.actualCash,
                    lastClosingDate = closing.dateString,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        val networks = listOf(
            NetworkType.M_PESA to closing.mpesaActual,
            NetworkType.AIRTEL_MONEY to closing.airtelActual,
            NetworkType.TIGO_PESA to closing.tigoActual,
            NetworkType.HALOPESA to closing.halopesaActual
        )
        for ((net, bal) in networks) {
            val f = wakalaDao.getFloat(net)
            if (f != null) {
                wakalaDao.insertOrUpdateFloat(
                    f.copy(currentBalance = bal, openingBalance = bal, updatedAt = System.currentTimeMillis())
                )
            }
        }
    }

    suspend fun deleteTransaction(tx: TransactionEntity) = withContext(Dispatchers.IO) {
        // Reverse transaction effect on balances
        val drawer = wakalaDao.getCashDrawer()
        if (drawer != null) {
            var revCash = drawer.currentCash
            when (tx.type) {
                TransactionType.DEPOSIT -> revCash -= tx.amount
                TransactionType.WITHDRAWAL -> revCash += tx.amount
                TransactionType.BUY_FLOAT -> revCash += tx.amount
                TransactionType.SELL_FLOAT -> revCash -= tx.amount
                TransactionType.EXPENSE -> revCash += tx.amount
                TransactionType.COMMISSION -> {}
            }
            wakalaDao.updateCashDrawer(revCash)
        }

        if (tx.network != null) {
            val f = wakalaDao.getFloat(tx.network)
            if (f != null) {
                var revFloat = f.currentBalance
                when (tx.type) {
                    TransactionType.DEPOSIT -> revFloat += tx.amount
                    TransactionType.WITHDRAWAL -> revFloat -= tx.amount
                    TransactionType.BUY_FLOAT -> revFloat -= tx.amount
                    TransactionType.SELL_FLOAT -> revFloat += tx.amount
                    TransactionType.COMMISSION -> revFloat -= (tx.commission.takeIf { it > 0 } ?: tx.amount)
                    TransactionType.EXPENSE -> {}
                }
                wakalaDao.updateFloatBalance(tx.network, revFloat)
            }
        }

        wakalaDao.deleteTransaction(tx.id)
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
