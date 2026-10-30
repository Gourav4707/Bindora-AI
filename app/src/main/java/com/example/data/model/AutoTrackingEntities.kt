package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a connected financial institution, account aggregator,
 * or on-device tracking source.
 */
@Entity(tableName = "connected_accounts")
data class ConnectedAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val providerId: String, // e.g. "notification_listener", "csv_statement_import", "setu_aa", "gpay_notice"
    val providerName: String,
    val providerType: String, // "NOTIFICATION_LISTENER", "STATEMENT_IMPORT", "ACCOUNT_AGGREGATOR", "DIRECT_UPI_NOTICE"
    val accountNumberMasked: String = "",
    val connectionStatus: String = "Disconnected", // "Connected", "Syncing", "Action required", "Connection expired", "Temporarily unavailable", "Disconnected"
    val lastSyncMillis: Long? = null,
    val syncMethod: String = "",
    val dataPermissionsGranted: String = "",
    val isDirectAppNoticeOnly: Boolean = false,
    val authDetails: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

/**
 * Normalized financial transaction imported from any source (CSV, notification, AA feed).
 * Stored separately from manual transactions until confirmed or reconciled.
 */
@Entity(
    tableName = "imported_transactions",
    indices = [
        Index(value = ["externalReference"]),
        Index(value = ["status"]),
        Index(value = ["transactionTimestamp"])
    ]
)
data class ImportedTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceProviderId: String,
    val sourceProviderName: String,
    val sourceAccountMasked: String = "",
    val externalReference: String = "", // UTR, UPI Txn ID, or unique reference number
    val rawAmount: Double,
    val currency: String = "₹",
    val direction: String, // "DEBIT", "CREDIT", "TRANSFER", "REFUND"
    val rawDescription: String,
    val cleanMerchant: String,
    val suggestedCategory: String,
    val userCategory: String? = null,
    val transactionTimestamp: Long,
    val bookingTimestamp: Long = transactionTimestamp,
    val ingestionMethod: String = "NOTIFICATION", // "NOTIFICATION", "CSV_IMPORT", "ACCOUNT_AGGREGATOR", "MANUAL"
    val confidenceScore: Float = 0.8f,
    val status: String = "NEEDS_REVIEW", // "NEEDS_REVIEW", "CONFIRMED", "POSSIBLE_DUPLICATE", "EXCLUDED", "RECONCILED"
    val duplicateOfId: Long? = null,
    val duplicateReason: String = "",
    val matchedTransactionId: Long? = null, // ID in `transactions` table once confirmed
    val notes: String = ""
)

/**
 * User-confirmed merchant to category mappings learned from user edits.
 * Gives user control over how recurring student merchants are categorized.
 */
@Entity(
    tableName = "merchant_category_mappings",
    indices = [Index(value = ["merchantPattern"], unique = true)]
)
data class MerchantCategoryMappingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantPattern: String, // Normalized upper-case pattern e.g. "SWIGGY", "CANTEEN", "ZEPTO"
    val category: String,
    val userConfirmed: Boolean = true,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

/**
 * Audit log of synchronizations and imports for full user transparency.
 */
@Entity(tableName = "sync_audit_logs")
data class SyncAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val providerId: String,
    val providerName: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val itemsIngested: Int = 0,
    val duplicatesDetected: Int = 0,
    val statusMessage: String = "Completed successfully"
)
