package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.ingestion.BankStatementParser
import com.example.data.ingestion.CategoryClassifier
import com.example.data.ingestion.DeduplicationEngine
import com.example.data.ingestion.NotificationParser
import com.example.data.model.ImportedTransactionEntity
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
class AutoTrackingIngestionTest {

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
    fun testNotificationParserExtractsValidDebit() {
        val title = "Google Pay"
        val text = "Paid ₹240.00 to Swiggy via UPI Ref 429182910291 from SBI A/c ...4821"

        val parsed = NotificationParser.parseNotification(title, text)
        assertNotNull(parsed)
        assertEquals(240.0, parsed!!.amount, 0.001)
        assertEquals("DEBIT", parsed.direction)
        assertTrue(parsed.merchantOrSender.contains("Swiggy", ignoreCase = true))
        assertEquals("429182910291", parsed.externalReference)
        assertEquals("••4821", parsed.maskedAccount)
    }

    @Test
    fun testNotificationParserStrictlyRejectsOtp() {
        val title = "Bank Alert"
        val text = "492019 is your secret OTP for ICICI Bank login. Never share OTP with anyone. Valid for 5 mins."

        val parsed = NotificationParser.parseNotification(title, text)
        // Privacy assertion: OTPs MUST return null and never be parsed or stored
        assertNull(parsed)
    }

    @Test
    fun testNotificationParserRejectsPromoMessage() {
        val title = "Offers"
        val text = "Congratulations! You are pre-approved for ₹50,000 credit limit. Apply today!"

        val parsed = NotificationParser.parseNotification(title, text)
        assertNull(parsed)
    }

    @Test
    fun testBankStatementParserHandlesStandardIndianCsv() {
        val csv = """
Date,Narration,Chq/Ref No,Debit,Credit,Balance
29/10/2026,UPI/429182910291/DR/SWIGGY/ICICI,429182910291,240.00,,4760.00
29/10/2026,UPI/829102849182/DR/CHAI POINT/SBI,829102849182,45.00,,4715.00
28/10/2026,UPI/910293847581/DR/DMRC METRO/PAYTM,910293847581,80.00,,4635.00
25/10/2026,ALLOWANCE TRANSFER PAPA,48291823901,,5000.00,9450.00
""".trimIndent()

        val result = BankStatementParser.parseCsv(csv)
        assertTrue(result.success)
        assertEquals(4, result.totalRowsParsed)

        val swiggyRow = result.rows[0]
        assertEquals(240.0, swiggyRow.amount, 0.001)
        assertEquals("DEBIT", swiggyRow.direction)
        assertEquals("429182910291", swiggyRow.reference)

        val allowanceRow = result.rows[3]
        assertEquals(5000.0, allowanceRow.amount, 0.001)
        assertEquals("CREDIT", allowanceRow.direction)
    }

    @Test
    fun testCategoryClassifierKeywordsAndLearnedMapping() {
        // Test standard student keyword classification
        val swiggyResult = CategoryClassifier.classify("UPI/429182910291/DR/SWIGGY/ICICI", "DEBIT")
        assertEquals("Food & Canteen", swiggyResult.category)
        assertTrue(swiggyResult.cleanMerchant.contains("Swiggy", ignoreCase = true))

        val metroResult = CategoryClassifier.classify("DMRC METRO RECHARGE", "DEBIT")
        assertEquals("Travel & Metro", metroResult.category)

        val blinkitResult = CategoryClassifier.classify("BLINKIT GROCERY STORE", "DEBIT")
        assertEquals("Groceries", blinkitResult.category)

        // Test learned mapping override
        val learnedRules = mapOf("CAMPUS XEROX" to "Books & Stationery")
        val customResult = CategoryClassifier.classify("PAYMENT TO CAMPUS XEROX", "DEBIT", learnedRules)
        assertEquals("Books & Stationery", customResult.category)
        assertTrue(customResult.isLearnedRule)
    }

    @Test
    fun testDeduplicationEngineExactMatch() {
        val existingImported = listOf(
            ImportedTransactionEntity(
                id = 1L,
                sourceProviderId = "notification_listener",
                sourceProviderName = "GPay",
                externalReference = "429182910291",
                rawAmount = 240.0,
                direction = "DEBIT",
                rawDescription = "Swiggy payment",
                cleanMerchant = "Swiggy",
                suggestedCategory = "Food & Canteen",
                transactionTimestamp = System.currentTimeMillis(),
                ingestionMethod = "NOTIFICATION"
            )
        )

        val dedupe = DeduplicationEngine.evaluate(
            candidateAmount = 240.0,
            candidateDirection = "DEBIT",
            candidateTimestamp = System.currentTimeMillis(),
            candidateRef = "429182910291",
            candidateMerchant = "Swiggy",
            existingImported = existingImported,
            existingManual = emptyList()
        )

        assertTrue(dedupe.isDuplicate)
        assertTrue(dedupe.isExactMatch)
        assertEquals(1L, dedupe.matchedImportedId)
    }

    @Test
    fun testDeduplicationEngineFuzzyCompositeMatch() {
        val now = System.currentTimeMillis()
        val existingImported = listOf(
            ImportedTransactionEntity(
                id = 10L,
                sourceProviderId = "notification_listener",
                sourceProviderName = "SMS Listener",
                externalReference = "", // no ref
                rawAmount = 45.0,
                direction = "DEBIT",
                rawDescription = "Chai Point",
                cleanMerchant = "Chai Point",
                suggestedCategory = "Food & Canteen",
                transactionTimestamp = now,
                ingestionMethod = "NOTIFICATION"
            )
        )

        // Candidate comes 3 minutes later with same amount and merchant
        val dedupe = DeduplicationEngine.evaluate(
            candidateAmount = 45.0,
            candidateDirection = "DEBIT",
            candidateTimestamp = now + (3 * 60 * 1000), // 3 mins later
            candidateRef = "",
            candidateMerchant = "Chai Point",
            existingImported = existingImported,
            existingManual = emptyList()
        )

        assertTrue(dedupe.isDuplicate)
        assertFalse(dedupe.isExactMatch)
        assertEquals(10L, dedupe.matchedImportedId)
    }

    @Test
    fun testEndToEndImportConfirmReconcileFlow() = runBlocking {
        repository.initializeDefaultDataIfEmpty()

        val sampleCsv = """
Date,Narration,Chq/Ref No,Debit,Credit,Balance
29/10/2026,UPI/998877665544/DR/COLLEGE MESS/SBI,998877665544,120.00,,4880.00
""".trimIndent()

        // 1. Import CSV
        val syncResult = repository.importTransactionsFromCsv(sampleCsv, "Student Bank Account")
        assertTrue(syncResult.success)
        assertEquals(1, syncResult.itemsIngested)

        // 2. Verify imported transaction is in Room
        val importedList = repository.allImportedTransactionsFlow.first()
        val item = importedList.first { it.externalReference == "998877665544" }
        assertEquals(120.0, item.rawAmount, 0.001)
        assertEquals("NEEDS_REVIEW", item.status)

        // 3. Confirm transaction and assign category
        repository.confirmImportedTransaction(
            importedTxId = item.id,
            finalCategory = "Food & Canteen",
            finalMerchant = "College Mess"
        )

        // 4. Verify transaction moved to actual transactions table
        val transactions = repository.allTransactionsFlow.first()
        val confirmedTx = transactions.firstOrNull { it.amount == 120.0 && it.title == "College Mess" }
        assertNotNull(confirmedTx)
        assertEquals("Food & Canteen", confirmedTx!!.category)

        // 5. Verify status updated to CONFIRMED
        val updatedImported = repository.allImportedTransactionsFlow.first().first { it.id == item.id }
        assertEquals("CONFIRMED", updatedImported.status)
        assertEquals(confirmedTx.id, updatedImported.matchedTransactionId)

        // 6. Verify merchant learned rule was created
        val mappings = repository.merchantMappingsFlow.first()
        assertTrue(mappings.any { it.merchantPattern.contains("COLLEGE MESS") && it.category == "Food & Canteen" })
    }
}
