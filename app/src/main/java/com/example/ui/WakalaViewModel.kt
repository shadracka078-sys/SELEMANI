package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.WakalaDatabase
import com.example.data.model.CashDrawerEntity
import com.example.data.model.DailyClosingEntity
import com.example.data.model.FloatBalanceEntity
import com.example.data.model.NetworkType
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.repository.WakalaRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayStats(
    val depositsCount: Int = 0,
    val depositsAmount: Double = 0.0,
    val withdrawalsCount: Int = 0,
    val withdrawalsAmount: Double = 0.0,
    val commissionsAmount: Double = 0.0,
    val expensesAmount: Double = 0.0,
    val totalTransactions: Int = 0
)

data class ExpectedClosingState(
    val dateString: String = "",
    val openingCash: Double = 0.0,
    val expectedCash: Double = 0.0,
    val expectedFloats: Map<NetworkType, Double> = emptyMap(),
    val openingFloats: Map<NetworkType, Double> = emptyMap()
)

class WakalaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WakalaRepository

    init {
        val database = WakalaDatabase.getDatabase(application)
        repository = WakalaRepository(database.wakalaDao())
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    val cashDrawer: StateFlow<CashDrawerEntity?> = repository.cashDrawerFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val floatBalances: StateFlow<List<FloatBalanceEntity>> = repository.allFloats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDailyClosings: StateFlow<List<DailyClosingEntity>> = repository.allDailyClosings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Filters
    val selectedNetworkFilter = MutableStateFlow<NetworkType?>(null)
    val selectedTypeFilter = MutableStateFlow<TransactionType?>(null)
    val searchQuery = MutableStateFlow("")

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        selectedNetworkFilter,
        selectedTypeFilter,
        searchQuery
    ) { txList, networkFilter, typeFilter, query ->
        txList.filter { tx ->
            val matchesNetwork = networkFilter == null || tx.network == networkFilter
            val matchesType = typeFilter == null || tx.type == typeFilter
            val matchesQuery = query.isBlank() ||
                tx.customerPhone.contains(query, ignoreCase = true) ||
                tx.customerName.contains(query, ignoreCase = true) ||
                tx.referenceNumber.contains(query, ignoreCase = true) ||
                tx.notes.contains(query, ignoreCase = true)
            matchesNetwork && matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today's Stats
    val todayDateString: String = WakalaRepository.getTodayDateString()

    val todayStats: StateFlow<TodayStats> = allTransactions.combine(MutableStateFlow(todayDateString)) { list, today ->
        val todayTxs = list.filter { it.dateString == today }
        val deposits = todayTxs.filter { it.type == TransactionType.DEPOSIT }
        val withdrawals = todayTxs.filter { it.type == TransactionType.WITHDRAWAL }
        val commissions = todayTxs.sumOf { it.commission }
        val expenses = todayTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        TodayStats(
            depositsCount = deposits.size,
            depositsAmount = deposits.sumOf { it.amount },
            withdrawalsCount = withdrawals.size,
            withdrawalsAmount = withdrawals.sumOf { it.amount },
            commissionsAmount = commissions,
            expensesAmount = expenses,
            totalTransactions = todayTxs.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayStats())

    // Expected Closing state calculated dynamically
    val expectedClosing: StateFlow<ExpectedClosingState> = combine(
        cashDrawer,
        floatBalances,
        allTransactions
    ) { drawer, floats, txList ->
        val todayTxs = txList.filter { it.dateString == todayDateString }
        val openCash = drawer?.openingCash ?: 0.0
        
        var calcCash = openCash
        val expectedFloatsMap = mutableMapOf<NetworkType, Double>()
        val openingFloatsMap = mutableMapOf<NetworkType, Double>()

        for (f in floats) {
            openingFloatsMap[f.network] = f.openingBalance
            expectedFloatsMap[f.network] = f.openingBalance
        }

        for (tx in todayTxs) {
            when (tx.type) {
                TransactionType.DEPOSIT -> {
                    calcCash += tx.amount
                    if (tx.network != null) {
                        expectedFloatsMap[tx.network] = (expectedFloatsMap[tx.network] ?: 0.0) - tx.amount
                    }
                }
                TransactionType.WITHDRAWAL -> {
                    calcCash -= tx.amount
                    if (tx.network != null) {
                        expectedFloatsMap[tx.network] = (expectedFloatsMap[tx.network] ?: 0.0) + tx.amount
                    }
                }
                TransactionType.BUY_FLOAT -> {
                    calcCash -= tx.amount
                    if (tx.network != null) {
                        expectedFloatsMap[tx.network] = (expectedFloatsMap[tx.network] ?: 0.0) + tx.amount
                    }
                }
                TransactionType.SELL_FLOAT -> {
                    calcCash += tx.amount
                    if (tx.network != null) {
                        expectedFloatsMap[tx.network] = (expectedFloatsMap[tx.network] ?: 0.0) - tx.amount
                    }
                }
                TransactionType.EXPENSE -> {
                    calcCash -= tx.amount
                }
                TransactionType.COMMISSION -> {
                    if (tx.network != null) {
                        expectedFloatsMap[tx.network] = (expectedFloatsMap[tx.network] ?: 0.0) + (tx.commission.takeIf { it > 0 } ?: tx.amount)
                    } else {
                        calcCash += (tx.commission.takeIf { it > 0 } ?: tx.amount)
                    }
                }
            }
        }

        ExpectedClosingState(
            dateString = todayDateString,
            openingCash = openCash,
            expectedCash = calcCash,
            expectedFloats = expectedFloatsMap,
            openingFloats = openingFloatsMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpectedClosingState())

    // Feedback messages
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    fun recordTransaction(
        type: TransactionType,
        network: NetworkType?,
        amount: Double,
        commission: Double,
        customerPhone: String,
        customerName: String,
        referenceNumber: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (amount <= 0) {
                _userMessage.emit("Tafadhali weka kiasi sahihi cha fedha.")
                return@launch
            }
            val result = repository.recordTransaction(
                type = type,
                network = network,
                amount = amount,
                commission = commission,
                customerPhone = customerPhone,
                customerName = customerName,
                referenceNumber = referenceNumber,
                notes = notes
            )
            if (result.isSuccess) {
                val actionName = when (type) {
                    TransactionType.DEPOSIT -> "Muamala wa Kuweka (Deposit)"
                    TransactionType.WITHDRAWAL -> "Muamala wa Kutoa (Withdrawal)"
                    TransactionType.BUY_FLOAT -> "Kununua Float"
                    TransactionType.SELL_FLOAT -> "Kubadili Float kuwa Cash"
                    TransactionType.EXPENSE -> "Matumizi"
                    TransactionType.COMMISSION -> "Kamisheni"
                }
                _userMessage.emit("$actionName umerekodiwa kikamilifu!")
            } else {
                _userMessage.emit("Hitilafu: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            _userMessage.emit("Muamala umefutwa na salio limerekebishwa.")
        }
    }

    fun submitDailyClosing(
        actualCash: Double,
        actualFloats: Map<NetworkType, Double>,
        notes: String
    ) {
        viewModelScope.launch {
            val exp = expectedClosing.value
            val stats = todayStats.value

            val mpesaExp = exp.expectedFloats[NetworkType.M_PESA] ?: 0.0
            val mpesaAct = actualFloats[NetworkType.M_PESA] ?: mpesaExp
            val airtelExp = exp.expectedFloats[NetworkType.AIRTEL_MONEY] ?: 0.0
            val airtelAct = actualFloats[NetworkType.AIRTEL_MONEY] ?: airtelExp
            val tigoExp = exp.expectedFloats[NetworkType.TIGO_PESA] ?: 0.0
            val tigoAct = actualFloats[NetworkType.TIGO_PESA] ?: tigoExp
            val haloExp = exp.expectedFloats[NetworkType.HALOPESA] ?: 0.0
            val haloAct = actualFloats[NetworkType.HALOPESA] ?: haloExp

            val cashVariance = actualCash - exp.expectedCash
            val mpesaVariance = mpesaAct - mpesaExp
            val airtelVariance = airtelAct - airtelExp
            val tigoVariance = tigoAct - tigoExp
            val haloVariance = haloAct - haloExp

            val closing = DailyClosingEntity(
                dateString = exp.dateString,
                closedAt = System.currentTimeMillis(),
                openingCash = exp.openingCash,
                expectedCash = exp.expectedCash,
                actualCash = actualCash,
                cashVariance = cashVariance,
                mpesaOpening = exp.openingFloats[NetworkType.M_PESA] ?: 0.0,
                mpesaExpected = mpesaExp,
                mpesaActual = mpesaAct,
                mpesaVariance = mpesaVariance,
                airtelOpening = exp.openingFloats[NetworkType.AIRTEL_MONEY] ?: 0.0,
                airtelExpected = airtelExp,
                airtelActual = airtelAct,
                airtelVariance = airtelVariance,
                tigoOpening = exp.openingFloats[NetworkType.TIGO_PESA] ?: 0.0,
                tigoExpected = tigoExp,
                tigoActual = tigoAct,
                tigoVariance = tigoVariance,
                halopesaOpening = exp.openingFloats[NetworkType.HALOPESA] ?: 0.0,
                halopesaExpected = haloExp,
                halopesaActual = haloAct,
                halopesaVariance = haloVariance,
                totalDepositsCount = stats.depositsCount,
                totalDepositsAmount = stats.depositsAmount,
                totalWithdrawalsCount = stats.withdrawalsCount,
                totalWithdrawalsAmount = stats.withdrawalsAmount,
                totalCommissionsEarned = stats.commissionsAmount,
                totalExpenses = stats.expensesAmount,
                notes = notes,
                isClosed = true
            )

            repository.saveDailyClosing(closing)
            _userMessage.emit("Hesabu ya siku ${exp.dateString} imefungwa na kuhifadhiwa kikamilifu!")
        }
    }

    fun adjustFloatDirectly(network: NetworkType, newBalance: Double) {
        viewModelScope.launch {
            repository.updateFloatBalanceDirectly(network, newBalance)
            _userMessage.emit("Salio la float la ${network.displayName} limesasishwa.")
        }
    }

    fun adjustCashDrawerDirectly(newCash: Double) {
        viewModelScope.launch {
            repository.updateCashDrawerDirectly(newCash)
            _userMessage.emit("Salio la fedha taslimu (Cash) limesasishwa.")
        }
    }

    fun resetDayBalances() {
        viewModelScope.launch {
            repository.resetDrawerAndFloatsToOpening()
            _userMessage.emit("Salio la kuanzia limewekwa upya.")
        }
    }
}
