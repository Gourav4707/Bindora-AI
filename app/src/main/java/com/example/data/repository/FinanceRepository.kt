package com.example.data.repository

import com.example.data.dao.FinanceDao
import com.example.data.model.BudgetEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.GoalContributionEntity
import com.example.data.model.LessonProgressEntity
import com.example.data.model.LoanScenarioEntity
import com.example.data.model.PaperAccountEntity
import com.example.data.model.PaperHoldingEntity
import com.example.data.model.PaperOrderEntity
import com.example.data.model.PaperTradingData
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceRepository(private val dao: FinanceDao) {

    val userProfileFlow: Flow<UserProfileEntity?> = dao.getUserProfileFlow()
    val allTransactionsFlow: Flow<List<TransactionEntity>> = dao.getAllTransactionsFlow()
    val activeSavingsGoalsFlow: Flow<List<SavingsGoalEntity>> = dao.getActiveSavingsGoalsFlow()
    val loanScenariosFlow: Flow<List<LoanScenarioEntity>> = dao.getAllLoanScenariosFlow()
    val paperAccountFlow: Flow<PaperAccountEntity?> = dao.getPaperAccountFlow()
    val paperHoldingsFlow: Flow<List<PaperHoldingEntity>> = dao.getAllHoldingsFlow()
    val paperOrdersFlow: Flow<List<PaperOrderEntity>> = dao.getAllOrdersFlow()
    val lessonProgressFlow: Flow<List<LessonProgressEntity>> = dao.getAllLessonProgressFlow()
    val chatMessagesFlow: Flow<List<ChatMessageEntity>> = dao.getChatMessagesFlow()
    val userSubscriptionFlow: Flow<com.example.data.model.UserSubscriptionEntity?> = dao.getUserSubscriptionFlow()
    val paymentOrdersFlow: Flow<List<com.example.data.model.PaymentOrderEntity>> = dao.getAllPaymentOrdersFlow()

    fun getBudgetForMonth(monthKey: String): Flow<BudgetEntity?> = dao.getBudgetForMonthFlow(monthKey)

    fun getCurrentMonthKey(): String {
        return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }

    suspend fun initializeDefaultDataIfEmpty() {
        val existingProfile = dao.getUserProfile()
        if (existingProfile == null) {
            // Seed initial student profile
            val defaultProfile = UserProfileEntity(
                id = 1L,
                name = "Aman Sharma",
                college = "Delhi Technological University",
                isHosteller = true,
                monthlyPocketMoney = 5000.0,
                targetMonthlySavings = 1000.0,
                preferredLanguage = "Hinglish",
                isOnboarded = true,
                currency = "₹"
            )
            dao.saveUserProfile(defaultProfile)

            // Seed current month budget
            val monthKey = getCurrentMonthKey()
            dao.saveBudget(
                BudgetEntity(
                    monthKey = monthKey,
                    totalLimit = 5000.0,
                    canteenLimit = 1600.0,
                    groceriesLimit = 800.0,
                    travelLimit = 600.0,
                    booksLimit = 500.0,
                    entertainmentLimit = 600.0,
                    rechargeLimit = 300.0,
                    shoppingLimit = 400.0,
                    otherLimit = 200.0
                )
            )

            // Seed initial transactions
            val now = System.currentTimeMillis()
            val day = 24L * 60 * 60 * 1000
            dao.insertTransaction(
                TransactionEntity(
                    title = "Monthly Pocket Money",
                    amount = 5000.0,
                    isIncome = true,
                    category = "Pocket Money",
                    dateMillis = now - 5 * day,
                    notes = "Received from Papa",
                    paymentMode = "UPI"
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    title = "College Canteen Chai & Samosa",
                    amount = 60.0,
                    isIncome = false,
                    category = "Food & Canteen",
                    dateMillis = now - 4 * day,
                    notes = "Tea break with hostel friends",
                    paymentMode = "UPI"
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    title = "Delhi Metro SmartCard Recharge",
                    amount = 200.0,
                    isIncome = false,
                    category = "Travel & Metro",
                    dateMillis = now - 3 * day,
                    notes = "Weekly commute to campus",
                    paymentMode = "UPI"
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    title = "Engineering Mathematics Xerox",
                    amount = 120.0,
                    isIncome = false,
                    category = "Books & Stationery",
                    dateMillis = now - 2 * day,
                    notes = "Previous year question papers",
                    paymentMode = "Cash"
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    title = "Hostel Mess Night Maggie & Milk",
                    amount = 85.0,
                    isIncome = false,
                    category = "Groceries",
                    dateMillis = now - 1 * day,
                    notes = "Late night study session",
                    paymentMode = "UPI"
                )
            )

            // Seed initial Savings Goals
            val goalId1 = dao.insertSavingsGoal(
                SavingsGoalEntity(
                    title = "Hostel Emergency Buffer",
                    targetAmount = 3000.0,
                    savedAmount = 1200.0,
                    category = "Emergency",
                    targetDateMillis = now + 45 * day
                )
            )
            dao.insertContribution(
                GoalContributionEntity(
                    goalId = goalId1,
                    amount = 1200.0,
                    dateMillis = now - 5 * day,
                    note = "Saved from pocket money"
                )
            )

            dao.insertSavingsGoal(
                SavingsGoalEntity(
                    title = "Laptop for Coding & Internships",
                    targetAmount = 45000.0,
                    savedAmount = 6500.0,
                    category = "Tech",
                    targetDateMillis = now + 180 * day
                )
            )

            // Seed Paper Trading Account
            dao.savePaperAccount(
                PaperAccountEntity(
                    id = 1L,
                    cashBalance = 100000.0,
                    initialBalance = 100000.0,
                    totalRealizedPnl = 0.0
                )
            )

            // Seed sample holding
            val zStock = PaperTradingData.INITIAL_STOCKS.first { it.symbol == "ZOMATO" }
            dao.saveHolding(
                PaperHoldingEntity(
                    symbol = zStock.symbol,
                    companyName = zStock.companyName,
                    quantity = 25,
                    avgBuyPrice = 220.0,
                    currentPrice = zStock.currentPrice
                )
            )
            dao.insertOrder(
                PaperOrderEntity(
                    symbol = zStock.symbol,
                    companyName = zStock.companyName,
                    orderType = "BUY",
                    quantity = 25,
                    price = 220.0,
                    totalAmount = 5500.0,
                    timestampMillis = now - 3 * day
                )
            )
            dao.savePaperAccount(
                PaperAccountEntity(
                    id = 1L,
                    cashBalance = 94500.0,
                    initialBalance = 100000.0,
                    totalRealizedPnl = 0.0
                )
            )

            // Seed initial welcoming message in Chat
            dao.insertChatMessage(
                ChatMessageEntity(
                    sender = "coach",
                    text = "Namaste Aman! 👋 Main aapka Bindora AI Coach hoon. Every Rupee Has a Purpose! Pocket money budgeting, canteen kharcha control karna, ya education loan samajhna ho—kuch bhi puchiye in Hindi, English, ya Hinglish!"
                )
            )
        }
        initializeDefaultConnectedAccountsIfEmpty()

        val existingSub = dao.getUserSubscription()
        if (existingSub == null) {
            dao.saveUserSubscription(
                com.example.data.model.UserSubscriptionEntity(
                    id = 1L,
                    planId = "free",
                    planName = "Bindora Free",
                    billingCycle = "FREE",
                    status = "ACTIVE"
                )
            )
        }
    }

    suspend fun getSubscription(): com.example.data.model.UserSubscriptionEntity? = dao.getUserSubscription()

    suspend fun saveSubscription(subscription: com.example.data.model.UserSubscriptionEntity) {
        dao.saveUserSubscription(subscription)
    }

    suspend fun savePaymentOrder(order: com.example.data.model.PaymentOrderEntity) {
        dao.insertPaymentOrder(order)
    }

    suspend fun updatePaymentOrder(order: com.example.data.model.PaymentOrderEntity) {
        dao.updatePaymentOrder(order)
    }

    suspend fun getPaymentOrderByNumber(orderNumber: String): com.example.data.model.PaymentOrderEntity? {
        return dao.getPaymentOrderByNumber(orderNumber)
    }

    suspend fun saveProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun addTransaction(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    suspend fun saveBudget(budget: BudgetEntity) {
        dao.saveBudget(budget)
    }

    suspend fun addSavingsGoal(goal: SavingsGoalEntity): Long {
        return dao.insertSavingsGoal(goal)
    }

    suspend fun addContribution(goalId: Long, amount: Double, note: String) {
        dao.insertContribution(
            GoalContributionEntity(
                goalId = goalId,
                amount = amount,
                note = note
            )
        )
        val goal = dao.getSavingsGoalById(goalId)
        if (goal != null) {
            val updatedAmount = (goal.savedAmount + amount).coerceAtMost(goal.targetAmount * 1.5)
            dao.updateGoalSavedAmount(goalId, updatedAmount)
        }
    }

    suspend fun updateSavingsGoal(goal: SavingsGoalEntity) {
        dao.updateSavingsGoal(goal)
    }

    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        dao.deleteSavingsGoal(goal)
    }

    fun getContributionsForGoalFlow(goalId: Long): kotlinx.coroutines.flow.Flow<List<GoalContributionEntity>> {
        return dao.getContributionsForGoalFlow(goalId)
    }

    suspend fun saveLoanScenario(scenario: LoanScenarioEntity) {
        dao.insertLoanScenario(scenario)
    }

    suspend fun deleteLoanScenario(scenario: LoanScenarioEntity) {
        dao.deleteLoanScenario(scenario)
    }

    suspend fun saveLessonProgress(lessonId: String, score: Int, total: Int) {
        dao.saveLessonProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                isCompleted = true,
                quizScore = score,
                totalQuestions = total,
                completedAtMillis = System.currentTimeMillis()
            )
        )
    }

    // Paper Trading operations
    suspend fun executePaperOrder(
        symbol: String,
        companyName: String,
        isBuy: Boolean,
        quantity: Int,
        price: Double
    ): Pair<Boolean, String> {
        val account = dao.getPaperAccount() ?: PaperAccountEntity()
        val totalCost = quantity * price

        if (isBuy) {
            if (account.cashBalance < totalCost) {
                return Pair(false, "Insufficient virtual cash! Need ₹${"%,.2f".format(totalCost)}, available: ₹${"%,.2f".format(account.cashBalance)}")
            }
            val existingHolding = dao.getHolding(symbol)
            val newQuantity = (existingHolding?.quantity ?: 0) + quantity
            val newTotalCost = ((existingHolding?.avgBuyPrice ?: 0.0) * (existingHolding?.quantity ?: 0)) + totalCost
            val newAvgPrice = newTotalCost / newQuantity

            dao.saveHolding(
                PaperHoldingEntity(
                    symbol = symbol,
                    companyName = companyName,
                    quantity = newQuantity,
                    avgBuyPrice = newAvgPrice,
                    currentPrice = price
                )
            )
            dao.savePaperAccount(
                account.copy(cashBalance = account.cashBalance - totalCost)
            )
            dao.insertOrder(
                PaperOrderEntity(
                    symbol = symbol,
                    companyName = companyName,
                    orderType = "BUY",
                    quantity = quantity,
                    price = price,
                    totalAmount = totalCost
                )
            )
            return Pair(true, "Successfully bought $quantity shares of $symbol at ₹${"%,.2f".format(price)}")
        } else {
            val existingHolding = dao.getHolding(symbol)
            if (existingHolding == null || existingHolding.quantity < quantity) {
                return Pair(false, "You only own ${existingHolding?.quantity ?: 0} shares of $symbol")
            }
            val profitOrLoss = (price - existingHolding.avgBuyPrice) * quantity
            val remainingQty = existingHolding.quantity - quantity

            if (remainingQty == 0) {
                dao.deleteHolding(existingHolding)
            } else {
                dao.saveHolding(
                    existingHolding.copy(
                        quantity = remainingQty,
                        currentPrice = price
                    )
                )
            }
            dao.savePaperAccount(
                account.copy(
                    cashBalance = account.cashBalance + totalCost,
                    totalRealizedPnl = account.totalRealizedPnl + profitOrLoss
                )
            )
            dao.insertOrder(
                PaperOrderEntity(
                    symbol = symbol,
                    companyName = companyName,
                    orderType = "SELL",
                    quantity = quantity,
                    price = price,
                    totalAmount = totalCost
                )
            )
            val pnlText = if (profitOrLoss >= 0) "+₹${"%,.2f".format(profitOrLoss)} profit" else "-₹${"%,.2f".format(-profitOrLoss)} loss"
            return Pair(true, "Sold $quantity shares of $symbol ($pnlText)")
        }
    }

    suspend fun resetPaperAccount() {
        dao.clearHoldings()
        dao.clearOrders()
        dao.savePaperAccount(
            PaperAccountEntity(
                id = 1L,
                cashBalance = 100000.0,
                initialBalance = 100000.0,
                totalRealizedPnl = 0.0
            )
        )
    }

    // Chat AI Coach
    suspend fun addChatMessage(sender: String, text: String) {
        dao.insertChatMessage(
            ChatMessageEntity(
                sender = sender,
                text = text
            )
        )
    }

    suspend fun clearChat() {
        dao.clearChatMessages()
    }

    suspend fun clearAllUserData() {
        dao.clearAllTransactions()
        dao.clearAllBudgets()
        dao.clearAllSavingsGoals()
        dao.clearAllGoalContributions()
        dao.clearAllLoanScenarios()
        dao.clearHoldings()
        dao.clearOrders()
        dao.clearLessonProgress()
        dao.clearChatMessages()
        dao.clearConnectedAccounts()
        dao.clearImportedTransactions()
        dao.clearMerchantMappings()
        dao.clearSyncAuditLogs()
        dao.clearUserProfile()
    }

    // Auto-Tracking: Flows
    val connectedAccountsFlow: Flow<List<com.example.data.model.ConnectedAccountEntity>> = dao.getAllConnectedAccountsFlow()
    val allImportedTransactionsFlow: Flow<List<com.example.data.model.ImportedTransactionEntity>> = dao.getAllImportedTransactionsFlow()
    val pendingReviewCountFlow: Flow<Int> = dao.getPendingReviewCountFlow()
    val merchantMappingsFlow: Flow<List<com.example.data.model.MerchantCategoryMappingEntity>> = dao.getAllMerchantMappingsFlow()
    val syncAuditLogsFlow: Flow<List<com.example.data.model.SyncAuditLogEntity>> = dao.getSyncAuditLogsFlow()

    fun getImportedTransactionsByStatus(status: String): Flow<List<com.example.data.model.ImportedTransactionEntity>> {
        return if (status == "ALL") dao.getAllImportedTransactionsFlow() else dao.getImportedTransactionsByStatusFlow(status)
    }

    suspend fun initializeDefaultConnectedAccountsIfEmpty() {
        val existing = dao.getAllConnectedAccountsFlow()
        // Check if accounts exist by trying to fetch one
        val first = dao.getConnectedAccountByProviderId("notification_listener")
        if (first == null) {
            dao.insertConnectedAccount(
                com.example.data.model.ConnectedAccountEntity(
                    providerId = "notification_listener",
                    providerName = "Payment SMS & UPI Alert Listener",
                    providerType = "NOTIFICATION_LISTENER",
                    accountNumberMasked = "On-Device Android UPI Apps",
                    connectionStatus = "Connected",
                    lastSyncMillis = System.currentTimeMillis() - 1000 * 60 * 30, // 30 mins ago
                    syncMethod = "Local Regex Parsing (Zero-Cloud)",
                    dataPermissionsGranted = "Read-only financial debit/credit alerts. Filtered on-device.",
                    isDirectAppNoticeOnly = false
                )
            )
            dao.insertConnectedAccount(
                com.example.data.model.ConnectedAccountEntity(
                    providerId = "csv_statement_import",
                    providerName = "Bank Statement Importer (CSV)",
                    providerType = "STATEMENT_IMPORT",
                    accountNumberMasked = "SBI, HDFC, ICICI, Axis Statements",
                    connectionStatus = "Connected",
                    lastSyncMillis = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
                    syncMethod = "User Selected File Parsing",
                    dataPermissionsGranted = "Local memory file parsing. No credentials required.",
                    isDirectAppNoticeOnly = false
                )
            )
            dao.insertConnectedAccount(
                com.example.data.model.ConnectedAccountEntity(
                    providerId = "rbi_account_aggregator",
                    providerName = "RBI Account Aggregator (AA)",
                    providerType = "ACCOUNT_AGGREGATOR",
                    accountNumberMasked = "Requires AA Consent Handle (@finvu/@setu)",
                    connectionStatus = "Action required",
                    lastSyncMillis = null,
                    syncMethod = "RBI Regulated Digital Consent Flow (FIP/FIU)",
                    dataPermissionsGranted = "Read-only digitally signed banking statements upon user OTP consent.",
                    isDirectAppNoticeOnly = false
                )
            )
            dao.insertConnectedAccount(
                com.example.data.model.ConnectedAccountEntity(
                    providerId = "direct_upi_gpay_phonepe",
                    providerName = "Google Pay & PhonePe Direct Notice",
                    providerType = "DIRECT_UPI_NOTICE",
                    accountNumberMasked = "Proprietary Server API (Not Publicly Exposed)",
                    connectionStatus = "Disconnected",
                    lastSyncMillis = null,
                    syncMethod = "Enterprise Closed Banking API",
                    dataPermissionsGranted = "None. Handled via On-Device Payment Alert Listener.",
                    isDirectAppNoticeOnly = true
                )
            )
        }
    }

    suspend fun connectAccount(providerId: String) {
        val account = dao.getConnectedAccountByProviderId(providerId)
        if (account != null) {
            dao.updateConnectedAccount(
                account.copy(
                    connectionStatus = "Connected",
                    lastSyncMillis = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun disconnectAccount(providerId: String) {
        val account = dao.getConnectedAccountByProviderId(providerId)
        if (account != null) {
            dao.updateConnectedAccount(
                account.copy(
                    connectionStatus = "Disconnected"
                )
            )
            dao.insertSyncAuditLog(
                com.example.data.model.SyncAuditLogEntity(
                    providerId = providerId,
                    providerName = account.providerName,
                    itemsIngested = 0,
                    duplicatesDetected = 0,
                    statusMessage = "Disconnected by user."
                )
            )
        }
    }

    suspend fun importTransactionsFromCsv(
        csvContent: String,
        sourceName: String = "Bank Statement"
    ): com.example.data.ingestion.SyncResult {
        val parseResult = com.example.data.ingestion.BankStatementParser.parseCsv(csvContent)
        if (!parseResult.success || parseResult.rows.isEmpty()) {
            return com.example.data.ingestion.SyncResult(
                success = false,
                itemsIngested = 0,
                duplicatesDetected = 0,
                message = parseResult.errorMessages.firstOrNull() ?: "Unable to parse CSV. Please verify statement format."
            )
        }

        // Fetch existing transactions for deduplication
        val existingImported = dao.getAllImportedTransactionsFlow()
        val allImportedList = mutableListOf<com.example.data.model.ImportedTransactionEntity>()
        // Read recent manual transactions
        val startWindow = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        val manualList = mutableListOf<TransactionEntity>()
        // Collect current snapshot
        try {
            manualList.addAll(dao.getAllTransactionsFlow().first())
            allImportedList.addAll(dao.getAllImportedTransactionsFlow().first())
        } catch (_: Exception) {}

        // Fetch learned merchant mappings
        val learnedMap = mutableMapOf<String, String>()
        try {
            dao.getAllMerchantMappingsFlow().first().forEach {
                learnedMap[it.merchantPattern] = it.category
            }
        } catch (_: Exception) {}

        var ingestedCount = 0
        var duplicatesCount = 0
        val newImportedEntities = mutableListOf<com.example.data.model.ImportedTransactionEntity>()

        for (row in parseResult.rows) {
            val classified = com.example.data.ingestion.CategoryClassifier.classify(
                rawDescription = row.description,
                direction = row.direction,
                learnedMappings = learnedMap
            )

            // Deduplication Check
            val dedupe = com.example.data.ingestion.DeduplicationEngine.evaluate(
                candidateAmount = row.amount,
                candidateDirection = row.direction,
                candidateTimestamp = row.dateMillis,
                candidateRef = row.reference,
                candidateMerchant = classified.cleanMerchant,
                existingImported = allImportedList,
                existingManual = manualList
            )

            val status = if (dedupe.isDuplicate) {
                duplicatesCount++
                if (dedupe.isExactMatch) "EXCLUDED" else "POSSIBLE_DUPLICATE"
            } else {
                ingestedCount++
                "NEEDS_REVIEW"
            }

            val entity = com.example.data.model.ImportedTransactionEntity(
                sourceProviderId = "csv_statement_import",
                sourceProviderName = sourceName,
                sourceAccountMasked = "CSV Import",
                externalReference = row.reference,
                rawAmount = row.amount,
                currency = "₹",
                direction = row.direction,
                rawDescription = row.description,
                cleanMerchant = classified.cleanMerchant,
                suggestedCategory = classified.category,
                transactionTimestamp = row.dateMillis,
                bookingTimestamp = row.dateMillis,
                ingestionMethod = "CSV_IMPORT",
                confidenceScore = classified.confidenceScore,
                status = status,
                duplicateOfId = dedupe.matchedImportedId,
                duplicateReason = dedupe.reason
            )

            val insertedId = dao.insertImportedTransaction(entity)
            allImportedList.add(entity.copy(id = insertedId))
            newImportedEntities.add(entity.copy(id = insertedId))
        }

        // Update connected account sync time
        val account = dao.getConnectedAccountByProviderId("csv_statement_import")
        if (account != null) {
            dao.updateConnectedAccount(
                account.copy(
                    lastSyncMillis = System.currentTimeMillis(),
                    connectionStatus = "Connected"
                )
            )
        }

        // Record audit log
        dao.insertSyncAuditLog(
            com.example.data.model.SyncAuditLogEntity(
                providerId = "csv_statement_import",
                providerName = sourceName,
                itemsIngested = ingestedCount,
                duplicatesDetected = duplicatesCount,
                statusMessage = "Imported ${parseResult.totalRowsParsed} rows ($ingestedCount new, $duplicatesCount duplicates detected)."
            )
        )

        return com.example.data.ingestion.SyncResult(
            success = true,
            itemsIngested = ingestedCount,
            duplicatesDetected = duplicatesCount,
            message = "Successfully parsed ${parseResult.totalRowsParsed} transactions ($ingestedCount ready for review).",
            newTransactions = newImportedEntities
        )
    }

    suspend fun processIncomingNotification(
        title: String,
        text: String,
        sourcePackage: String = "com.google.android.apps.nbu.paisa.user"
    ): com.example.data.model.ImportedTransactionEntity? {
        val parsed = com.example.data.ingestion.NotificationParser.parseNotification(title, text) ?: return null

        // Fetch learned mappings
        val learnedMap = mutableMapOf<String, String>()
        try {
            dao.getAllMerchantMappingsFlow().first().forEach {
                learnedMap[it.merchantPattern] = it.category
            }
        } catch (_: Exception) {}

        val classified = com.example.data.ingestion.CategoryClassifier.classify(
            rawDescription = "${parsed.merchantOrSender} ${parsed.rawText}",
            direction = parsed.direction,
            learnedMappings = learnedMap
        )

        val allImportedList = mutableListOf<com.example.data.model.ImportedTransactionEntity>()
        val manualList = mutableListOf<TransactionEntity>()
        try {
            manualList.addAll(dao.getAllTransactionsFlow().first())
            allImportedList.addAll(dao.getAllImportedTransactionsFlow().first())
        } catch (_: Exception) {}

        val dedupe = com.example.data.ingestion.DeduplicationEngine.evaluate(
            candidateAmount = parsed.amount,
            candidateDirection = parsed.direction,
            candidateTimestamp = parsed.timestampMillis,
            candidateRef = parsed.externalReference,
            candidateMerchant = classified.cleanMerchant,
            existingImported = allImportedList,
            existingManual = manualList
        )

        val status = when {
            dedupe.isExactMatch -> "EXCLUDED"
            dedupe.isDuplicate -> "POSSIBLE_DUPLICATE"
            else -> "NEEDS_REVIEW"
        }

        val entity = com.example.data.model.ImportedTransactionEntity(
            sourceProviderId = "notification_listener",
            sourceProviderName = if (title.contains("PhonePe", ignoreCase = true)) "PhonePe UPI" else "Payment Alert Listener",
            sourceAccountMasked = parsed.maskedAccount.ifBlank { "UPI" },
            externalReference = parsed.externalReference,
            rawAmount = parsed.amount,
            currency = "₹",
            direction = parsed.direction,
            rawDescription = parsed.rawText,
            cleanMerchant = classified.cleanMerchant,
            suggestedCategory = classified.category,
            transactionTimestamp = parsed.timestampMillis,
            bookingTimestamp = parsed.timestampMillis,
            ingestionMethod = "NOTIFICATION",
            confidenceScore = classified.confidenceScore,
            status = status,
            duplicateOfId = dedupe.matchedImportedId,
            duplicateReason = dedupe.reason
        )

        val id = dao.insertImportedTransaction(entity)

        // Log audit
        dao.insertSyncAuditLog(
            com.example.data.model.SyncAuditLogEntity(
                providerId = "notification_listener",
                providerName = "Payment SMS & UPI Alert Listener",
                itemsIngested = if (status == "NEEDS_REVIEW") 1 else 0,
                duplicatesDetected = if (dedupe.isDuplicate) 1 else 0,
                statusMessage = "Captured payment of ₹${parsed.amount} to ${classified.cleanMerchant}."
            )
        )

        return entity.copy(id = id)
    }

    suspend fun confirmImportedTransaction(
        importedTxId: Long,
        finalCategory: String,
        finalMerchant: String
    ) {
        val imported = dao.getImportedTransactionById(importedTxId) ?: return

        // 1. Insert into actual transactions table
        val isIncome = imported.direction.equals("CREDIT", ignoreCase = true)
        val transactionEntity = TransactionEntity(
            title = finalMerchant.ifBlank { imported.cleanMerchant },
            amount = imported.rawAmount,
            isIncome = isIncome,
            category = finalCategory,
            dateMillis = imported.transactionTimestamp,
            notes = "Auto-imported via ${imported.sourceProviderName}${if (imported.externalReference.isNotBlank()) " [Ref: ${imported.externalReference}]" else ""}",
            paymentMode = if (imported.sourceAccountMasked.contains("UPI", ignoreCase = true)) "UPI" else "Card/NetBanking"
        )
        val newTxId = dao.insertTransaction(transactionEntity)

        // 2. Mark imported transaction as CONFIRMED
        dao.updateImportedTransaction(
            imported.copy(
                status = "CONFIRMED",
                userCategory = finalCategory,
                cleanMerchant = finalMerchant.ifBlank { imported.cleanMerchant },
                matchedTransactionId = newTxId
            )
        )

        // 3. Learn merchant mapping for future automation
        learnMerchantMapping(finalMerchant.ifBlank { imported.cleanMerchant }, finalCategory)
    }

    suspend fun excludeImportedTransaction(importedTxId: Long, reason: String = "Excluded by user") {
        val imported = dao.getImportedTransactionById(importedTxId) ?: return
        dao.updateImportedTransaction(
            imported.copy(
                status = "EXCLUDED",
                notes = reason
            )
        )
    }

    suspend fun resolveDuplicate(importedTxId: Long, keepExisting: Boolean) {
        val imported = dao.getImportedTransactionById(importedTxId) ?: return
        if (keepExisting) {
            dao.updateImportedTransaction(
                imported.copy(
                    status = "EXCLUDED",
                    notes = "Marked as duplicate of #${imported.duplicateOfId}"
                )
            )
        } else {
            // Keep this one instead: change to NEEDS_REVIEW so user can confirm
            dao.updateImportedTransaction(
                imported.copy(
                    status = "NEEDS_REVIEW",
                    duplicateOfId = null,
                    duplicateReason = "Re-opened by user (override duplicate detection)"
                )
            )
        }
    }

    suspend fun learnMerchantMapping(merchant: String, category: String) {
        val clean = com.example.data.ingestion.CategoryClassifier.cleanMerchantName(merchant).uppercase(Locale.ROOT)
        if (clean.length >= 3) {
            dao.insertMerchantMapping(
                com.example.data.model.MerchantCategoryMappingEntity(
                    merchantPattern = clean,
                    category = category,
                    userConfirmed = true
                )
            )
        }
    }
}

