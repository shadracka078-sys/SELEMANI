package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.WakalaDatabase
import com.example.data.model.DailyClosingEntity
import com.example.data.model.NetworkType
import com.example.data.model.TransactionType
import com.example.data.repository.WakalaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WakalaFlowTest {

    private lateinit var db: WakalaDatabase
    private lateinit var repository: WakalaRepository

    @Before
    fun setup(): Unit = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WakalaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WakalaRepository(db.wakalaDao())
        repository.initializeDefaultDataIfEmpty()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testDepositUpdatesCashAndFloat(): Unit = runBlocking {
        val initialCash = repository.cashDrawerFlow.first()?.currentCash ?: 0.0
        val initialMpesa = db.wakalaDao().getFloat(NetworkType.M_PESA)?.currentBalance ?: 0.0

        val depositAmount = 50000.0
        val commission = 600.0

        repository.recordTransaction(
            type = TransactionType.DEPOSIT,
            network = NetworkType.M_PESA,
            amount = depositAmount,
            commission = commission,
            customerPhone = "0754123456",
            customerName = "Ali Juma",
            referenceNumber = "MP12345678",
            notes = "Mtihani wa kuweka"
        )

        val updatedCash = repository.cashDrawerFlow.first()?.currentCash ?: 0.0
        val updatedMpesa = db.wakalaDao().getFloat(NetworkType.M_PESA)?.currentBalance ?: 0.0

        // Customer deposits cash into agent drawer, agent sends float to customer phone
        assertEquals(initialCash + depositAmount, updatedCash, 0.01)
        // Float decreases by deposit amount
        assertEquals(initialMpesa - depositAmount, updatedMpesa, 0.01)
    }

    @Test
    fun testWithdrawalUpdatesCashAndFloat(): Unit = runBlocking {
        val initialCash = repository.cashDrawerFlow.first()?.currentCash ?: 0.0
        val initialAirtel = db.wakalaDao().getFloat(NetworkType.AIRTEL_MONEY)?.currentBalance ?: 0.0

        val withdrawalAmount = 30000.0
        val commission = 450.0

        repository.recordTransaction(
            type = TransactionType.WITHDRAWAL,
            network = NetworkType.AIRTEL_MONEY,
            amount = withdrawalAmount,
            commission = commission,
            customerPhone = "0784987654",
            customerName = "Amina Said",
            referenceNumber = "AT87654321",
            notes = "Mtihani wa kutoa"
        )

        val updatedCash = repository.cashDrawerFlow.first()?.currentCash ?: 0.0
        val updatedAirtel = db.wakalaDao().getFloat(NetworkType.AIRTEL_MONEY)?.currentBalance ?: 0.0

        // Customer withdraws: agent gives physical cash, gets digital float
        assertEquals(initialCash - withdrawalAmount, updatedCash, 0.01)
        assertEquals(initialAirtel + withdrawalAmount, updatedAirtel, 0.01)
    }

    @Test
    fun testDailyClosingPersistence(): Unit = runBlocking {
        val currentCash = repository.cashDrawerFlow.first()?.currentCash ?: 0.0
        val mpesaBal = db.wakalaDao().getFloat(NetworkType.M_PESA)?.currentBalance ?: 0.0
        val airtelBal = db.wakalaDao().getFloat(NetworkType.AIRTEL_MONEY)?.currentBalance ?: 0.0
        val tigoBal = db.wakalaDao().getFloat(NetworkType.TIGO_PESA)?.currentBalance ?: 0.0
        val haloBal = db.wakalaDao().getFloat(NetworkType.HALOPESA)?.currentBalance ?: 0.0

        val closing = DailyClosingEntity(
            dateString = "2026-09-11",
            closedAt = System.currentTimeMillis(),
            openingCash = currentCash,
            expectedCash = currentCash,
            actualCash = currentCash,
            cashVariance = 0.0,
            mpesaOpening = mpesaBal,
            mpesaExpected = mpesaBal,
            mpesaActual = mpesaBal,
            mpesaVariance = 0.0,
            airtelOpening = airtelBal,
            airtelExpected = airtelBal,
            airtelActual = airtelBal,
            airtelVariance = 0.0,
            tigoOpening = tigoBal,
            tigoExpected = tigoBal,
            tigoActual = tigoBal,
            tigoVariance = 0.0,
            halopesaOpening = haloBal,
            halopesaExpected = haloBal,
            halopesaActual = haloBal,
            halopesaVariance = 0.0,
            totalDepositsCount = 0,
            totalDepositsAmount = 0.0,
            totalWithdrawalsCount = 0,
            totalWithdrawalsAmount = 0.0,
            totalCommissionsEarned = 0.0,
            totalExpenses = 0.0,
            notes = "Hesabu imelingana kikamilifu"
        )

        repository.saveDailyClosing(closing)

        val pastClosings = repository.allDailyClosings.first()
        assertEquals(1, pastClosings.size)
        assertTrue(pastClosings[0].isBalanced)
        assertEquals(0.0, pastClosings[0].totalVariance, 0.01)
    }
}
