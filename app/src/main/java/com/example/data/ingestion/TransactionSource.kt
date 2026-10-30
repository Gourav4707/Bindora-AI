package com.example.data.ingestion

import com.example.data.model.ImportedTransactionEntity

enum class ProviderType {
    NOTIFICATION_LISTENER,
    STATEMENT_IMPORT,
    ACCOUNT_AGGREGATOR,
    DIRECT_UPI_NOTICE
}

enum class ConnectionStatus(val label: String) {
    CONNECTED("Connected"),
    SYNCING("Syncing"),
    ACTION_REQUIRED("Action required"),
    CONNECTION_EXPIRED("Connection expired"),
    TEMPORARILY_UNAVAILABLE("Temporarily unavailable"),
    DISCONNECTED("Disconnected")
}

data class SyncResult(
    val success: Boolean,
    val itemsIngested: Int,
    val duplicatesDetected: Int,
    val message: String,
    val newTransactions: List<ImportedTransactionEntity> = emptyList()
)

data class ProviderDescriptor(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: ProviderType,
    val isSupportedDirectly: Boolean,
    val syncMethodDescription: String,
    val dataPermissionsExplanation: String,
    val privacyNote: String,
    val directConnectionNotice: String? = null
)

object SupportedProvidersCatalog {
    val ALL_PROVIDERS = listOf(
        ProviderDescriptor(
            id = "notification_listener",
            name = "Payment SMS & UPI Alert Listener",
            subtitle = "Automatic local tracking from GPay, PhonePe, Paytm, and Bank SMS",
            type = ProviderType.NOTIFICATION_LISTENER,
            isSupportedDirectly = true,
            syncMethodDescription = "On-Device Local Notification & SMS Parsing",
            dataPermissionsExplanation = "Read-only access to incoming financial payment notifications and bank debit SMS. OTPs and sensitive messages are automatically filtered and never logged.",
            privacyNote = "100% on-device processing. No data ever leaves your phone or reaches any external cloud servers."
        ),
        ProviderDescriptor(
            id = "csv_statement_import",
            name = "Bank Statement Importer (CSV)",
            subtitle = "Import official monthly statements from SBI, HDFC, ICICI, Axis, PNB, etc.",
            type = ProviderType.STATEMENT_IMPORT,
            isSupportedDirectly = true,
            syncMethodDescription = "Local CSV Statement Parser & Column Matcher",
            dataPermissionsExplanation = "Read-only access to user-selected CSV files. Extracts Date, Description/Narration, UTR/Ref, and Debit/Credit amounts.",
            privacyNote = "Processed strictly in local app memory. No banking passwords or credentials requested."
        ),
        ProviderDescriptor(
            id = "rbi_account_aggregator",
            name = "RBI Account Aggregator (AA)",
            subtitle = "Regulated read-only banking consent network (Setu / Finvu / Sahamati)",
            type = ProviderType.ACCOUNT_AGGREGATOR,
            isSupportedDirectly = true,
            syncMethodDescription = "RBI Regulated Digital Consent Flow (FIP/FIU)",
            dataPermissionsExplanation = "Read-only digitally signed bank account transaction statements via licensed Account Aggregator consent handles.",
            privacyNote = "Governed by RBI regulations. End-to-end encrypted; apps never see banking login passwords or MPIN."
        ),
        ProviderDescriptor(
            id = "direct_upi_gpay_phonepe",
            name = "Google Pay & PhonePe Direct Connect",
            subtitle = "Direct proprietary cloud API connection notice",
            type = ProviderType.DIRECT_UPI_NOTICE,
            isSupportedDirectly = false,
            syncMethodDescription = "Enterprise Proprietary API (Not Publicly Available)",
            dataPermissionsExplanation = "Requires closed enterprise banking agreements with NPCI and payment gateway operators.",
            privacyNote = "Neither Google Pay nor PhonePe provides direct public APIs for third-party consumer apps to read user transaction feeds directly from their servers.",
            directConnectionNotice = "Honest Disclosure: Third-party apps cannot legitimately connect directly to Google Pay or PhonePe servers without enterprise banking contracts. Bindora tracks UPI transactions safely and legitimately using our On-Device Payment SMS & UPI Alert Listener or CSV Statement Import!"
        )
    )

    fun getProvider(id: String): ProviderDescriptor? {
        return ALL_PROVIDERS.firstOrNull { it.id == id }
    }
}
