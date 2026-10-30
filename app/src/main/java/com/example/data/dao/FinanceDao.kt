package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BudgetEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.GoalContributionEntity
import com.example.data.model.LessonProgressEntity
import com.example.data.model.LoanScenarioEntity
import com.example.data.model.PaperAccountEntity
import com.example.data.model.PaperHoldingEntity
import com.example.data.model.PaperOrderEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis ORDER BY dateMillis DESC")
    fun getTransactionsInRangeFlow(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    // Budgets
    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey LIMIT 1")
    fun getBudgetForMonthFlow(monthKey: String): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey LIMIT 1")
    suspend fun getBudgetForMonth(monthKey: String): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudget(budget: BudgetEntity)

    // Savings Goals
    @Query("SELECT * FROM savings_goals WHERE isArchived = 0 ORDER BY id DESC")
    fun getActiveSavingsGoalsFlow(): Flow<List<SavingsGoalEntity>>

    @Query("SELECT * FROM savings_goals WHERE id = :goalId LIMIT 1")
    suspend fun getSavingsGoalById(goalId: Long): SavingsGoalEntity?

    @Query("UPDATE savings_goals SET savedAmount = :newSavedAmount WHERE id = :goalId")
    suspend fun updateGoalSavedAmount(goalId: Long, newSavedAmount: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsGoal(goal: SavingsGoalEntity): Long

    @Update
    suspend fun updateSavingsGoal(goal: SavingsGoalEntity)

    @Delete
    suspend fun deleteSavingsGoal(goal: SavingsGoalEntity)

    @Query("DELETE FROM savings_goals")
    suspend fun clearAllSavingsGoals()

    @Query("SELECT * FROM goal_contributions WHERE goalId = :goalId ORDER BY dateMillis DESC")
    fun getContributionsForGoalFlow(goalId: Long): Flow<List<GoalContributionEntity>>

    @Insert
    suspend fun insertContribution(contribution: GoalContributionEntity): Long

    @Query("DELETE FROM goal_contributions")
    suspend fun clearAllGoalContributions()

    @Query("DELETE FROM budgets")
    suspend fun clearAllBudgets()

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    // Loan Scenarios
    @Query("SELECT * FROM loan_scenarios ORDER BY savedAtMillis DESC")
    fun getAllLoanScenariosFlow(): Flow<List<LoanScenarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanScenario(scenario: LoanScenarioEntity): Long

    @Delete
    suspend fun deleteLoanScenario(scenario: LoanScenarioEntity)

    @Query("DELETE FROM loan_scenarios")
    suspend fun clearAllLoanScenarios()

    // Paper Trading
    @Query("SELECT * FROM paper_account WHERE id = 1 LIMIT 1")
    fun getPaperAccountFlow(): Flow<PaperAccountEntity?>

    @Query("SELECT * FROM paper_account WHERE id = 1 LIMIT 1")
    suspend fun getPaperAccount(): PaperAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePaperAccount(account: PaperAccountEntity)

    @Query("SELECT * FROM paper_holdings ORDER BY symbol ASC")
    fun getAllHoldingsFlow(): Flow<List<PaperHoldingEntity>>

    @Query("SELECT * FROM paper_holdings WHERE symbol = :symbol LIMIT 1")
    suspend fun getHolding(symbol: String): PaperHoldingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHolding(holding: PaperHoldingEntity)

    @Delete
    suspend fun deleteHolding(holding: PaperHoldingEntity)

    @Query("DELETE FROM paper_holdings")
    suspend fun clearHoldings()

    @Query("SELECT * FROM paper_orders ORDER BY timestampMillis DESC")
    fun getAllOrdersFlow(): Flow<List<PaperOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: PaperOrderEntity): Long

    @Query("DELETE FROM paper_orders")
    suspend fun clearOrders()

    // Lesson Progress
    @Query("SELECT * FROM lesson_progress")
    fun getAllLessonProgressFlow(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessonProgress(progress: LessonProgressEntity)

    @Query("DELETE FROM lesson_progress")
    suspend fun clearLessonProgress()

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestampMillis ASC")
    fun getChatMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    // Connected Accounts & Sources
    @Query("SELECT * FROM connected_accounts ORDER BY id ASC")
    fun getAllConnectedAccountsFlow(): Flow<List<com.example.data.model.ConnectedAccountEntity>>

    @Query("SELECT * FROM connected_accounts WHERE providerId = :providerId LIMIT 1")
    suspend fun getConnectedAccountByProviderId(providerId: String): com.example.data.model.ConnectedAccountEntity?

    @Query("SELECT * FROM connected_accounts WHERE id = :id LIMIT 1")
    suspend fun getConnectedAccountById(id: Long): com.example.data.model.ConnectedAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnectedAccount(account: com.example.data.model.ConnectedAccountEntity): Long

    @Update
    suspend fun updateConnectedAccount(account: com.example.data.model.ConnectedAccountEntity)

    @Delete
    suspend fun deleteConnectedAccount(account: com.example.data.model.ConnectedAccountEntity)

    @Query("DELETE FROM connected_accounts")
    suspend fun clearConnectedAccounts()

    // Imported Transactions
    @Query("SELECT * FROM imported_transactions ORDER BY transactionTimestamp DESC")
    fun getAllImportedTransactionsFlow(): Flow<List<com.example.data.model.ImportedTransactionEntity>>

    @Query("SELECT * FROM imported_transactions WHERE status = :status ORDER BY transactionTimestamp DESC")
    fun getImportedTransactionsByStatusFlow(status: String): Flow<List<com.example.data.model.ImportedTransactionEntity>>

    @Query("SELECT COUNT(*) FROM imported_transactions WHERE status = 'NEEDS_REVIEW' OR status = 'POSSIBLE_DUPLICATE'")
    fun getPendingReviewCountFlow(): Flow<Int>

    @Query("SELECT * FROM imported_transactions WHERE id = :id LIMIT 1")
    suspend fun getImportedTransactionById(id: Long): com.example.data.model.ImportedTransactionEntity?

    @Query("SELECT * FROM imported_transactions WHERE externalReference = :ref AND externalReference != '' LIMIT 1")
    suspend fun findImportedByExternalReference(ref: String): com.example.data.model.ImportedTransactionEntity?

    @Query("SELECT * FROM imported_transactions WHERE rawAmount = :amount AND direction = :direction AND transactionTimestamp BETWEEN :startMillis AND :endMillis LIMIT 5")
    suspend fun findPotentialDuplicateImported(amount: Double, direction: String, startMillis: Long, endMillis: Long): List<com.example.data.model.ImportedTransactionEntity>

    @Query("SELECT * FROM transactions WHERE amount = :amount AND isIncome = :isIncome AND dateMillis BETWEEN :startMillis AND :endMillis LIMIT 5")
    suspend fun findPotentialDuplicateManual(amount: Double, isIncome: Boolean, startMillis: Long, endMillis: Long): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportedTransaction(tx: com.example.data.model.ImportedTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportedTransactions(list: List<com.example.data.model.ImportedTransactionEntity>): List<Long>

    @Update
    suspend fun updateImportedTransaction(tx: com.example.data.model.ImportedTransactionEntity)

    @Delete
    suspend fun deleteImportedTransaction(tx: com.example.data.model.ImportedTransactionEntity)

    @Query("DELETE FROM imported_transactions")
    suspend fun clearImportedTransactions()

    // Merchant Category Mappings
    @Query("SELECT * FROM merchant_category_mappings ORDER BY merchantPattern ASC")
    fun getAllMerchantMappingsFlow(): Flow<List<com.example.data.model.MerchantCategoryMappingEntity>>

    @Query("SELECT * FROM merchant_category_mappings WHERE merchantPattern = :pattern LIMIT 1")
    suspend fun getMerchantMapping(pattern: String): com.example.data.model.MerchantCategoryMappingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantMapping(mapping: com.example.data.model.MerchantCategoryMappingEntity): Long

    @Query("DELETE FROM merchant_category_mappings")
    suspend fun clearMerchantMappings()

    // Sync Audit Logs
    @Query("SELECT * FROM sync_audit_logs ORDER BY timestampMillis DESC LIMIT 50")
    fun getSyncAuditLogsFlow(): Flow<List<com.example.data.model.SyncAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncAuditLog(log: com.example.data.model.SyncAuditLogEntity): Long

    @Query("DELETE FROM sync_audit_logs")
    suspend fun clearSyncAuditLogs()

    // Subscriptions
    @Query("SELECT * FROM user_subscriptions WHERE id = 1 LIMIT 1")
    fun getUserSubscriptionFlow(): Flow<com.example.data.model.UserSubscriptionEntity?>

    @Query("SELECT * FROM user_subscriptions WHERE id = 1 LIMIT 1")
    suspend fun getUserSubscription(): com.example.data.model.UserSubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSubscription(subscription: com.example.data.model.UserSubscriptionEntity)

    // Payment Orders
    @Query("SELECT * FROM payment_orders ORDER BY createdAtMillis DESC")
    fun getAllPaymentOrdersFlow(): Flow<List<com.example.data.model.PaymentOrderEntity>>

    @Query("SELECT * FROM payment_orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getPaymentOrderByNumber(orderNumber: String): com.example.data.model.PaymentOrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentOrder(order: com.example.data.model.PaymentOrderEntity)

    @Update
    suspend fun updatePaymentOrder(order: com.example.data.model.PaymentOrderEntity)
}
