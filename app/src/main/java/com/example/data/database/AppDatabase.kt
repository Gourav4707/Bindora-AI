package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.FinanceDao
import com.example.data.model.BudgetEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ConnectedAccountEntity
import com.example.data.model.GoalContributionEntity
import com.example.data.model.ImportedTransactionEntity
import com.example.data.model.LessonProgressEntity
import com.example.data.model.LoanScenarioEntity
import com.example.data.model.MerchantCategoryMappingEntity
import com.example.data.model.PaperAccountEntity
import com.example.data.model.PaperHoldingEntity
import com.example.data.model.PaperOrderEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.PaymentOrderEntity
import com.example.data.model.SyncAuditLogEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.UserSubscriptionEntity

@Database(
    entities = [
        UserProfileEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        GoalContributionEntity::class,
        LoanScenarioEntity::class,
        PaperAccountEntity::class,
        PaperHoldingEntity::class,
        PaperOrderEntity::class,
        LessonProgressEntity::class,
        ChatMessageEntity::class,
        ConnectedAccountEntity::class,
        ImportedTransactionEntity::class,
        MerchantCategoryMappingEntity::class,
        SyncAuditLogEntity::class,
        PaymentOrderEntity::class,
        UserSubscriptionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_finance_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
