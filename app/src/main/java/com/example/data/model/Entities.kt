package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1L,
    val name: String = "College Student",
    val college: String = "Delhi University",
    val isHosteller: Boolean = true,
    val monthlyPocketMoney: Double = 5000.0,
    val targetMonthlySavings: Double = 1000.0,
    val preferredLanguage: String = "Hinglish",
    val isOnboarded: Boolean = false,
    val currency: String = "₹",
    val authUserId: String = "",
    val authEmail: String = "",
    val authProvider: String = "",
    val userType: String = "Student",
    val avatarUrl: String = ""
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val category: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val notes: String = "",
    val paymentMode: String = "UPI" // UPI, Cash, Card
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val monthKey: String, // e.g. "2026-10"
    val totalLimit: Double = 5000.0,
    val canteenLimit: Double = 1500.0,
    val groceriesLimit: Double = 800.0,
    val travelLimit: Double = 600.0,
    val booksLimit: Double = 500.0,
    val entertainmentLimit: Double = 600.0,
    val rechargeLimit: Double = 300.0,
    val shoppingLimit: Double = 500.0,
    val otherLimit: Double = 200.0,
    val alert50Sent: Boolean = false,
    val alert80Sent: Boolean = false,
    val alert100Sent: Boolean = false
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val targetDateMillis: Long = System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000,
    val category: String = "Gadgets",
    val isArchived: Boolean = false
)

@Entity(tableName = "goal_contributions")
data class GoalContributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val amount: Double,
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "loan_scenarios")
data class LoanScenarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val principal: Double,
    val annualInterestRate: Double,
    val tenureYears: Int,
    val moratoriumMonths: Int,
    val isSimpleInterestDuringMoratorium: Boolean = true,
    val voluntaryMoratoriumPayment: Double = 0.0,
    val calculatedEmi: Double,
    val totalInterest: Double,
    val totalRepayment: Double,
    val savedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "paper_account")
data class PaperAccountEntity(
    @PrimaryKey val id: Long = 1L,
    val cashBalance: Double = 100000.0, // Virtual ₹1,00,000 starting cash
    val initialBalance: Double = 100000.0,
    val totalRealizedPnl: Double = 0.0
)

@Entity(tableName = "paper_holdings")
data class PaperHoldingEntity(
    @PrimaryKey val symbol: String,
    val companyName: String,
    val quantity: Int,
    val avgBuyPrice: Double,
    val currentPrice: Double
)

@Entity(tableName = "paper_orders")
data class PaperOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val companyName: String,
    val orderType: String, // "BUY" or "SELL"
    val quantity: Int,
    val price: Double,
    val totalAmount: Double,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val isCompleted: Boolean = false,
    val quizScore: Int = 0,
    val totalQuestions: Int = 0,
    val completedAtMillis: Long = 0L
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user" or "coach"
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_orders")
data class PaymentOrderEntity(
    @PrimaryKey val orderNumber: String, // e.g. BND-SUB-20261030-9921
    val authUserId: String,
    val planId: String,
    val planName: String,
    val billingCycle: String, // "MONTHLY" or "ANNUAL"
    val baseAmount: Double,
    val gstRate: Double = 0.18,
    val gstAmount: Double,
    val totalAmount: Double,
    val currency: String = "INR",
    val status: String = "PENDING", // PENDING, SUCCESS, FAILED, CANCELLED
    val paymentMethod: String = "UPI",
    val merchantVpa: String = "bindora.pay@razorpay",
    val merchantName: String = "Bindora Technologies Pvt Ltd",
    val upiTxnRef: String = "",
    val providerOrderId: String = "",
    val failureReason: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val expiresAtMillis: Long = System.currentTimeMillis() + (15 * 60 * 1000), // 15 min window
    val completedAtMillis: Long? = null
)

@Entity(tableName = "user_subscriptions")
data class UserSubscriptionEntity(
    @PrimaryKey val id: Long = 1L, // Current active subscription record
    val authUserId: String = "",
    val planId: String = "free",
    val planName: String = "Bindora Free",
    val billingCycle: String = "FREE",
    val status: String = "ACTIVE", // ACTIVE, EXPIRED, CANCELLED, NONE
    val startsAtMillis: Long = System.currentTimeMillis(),
    val expiresAtMillis: Long = System.currentTimeMillis() + (3650L * 24 * 60 * 60 * 1000), // Free is ongoing
    val autoRenew: Boolean = false, // Disclosed manual renewal
    val lastOrderNumber: String = "",
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val isPremium: Boolean
        get() = (planId == "bindora_plus" || planId == "bindora_pro") &&
                status == "ACTIVE" &&
                System.currentTimeMillis() < expiresAtMillis
}

