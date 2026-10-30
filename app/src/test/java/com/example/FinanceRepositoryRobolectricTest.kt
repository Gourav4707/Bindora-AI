package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FinanceRepositoryRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: FinanceRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FinanceRepository(database.financeDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testUserProfilePersistence() = runBlocking {
        val profile = UserProfileEntity(
            id = 1L,
            name = "Gourav Dhaniya",
            college = "IIT Delhi",
            isHosteller = true,
            monthlyPocketMoney = 6000.0,
            targetMonthlySavings = 1200.0,
            preferredLanguage = "Hinglish",
            isOnboarded = true
        )
        repository.saveProfile(profile)

        val retrieved = repository.userProfileFlow.first()
        assertNotNull(retrieved)
        assertEquals("Gourav Dhaniya", retrieved?.name)
        assertEquals("IIT Delhi", retrieved?.college)
        assertEquals(6000.0, retrieved?.monthlyPocketMoney ?: 0.0, 0.01)
    }

    @Test
    fun testTransactionInsertionAndDeletion() = runBlocking {
        val tx = TransactionEntity(
            title = "Canteen Cold Coffee",
            amount = 50.0,
            isIncome = false,
            category = "Food & Canteen",
            notes = "Exam break",
            paymentMode = "UPI"
        )
        repository.addTransaction(tx)

        val allTxs = repository.allTransactionsFlow.first()
        assertEquals(1, allTxs.size)
        assertEquals("Canteen Cold Coffee", allTxs[0].title)
        assertEquals(50.0, allTxs[0].amount, 0.01)

        repository.deleteTransaction(allTxs[0])
        val emptyTxs = repository.allTransactionsFlow.first()
        assertTrue(emptyTxs.isEmpty())
    }

    @Test
    fun testSavingsGoalContributionUpdatesGoalAmount() = runBlocking {
        val goal = SavingsGoalEntity(
            title = "New Laptop",
            targetAmount = 50000.0,
            savedAmount = 5000.0,
            category = "Tech",
            targetDateMillis = System.currentTimeMillis() + 10000000L
        )
        val goalId = repository.addSavingsGoal(goal)

        // Add contribution of 2000
        repository.addContribution(goalId, 2000.0, "Saved from pocket money")

        val goals = repository.activeSavingsGoalsFlow.first()
        assertEquals(1, goals.size)
        // Assert the savedAmount was updated from 5000 to 7000!
        assertEquals(7000.0, goals[0].savedAmount, 0.01)
    }

    @Test
    fun testPaperTradingBuyAndSellFlow() = runBlocking {
        // Initialize clean account with 1,00,000 cash
        repository.resetPaperAccount()

        // 1. Buy 10 shares of Reliance at 2800 = 28,000
        val (buySuccess, buyMsg) = repository.executePaperOrder(
            symbol = "RELIANCE",
            companyName = "Reliance Industries",
            isBuy = true,
            quantity = 10,
            price = 2800.0
        )
        assertTrue(buySuccess)

        val accountAfterBuy = repository.paperAccountFlow.first()
        assertEquals(72000.0, accountAfterBuy?.cashBalance ?: 0.0, 0.01)

        val holdingsAfterBuy = repository.paperHoldingsFlow.first()
        assertEquals(1, holdingsAfterBuy.size)
        assertEquals("RELIANCE", holdingsAfterBuy[0].symbol)
        assertEquals(10, holdingsAfterBuy[0].quantity)
        assertEquals(2800.0, holdingsAfterBuy[0].avgBuyPrice, 0.01)

        // 2. Reject buy if insufficient cash
        val (failBuySuccess, _) = repository.executePaperOrder(
            symbol = "TCS",
            companyName = "Tata Consultancy Services",
            isBuy = true,
            quantity = 100,
            price = 4000.0 // 4,00,000 > 72,000
        )
        assertFalse(failBuySuccess)

        // 3. Sell 5 shares at 3000 (profit of 200 per share = +1000 profit, proceeds = 15,000)
        val (sellSuccess, sellMsg) = repository.executePaperOrder(
            symbol = "RELIANCE",
            companyName = "Reliance Industries",
            isBuy = false,
            quantity = 5,
            price = 3000.0
        )
        assertTrue(sellSuccess)
        assertTrue(sellMsg.contains("profit"))

        val accountAfterSell = repository.paperAccountFlow.first()
        // Cash should be 72000 + 15000 = 87000
        assertEquals(87000.0, accountAfterSell?.cashBalance ?: 0.0, 0.01)
        // Realized PnL should be +1000
        assertEquals(1000.0, accountAfterSell?.totalRealizedPnl ?: 0.0, 0.01)

        val holdingsAfterSell = repository.paperHoldingsFlow.first()
        assertEquals(1, holdingsAfterSell.size)
        assertEquals(5, holdingsAfterSell[0].quantity)

        // 4. Sell remaining 5 shares
        repository.executePaperOrder("RELIANCE", "Reliance Industries", isBuy = false, quantity = 5, price = 3000.0)
        val finalHoldings = repository.paperHoldingsFlow.first()
        assertTrue(finalHoldings.isEmpty())
    }

    @Test
    fun testDataWipeClearsAllTables() = runBlocking {
        repository.initializeDefaultDataIfEmpty()
        val beforeProfile = repository.userProfileFlow.first()
        assertNotNull(beforeProfile)

        // Trigger data wipe
        repository.clearAllUserData()

        val afterProfile = repository.userProfileFlow.first()
        assertNull(afterProfile)

        val afterTxs = repository.allTransactionsFlow.first()
        assertTrue(afterTxs.isEmpty())

        val afterGoals = repository.activeSavingsGoalsFlow.first()
        assertTrue(afterGoals.isEmpty())
    }
}
