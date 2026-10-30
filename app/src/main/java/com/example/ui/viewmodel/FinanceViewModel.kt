package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AcademyLesson
import com.example.data.model.AICoachEngine
import com.example.data.model.BudgetEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.FinanceConstants
import com.example.data.model.LoanCalculationResult
import com.example.data.model.LoanCalculatorHelper
import com.example.data.model.LoanScenarioEntity
import com.example.data.model.PaperAccountEntity
import com.example.data.model.PaperHoldingEntity
import com.example.data.model.PaperOrderEntity
import com.example.data.model.PaperTradingData
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.SimulatedStock
import com.example.data.model.TransactionEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.AcademyRepository
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.auth.AuthResult
import com.example.data.auth.AuthStatus
import com.example.data.auth.SupabaseAuthManager
import com.example.data.auth.SupabaseSession
import java.util.Locale
import kotlin.random.Random

enum class MainScreen {
    AUTH_WELCOME,
    AUTH_EMAIL_ENTRY,
    AUTH_OTP_VERIFY,
    ONBOARDING,
    HOME,
    TRANSACTIONS_BUDGET,
    LEARN,
    AI_COACH,
    TOOLS_SIM,
    PROFILE,
    CONNECTED_ACCOUNTS,
    IMPORTED_TRANSACTIONS,
    SUBSCRIPTION,
    UPI_CHECKOUT
}

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    val userProfile: StateFlow<UserProfileEntity?>
    val transactions: StateFlow<List<TransactionEntity>>
    val currentBudget: StateFlow<BudgetEntity?>
    val savingsGoals: StateFlow<List<SavingsGoalEntity>>
    val loanScenarios: StateFlow<List<LoanScenarioEntity>>
    val paperAccount: StateFlow<PaperAccountEntity?>
    val paperHoldings: StateFlow<List<PaperHoldingEntity>>
    val paperOrders: StateFlow<List<PaperOrderEntity>>
    val lessonProgress: StateFlow<List<com.example.data.model.LessonProgressEntity>>
    val chatMessages: StateFlow<List<ChatMessageEntity>>

    val connectedAccounts: StateFlow<List<com.example.data.model.ConnectedAccountEntity>>
    val importedTransactions: StateFlow<List<com.example.data.model.ImportedTransactionEntity>>
    val pendingReviewCount: StateFlow<Int>
    val syncAuditLogs: StateFlow<List<com.example.data.model.SyncAuditLogEntity>>

    val userSubscription: StateFlow<com.example.data.model.UserSubscriptionEntity?>
    val isPremiumUser: StateFlow<Boolean>
    val paymentOrders: StateFlow<List<com.example.data.model.PaymentOrderEntity>>

    val upiPaymentManager = com.example.data.payment.UpiPaymentManager(application)
    private val _selectedPlan = MutableStateFlow(com.example.data.model.SubscriptionPlansCatalog.PLANS[1])
    val selectedPlan = _selectedPlan.asStateFlow()

    private val _selectedBillingCycle = MutableStateFlow(com.example.data.model.BillingCycle.MONTHLY)
    val selectedBillingCycle = _selectedBillingCycle.asStateFlow()

    private val _currentCheckoutOrder = MutableStateFlow<com.example.data.model.PaymentOrderEntity?>(null)
    val currentCheckoutOrder = _currentCheckoutOrder.asStateFlow()

    private val _checkoutStatus = MutableStateFlow(com.example.data.model.PaymentOrderStatus.PENDING)
    val checkoutStatus = _checkoutStatus.asStateFlow()

    private val _checkoutStatusMessage = MutableStateFlow<String?>(null)
    val checkoutStatusMessage = _checkoutStatusMessage.asStateFlow()

    private val _isVerifyingPayment = MutableStateFlow(false)
    val isVerifyingPayment = _isVerifyingPayment.asStateFlow()

    private val _installedUpiApps = MutableStateFlow(upiPaymentManager.getInstalledUpiApps())
    val installedUpiApps = _installedUpiApps.asStateFlow()

    private val _selectedImportFilter = MutableStateFlow("ALL")
    val selectedImportFilter = _selectedImportFilter.asStateFlow()

    private val _importActionFeedback = MutableStateFlow<String?>(null)
    val importActionFeedback = _importActionFeedback.asStateFlow()

    private val _showStatementImportDialog = MutableStateFlow(false)
    val showStatementImportDialog = _showStatementImportDialog.asStateFlow()

    val supabaseAuthManager = SupabaseAuthManager(application)
    val authStatus: StateFlow<AuthStatus> = supabaseAuthManager.authStatus
    val currentSession: StateFlow<SupabaseSession?> = supabaseAuthManager.currentSession
    val pendingEmail: StateFlow<String?> = supabaseAuthManager.pendingEmail
    val authErrorMessage: StateFlow<String?> = supabaseAuthManager.lastErrorMessage

    private val _activeScreen = MutableStateFlow(MainScreen.AUTH_WELCOME)
    val activeScreen = _activeScreen.asStateFlow()

    private val _screenBackStack = mutableListOf<MainScreen>()

    private val _simulatedStocks = MutableStateFlow(PaperTradingData.INITIAL_STOCKS)
    val simulatedStocks = _simulatedStocks.asStateFlow()

    private val _isAITyping = MutableStateFlow(false)
    val isAITyping = _isAITyping.asStateFlow()

    private val _toolsTab = MutableStateFlow(0) // 0: Loan Calculator, 1: Paper Trading
    val toolsTab = _toolsTab.asStateFlow()

    private val _transactionsTab = MutableStateFlow(0) // 0: Transactions, 1: Budgets, 2: Goals
    val transactionsTab = _transactionsTab.asStateFlow()

    // Modals & Dialogs
    private val _selectedLesson = MutableStateFlow<AcademyLesson?>(null)
    val selectedLesson = _selectedLesson.asStateFlow()

    private val _showAddTransactionDialog = MutableStateFlow(false)
    val showAddTransactionDialog = _showAddTransactionDialog.asStateFlow()

    private val _transactionToEdit = MutableStateFlow<TransactionEntity?>(null)
    val transactionToEdit = _transactionToEdit.asStateFlow()

    private val _showAddGoalDialog = MutableStateFlow(false)
    val showAddGoalDialog = _showAddGoalDialog.asStateFlow()

    private val _goalToEdit = MutableStateFlow<SavingsGoalEntity?>(null)
    val goalToEdit = _goalToEdit.asStateFlow()

    private val _showOrderDialogStock = MutableStateFlow<SimulatedStock?>(null)
    val showOrderDialogStock = _showOrderDialogStock.asStateFlow()

    private val _orderDialogIsBuy = MutableStateFlow(true)
    val orderDialogIsBuy = _orderDialogIsBuy.asStateFlow()

    private val _orderFeedbackMessage = MutableStateFlow<String?>(null)
    val orderFeedbackMessage = _orderFeedbackMessage.asStateFlow()

    // Loan live calculator state
    private val _currentLoanResult = MutableStateFlow<LoanCalculationResult?>(null)
    val currentLoanResult = _currentLoanResult.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FinanceRepository(db.financeDao())

        userProfile = repository.userProfileFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
        transactions = repository.allTransactionsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        currentBudget = repository.getBudgetForMonth(repository.getCurrentMonthKey()).stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
        savingsGoals = repository.activeSavingsGoalsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        loanScenarios = repository.loanScenariosFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        paperAccount = repository.paperAccountFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )
        paperHoldings = repository.paperHoldingsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        paperOrders = repository.paperOrdersFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        lessonProgress = repository.lessonProgressFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        chatMessages = repository.chatMessagesFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        connectedAccounts = repository.connectedAccountsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        importedTransactions = repository.allImportedTransactionsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )
        pendingReviewCount = repository.pendingReviewCountFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            0
        )
        syncAuditLogs = repository.syncAuditLogsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

        // Determine initial screen based on persisted Supabase Auth session
        val initialStatus = supabaseAuthManager.authStatus.value
        _activeScreen.value = when (initialStatus) {
            AuthStatus.AUTHENTICATED -> MainScreen.HOME
            AuthStatus.ONBOARDING_REQUIRED -> MainScreen.ONBOARDING
            AuthStatus.OTP_SENT -> MainScreen.AUTH_OTP_VERIFY
            else -> MainScreen.AUTH_WELCOME
        }

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
            // Set initial loan calculation
            calculateLoan(750000.0, 9.5, 10, 54, true, 0.0)
            startStockPriceTicker()
        }

        viewModelScope.launch {
            supabaseAuthManager.authStatus.collect { status ->
                when (status) {
                    AuthStatus.AUTHENTICATED -> {
                        if (_activeScreen.value == MainScreen.AUTH_WELCOME ||
                            _activeScreen.value == MainScreen.AUTH_EMAIL_ENTRY ||
                            _activeScreen.value == MainScreen.AUTH_OTP_VERIFY ||
                            _activeScreen.value == MainScreen.ONBOARDING
                        ) {
                            _activeScreen.value = MainScreen.HOME
                        }
                    }
                    AuthStatus.ONBOARDING_REQUIRED -> {
                        _activeScreen.value = MainScreen.ONBOARDING
                    }
                    AuthStatus.OTP_SENT -> {
                        _activeScreen.value = MainScreen.AUTH_OTP_VERIFY
                    }
                    AuthStatus.SIGNED_OUT -> {
                        if (_activeScreen.value != MainScreen.AUTH_WELCOME &&
                            _activeScreen.value != MainScreen.AUTH_EMAIL_ENTRY
                        ) {
                            _activeScreen.value = MainScreen.AUTH_WELCOME
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun startStockPriceTicker() {
        viewModelScope.launch {
            while (true) {
                delay(4000)
                val updated = _simulatedStocks.value.map { stock ->
                    val deltaPercent = (Random.nextDouble(-0.8, 0.8))
                    val newPrice = BigDecimal(stock.currentPrice * (1.0 + deltaPercent / 100.0))
                        .setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                    val totalChange = BigDecimal(((newPrice - stock.basePrice) / stock.basePrice) * 100.0)
                        .setScale(2, RoundingMode.HALF_UP)
                        .toDouble()
                    stock.copy(currentPrice = newPrice, changePercent = totalChange)
                }
                _simulatedStocks.value = updated
            }
        }
    }

    fun navigateTo(screen: MainScreen) {
        if (_activeScreen.value != screen) {
            _screenBackStack.add(_activeScreen.value)
            _activeScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (_activeScreen.value == MainScreen.AUTH_OTP_VERIFY) {
            _activeScreen.value = MainScreen.AUTH_EMAIL_ENTRY
            return true
        }
        if (_activeScreen.value == MainScreen.AUTH_EMAIL_ENTRY) {
            _activeScreen.value = MainScreen.AUTH_WELCOME
            return true
        }
        if (_screenBackStack.isNotEmpty()) {
            _activeScreen.value = _screenBackStack.removeAt(_screenBackStack.size - 1)
            return true
        }
        if (_activeScreen.value != MainScreen.HOME && _activeScreen.value != MainScreen.AUTH_WELCOME) {
            _activeScreen.value = MainScreen.HOME
            return true
        }
        return false
    }

    fun setToolsTab(tab: Int) {
        _toolsTab.value = tab
    }

    fun setTransactionsTab(tab: Int) {
        _transactionsTab.value = tab
    }

    fun openLesson(lesson: AcademyLesson) {
        _selectedLesson.value = lesson
    }

    fun closeLesson() {
        _selectedLesson.value = null
    }

    fun showAddTransaction() {
        _transactionToEdit.value = null
        _showAddTransactionDialog.value = true
    }

    fun showEditTransaction(transaction: TransactionEntity) {
        _transactionToEdit.value = transaction
        _showAddTransactionDialog.value = true
    }

    fun dismissTransactionDialog() {
        _showAddTransactionDialog.value = false
        _transactionToEdit.value = null
    }

    fun showAddGoal() {
        _goalToEdit.value = null
        _showAddGoalDialog.value = true
    }

    fun showEditGoal(goal: SavingsGoalEntity) {
        _goalToEdit.value = goal
        _showAddGoalDialog.value = true
    }

    fun dismissGoalDialog() {
        _showAddGoalDialog.value = false
        _goalToEdit.value = null
    }

    fun openOrderDialog(stock: SimulatedStock, isBuy: Boolean) {
        _showOrderDialogStock.value = stock
        _orderDialogIsBuy.value = isBuy
        _orderFeedbackMessage.value = null
    }

    fun dismissOrderDialog() {
        _showOrderDialogStock.value = null
        _orderFeedbackMessage.value = null
    }

    fun saveTransaction(
        title: String,
        amount: Double,
        isIncome: Boolean,
        category: String,
        notes: String,
        paymentMode: String,
        idToEdit: Long? = null
    ) {
        viewModelScope.launch {
            if (idToEdit != null && idToEdit > 0) {
                repository.updateTransaction(
                    TransactionEntity(
                        id = idToEdit,
                        title = title,
                        amount = amount,
                        isIncome = isIncome,
                        category = category,
                        notes = notes,
                        paymentMode = paymentMode
                    )
                )
            } else {
                repository.addTransaction(
                    TransactionEntity(
                        title = title,
                        amount = amount,
                        isIncome = isIncome,
                        category = category,
                        notes = notes,
                        paymentMode = paymentMode
                    )
                )
            }
            dismissTransactionDialog()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun updateBudgetLimits(
        total: Double,
        canteen: Double,
        groceries: Double,
        travel: Double,
        books: Double,
        entertainment: Double,
        recharge: Double,
        shopping: Double,
        other: Double
    ) {
        viewModelScope.launch {
            val key = repository.getCurrentMonthKey()
            repository.saveBudget(
                BudgetEntity(
                    monthKey = key,
                    totalLimit = total,
                    canteenLimit = canteen,
                    groceriesLimit = groceries,
                    travelLimit = travel,
                    booksLimit = books,
                    entertainmentLimit = entertainment,
                    rechargeLimit = recharge,
                    shoppingLimit = shopping,
                    otherLimit = other
                )
            )
        }
    }

    fun createSavingsGoal(title: String, targetAmount: Double, category: String, targetDays: Int, initialDeposit: Double = 0.0) {
        viewModelScope.launch {
            val targetMillis = System.currentTimeMillis() + targetDays * 24L * 60 * 60 * 1000
            val goalId = repository.addSavingsGoal(
                SavingsGoalEntity(
                    title = title,
                    targetAmount = targetAmount,
                    savedAmount = initialDeposit.coerceAtLeast(0.0),
                    category = category,
                    targetDateMillis = targetMillis
                )
            )
            if (initialDeposit > 0.0) {
                repository.addContribution(goalId, initialDeposit, "Initial savings deposit")
            }
            dismissGoalDialog()
        }
    }

    fun updateSavingsGoal(goalId: Long, title: String, targetAmount: Double, category: String, targetDays: Int) {
        viewModelScope.launch {
            val targetMillis = System.currentTimeMillis() + targetDays * 24L * 60 * 60 * 1000
            val current = savingsGoals.value.firstOrNull { it.id == goalId }
            if (current != null) {
                repository.updateSavingsGoal(
                    current.copy(
                        title = title,
                        targetAmount = targetAmount,
                        category = category,
                        targetDateMillis = targetMillis
                    )
                )
            }
            dismissGoalDialog()
        }
    }

    fun addSavingsContribution(goal: SavingsGoalEntity, amount: Double, note: String) {
        viewModelScope.launch {
            repository.addContribution(goal.id, amount, note)
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.deleteSavingsGoal(goal)
        }
    }

    fun getGoalContributionsFlow(goalId: Long) = repository.getContributionsForGoalFlow(goalId)

    fun calculateLoan(
        principal: Double,
        annualRatePercent: Double,
        tenureYears: Int,
        moratoriumMonths: Int,
        isSimpleInterestInMoratorium: Boolean,
        voluntaryMoratoriumMonthlyPayment: Double
    ) {
        val result = LoanCalculatorHelper.calculateLoan(
            principal = principal,
            annualRatePercent = annualRatePercent,
            tenureYears = tenureYears,
            moratoriumMonths = moratoriumMonths,
            isSimpleInterestInMoratorium = isSimpleInterestInMoratorium,
            voluntaryMoratoriumMonthlyPayment = voluntaryMoratoriumMonthlyPayment
        )
        _currentLoanResult.value = result
    }

    fun saveCurrentLoanScenario(title: String) {
        val res = _currentLoanResult.value ?: return
        viewModelScope.launch {
            repository.saveLoanScenario(
                LoanScenarioEntity(
                    title = title,
                    principal = res.principal,
                    annualInterestRate = 9.5,
                    tenureYears = 10,
                    moratoriumMonths = 54,
                    calculatedEmi = res.monthlyEmi,
                    totalInterest = res.totalInterestPaid,
                    totalRepayment = res.totalRepayment
                )
            )
        }
    }

    fun deleteLoanScenario(scenario: LoanScenarioEntity) {
        viewModelScope.launch {
            repository.deleteLoanScenario(scenario)
        }
    }

    fun executePaperOrder(symbol: String, companyName: String, isBuy: Boolean, quantity: Int, price: Double) {
        viewModelScope.launch {
            val (success, message) = repository.executePaperOrder(symbol, companyName, isBuy, quantity, price)
            _orderFeedbackMessage.value = message
            if (success) {
                delay(1200)
                dismissOrderDialog()
            }
        }
    }

    fun resetPaperTrading() {
        viewModelScope.launch {
            repository.resetPaperAccount()
        }
    }

    fun submitLessonQuiz(lessonId: String, score: Int, total: Int) {
        viewModelScope.launch {
            repository.saveLessonProgress(lessonId, score, total)
        }
    }

    fun sendMessageToAICoach(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            repository.addChatMessage("user", userText)
            _isAITyping.value = true

            // Derive current financial context
            val txList = transactions.value
            val currentMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            val currentMonthTxs = txList.filter {
                SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(it.dateMillis)) == currentMonthPrefix
            }
            val totalExpense = currentMonthTxs.filter { !it.isIncome }.sumOf { it.amount }
            val totalIncome = currentMonthTxs.filter { it.isIncome }.sumOf { it.amount }

            val categoryExpenses = currentMonthTxs.filter { !it.isIncome }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
            val topCategory = categoryExpenses.maxByOrNull { it.value }

            delay(1000) // Brief natural pause

            val aiResponse = AICoachEngine.generateCoachingResponse(
                query = userText,
                profile = userProfile.value,
                totalExpensesThisMonth = totalExpense,
                totalIncomeThisMonth = totalIncome,
                budget = currentBudget.value,
                topExpenseCategory = topCategory?.key,
                topCategoryAmount = topCategory?.value ?: 0.0,
                goals = savingsGoals.value
            )

            repository.addChatMessage("coach", aiResponse)
            _isAITyping.value = false
        }
    }

    fun clearAIChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // --------------------------------------------------------------------------
    // Supabase Authentication Methods
    // --------------------------------------------------------------------------

    fun sendEmailOtp(email: String, onResult: (AuthResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = supabaseAuthManager.signInWithOtp(email)
            if (result is AuthResult.OtpSent) {
                _activeScreen.value = MainScreen.AUTH_OTP_VERIFY
            }
            onResult(result)
        }
    }

    fun verifyEmailOtp(email: String, token: String, onResult: (AuthResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = supabaseAuthManager.verifyOtp(email, token)
            when (result) {
                is AuthResult.Success -> {
                    if (result.isOnboardingRequired) {
                        _activeScreen.value = MainScreen.ONBOARDING
                    } else {
                        _activeScreen.value = MainScreen.HOME
                    }
                }
                else -> {}
            }
            onResult(result)
        }
    }

    fun startGoogleSignIn(context: Context) {
        if (supabaseAuthManager.isConfigured) {
            val authUrl = supabaseAuthManager.getGoogleOAuthUrl()
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback simulation if no browser available
                viewModelScope.launch {
                    val result = supabaseAuthManager.simulateGoogleAuth()
                    if (result is AuthResult.Success) {
                        _activeScreen.value = MainScreen.ONBOARDING
                    }
                }
            }
        } else {
            // Fast preview / demo evaluation mode
            viewModelScope.launch {
                val result = supabaseAuthManager.simulateGoogleAuth()
                if (result is AuthResult.Success) {
                    _activeScreen.value = MainScreen.ONBOARDING
                }
            }
        }
    }

    fun handleAuthDeepLink(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            val result = supabaseAuthManager.handleOAuthCallback(uri)
            if (result is AuthResult.Success) {
                if (result.isOnboardingRequired) {
                    _activeScreen.value = MainScreen.ONBOARDING
                } else {
                    _activeScreen.value = MainScreen.HOME
                }
            }
        }
    }

    fun cancelOtpFlow() {
        supabaseAuthManager.cancelOtpFlow()
        _activeScreen.value = MainScreen.AUTH_WELCOME
    }

    fun signOut() {
        viewModelScope.launch {
            supabaseAuthManager.signOut()
            _screenBackStack.clear()
            _activeScreen.value = MainScreen.AUTH_WELCOME
        }
    }

    fun completeProfileOnboarding(
        name: String,
        college: String,
        isHosteller: Boolean,
        pocketMoney: Double,
        savingsTarget: Double,
        userType: String = "Student",
        language: String = "Hinglish"
    ) {
        viewModelScope.launch {
            val session = currentSession.value
            val entity = UserProfileEntity(
                id = 1L,
                authUserId = session?.user?.id ?: "",
                authEmail = session?.user?.email ?: "",
                authProvider = session?.user?.provider ?: "email_otp",
                name = name,
                college = college,
                isHosteller = isHosteller,
                monthlyPocketMoney = pocketMoney,
                targetMonthlySavings = savingsTarget,
                preferredLanguage = language,
                userType = userType,
                avatarUrl = session?.user?.avatarUrl ?: "",
                isOnboarded = true
            )
            repository.saveProfile(entity)

            // Setup initial budget for this month
            val key = repository.getCurrentMonthKey()
            val currentB = currentBudget.value
            if (currentB == null || currentB.totalLimit <= 0) {
                repository.saveBudget(
                    BudgetEntity(
                        monthKey = key,
                        totalLimit = pocketMoney,
                        canteenLimit = pocketMoney * 0.35,
                        groceriesLimit = pocketMoney * 0.15,
                        travelLimit = pocketMoney * 0.12,
                        booksLimit = pocketMoney * 0.10,
                        entertainmentLimit = pocketMoney * 0.12,
                        rechargeLimit = pocketMoney * 0.06,
                        shoppingLimit = pocketMoney * 0.05,
                        otherLimit = pocketMoney * 0.05
                    )
                )
            }

            // Sync to Supabase PostgreSQL table & RLS policies
            supabaseAuthManager.syncProfileToSupabase(entity)
            supabaseAuthManager.markOnboardingCompleted()
            _activeScreen.value = MainScreen.HOME
        }
    }

    fun updateProfile(
        name: String,
        college: String,
        isHosteller: Boolean,
        pocketMoney: Double,
        savingsTarget: Double,
        language: String
    ) {
        viewModelScope.launch {
            val existing = userProfile.value
            val session = currentSession.value
            val updated = UserProfileEntity(
                id = 1L,
                authUserId = existing?.authUserId ?: session?.user?.id ?: "",
                authEmail = existing?.authEmail ?: session?.user?.email ?: "",
                authProvider = existing?.authProvider ?: session?.user?.provider ?: "email_otp",
                name = name,
                college = college,
                isHosteller = isHosteller,
                monthlyPocketMoney = pocketMoney,
                targetMonthlySavings = savingsTarget,
                preferredLanguage = language,
                userType = existing?.userType ?: "Student",
                avatarUrl = existing?.avatarUrl ?: session?.user?.avatarUrl ?: "",
                isOnboarded = true
            )
            repository.saveProfile(updated)
            supabaseAuthManager.syncProfileToSupabase(updated)

            // Also update budget if totalLimit was 0
            val currentB = currentBudget.value
            if (currentB == null || currentB.totalLimit <= 0) {
                repository.saveBudget(
                    BudgetEntity(
                        monthKey = repository.getCurrentMonthKey(),
                        totalLimit = pocketMoney
                    )
                )
            }
        }
    }

    fun resetAllUserData() {
        viewModelScope.launch {
            repository.clearAllUserData()
            supabaseAuthManager.signOut()
            _screenBackStack.clear()
            _activeScreen.value = MainScreen.AUTH_WELCOME
        }
    }

    // Smart deterministic insights
    fun generateDeterministicInsights(
        totalSpent: Double,
        budgetLimit: Double,
        categoryMap: Map<String, Double>
    ): List<String> {
        val insights = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemaining = maxDays - currentDay

        val percentUsed = if (budgetLimit > 0) ((totalSpent / budgetLimit) * 100).toInt() else 0

        if (percentUsed >= 100) {
            insights.add("⚠️ You have exceeded 100% of your monthly pocket money with $daysRemaining days left in the month.")
        } else if (percentUsed >= 80) {
            insights.add("🚨 80% Threshold Alert: You have used ${percentUsed}% of your total budget. Stick to mess food to avoid borrowing.")
        } else if (percentUsed >= 50 && currentDay <= 12) {
            insights.add("⚡ High Spending Rate: You spent 50% of your pocket money in the first $currentDay days. Slow down discretionary wants.")
        } else {
            insights.add("✅ Budget On Track: ${100 - percentUsed}% of your monthly allowance is safely remaining.")
        }

        val canteenSpend = categoryMap["Food & Canteen"] ?: 0.0
        if (canteenSpend > 0 && canteenSpend >= (budgetLimit * 0.35)) {
            insights.add("🍔 Food & Canteen accounts for ₹${"%,.0f".format(canteenSpend)} of your expenses. Packing college snacks will save ₹500/month.")
        }

        val goals = savingsGoals.value
        val topGoal = goals.firstOrNull { !it.isArchived }
        if (topGoal != null && topGoal.savedAmount > 0) {
            val progress = ((topGoal.savedAmount / topGoal.targetAmount) * 100).toInt()
            insights.add("🎯 Goal Progress: Your '${topGoal.title}' goal is ${progress}% funded! Adding ₹200 this week keeps you ahead.")
        }

        return insights
    }

    // Auto-Tracking Ingestion and Review Actions
    fun setImportFilter(filter: String) {
        _selectedImportFilter.value = filter
    }

    fun openStatementImportDialog() {
        _showStatementImportDialog.value = true
    }

    fun closeStatementImportDialog() {
        _showStatementImportDialog.value = false
    }

    fun clearImportFeedback() {
        _importActionFeedback.value = null
    }

    fun importCsvStatement(csvContent: String, sourceName: String = "Bank Statement") {
        viewModelScope.launch {
            val result = repository.importTransactionsFromCsv(csvContent, sourceName)
            _importActionFeedback.value = result.message
            _showStatementImportDialog.value = false
        }
    }

    fun simulateIncomingPayment(title: String, text: String) {
        viewModelScope.launch {
            val entity = repository.processIncomingNotification(title, text)
            if (entity != null) {
                _importActionFeedback.value = "Captured transaction of ₹${"%,.0f".format(entity.rawAmount)} to ${entity.cleanMerchant}."
            } else {
                _importActionFeedback.value = "Filtered non-payment alert or OTP (Privacy Protected)."
            }
        }
    }

    fun confirmImportedTransaction(id: Long, finalCategory: String, finalMerchant: String) {
        viewModelScope.launch {
            repository.confirmImportedTransaction(id, finalCategory, finalMerchant)
            _importActionFeedback.value = "Confirmed and added to your monthly budget!"
        }
    }

    fun excludeImportedTransaction(id: Long, reason: String = "Excluded by user") {
        viewModelScope.launch {
            repository.excludeImportedTransaction(id, reason)
            _importActionFeedback.value = "Excluded from your expenses."
        }
    }

    fun resolveDuplicate(id: Long, keepExisting: Boolean) {
        viewModelScope.launch {
            repository.resolveDuplicate(id, keepExisting)
            _importActionFeedback.value = if (keepExisting) "Resolved: duplicate ignored." else "Restored for review."
        }
    }

    fun connectProvider(providerId: String) {
        viewModelScope.launch {
            repository.connectAccount(providerId)
            _importActionFeedback.value = "Provider connection activated."
        }
    }

    fun disconnectProvider(providerId: String) {
        viewModelScope.launch {
            repository.disconnectAccount(providerId)
            _importActionFeedback.value = "Provider disconnected."
        }
    }
}
