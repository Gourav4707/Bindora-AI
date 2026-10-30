package com.example.data.ingestion

import com.example.data.model.ImportedTransactionEntity
import com.example.data.model.TransactionEntity
import kotlin.math.abs

data class DeduplicationResult(
    val isDuplicate: Boolean,
    val isExactMatch: Boolean,
    val matchedImportedId: Long? = null,
    val matchedManualId: Long? = null,
    val confidence: Float = 0f,
    val reason: String = ""
)

object DeduplicationEngine {

    private const val FIFTEEN_MINUTES_MS = 15L * 60L * 1000L
    private const val TWENTY_FOUR_HOURS_MS = 24L * 60L * 60L * 1000L
    private const val AMOUNT_TOLERANCE = 0.05

    /**
     * Checks if a new candidate transaction duplicates any existing imported or manual transactions.
     */
    fun evaluate(
        candidateAmount: Double,
        candidateDirection: String,
        candidateTimestamp: Long,
        candidateRef: String,
        candidateMerchant: String,
        existingImported: List<ImportedTransactionEntity>,
        existingManual: List<TransactionEntity>
    ): DeduplicationResult {

        // 1. Exact Reference Match against existing imported transactions
        if (candidateRef.isNotBlank()) {
            val exactImportedMatch = existingImported.firstOrNull {
                it.externalReference.isNotBlank() && it.externalReference.equals(candidateRef, ignoreCase = true)
            }
            if (exactImportedMatch != null) {
                return DeduplicationResult(
                    isDuplicate = true,
                    isExactMatch = true,
                    matchedImportedId = exactImportedMatch.id,
                    confidence = 1.0f,
                    reason = "Exact match on external reference / UTR '${candidateRef}' from ${exactImportedMatch.sourceProviderName}"
                )
            }
        }

        // 2. Exact Reference Match inside manual transactions (if saved in notes)
        if (candidateRef.isNotBlank()) {
            val exactManualMatch = existingManual.firstOrNull {
                it.notes.contains(candidateRef, ignoreCase = true)
            }
            if (exactManualMatch != null) {
                return DeduplicationResult(
                    isDuplicate = true,
                    isExactMatch = true,
                    matchedManualId = exactManualMatch.id,
                    confidence = 1.0f,
                    reason = "Exact match on reference '${candidateRef}' in existing manual transaction '${exactManualMatch.title}'"
                )
            }
        }

        // 3. Composite Fuzzy Match against imported transactions
        for (item in existingImported) {
            if (item.status == "EXCLUDED") continue

            val amountMatches = abs(item.rawAmount - candidateAmount) <= AMOUNT_TOLERANCE
            val directionMatches = item.direction.equals(candidateDirection, ignoreCase = true)
            val timeDiff = abs(item.transactionTimestamp - candidateTimestamp)

            if (amountMatches && directionMatches) {
                // If within 15 minutes, high confidence match
                if (timeDiff <= FIFTEEN_MINUTES_MS) {
                    val merchantOverlap = isMerchantSimilar(candidateMerchant, item.cleanMerchant)
                    val confidence = if (merchantOverlap) 0.95f else 0.80f
                    return DeduplicationResult(
                        isDuplicate = true,
                        isExactMatch = false,
                        matchedImportedId = item.id,
                        confidence = confidence,
                        reason = "Potential duplicate: ₹${String.format(java.util.Locale.US, "%.2f", candidateAmount)} (${item.cleanMerchant}) recorded within 15 mins by ${item.sourceProviderName}"
                    )
                }

                // If within 24 hours (e.g. statement date timestamp matching a notification from that day)
                if (timeDiff <= TWENTY_FOUR_HOURS_MS) {
                    val merchantOverlap = isMerchantSimilar(candidateMerchant, item.cleanMerchant)
                    if (merchantOverlap) {
                        return DeduplicationResult(
                            isDuplicate = true,
                            isExactMatch = false,
                            matchedImportedId = item.id,
                            confidence = 0.85f,
                            reason = "Same-day transaction matching '${item.cleanMerchant}' for ₹${String.format(java.util.Locale.US, "%.2f", candidateAmount)}"
                        )
                    }
                }
            }
        }

        // 4. Composite Fuzzy Match against manual transactions
        val isIncome = candidateDirection.equals("CREDIT", ignoreCase = true)
        for (manual in existingManual) {
            val amountMatches = abs(manual.amount - candidateAmount) <= AMOUNT_TOLERANCE
            val directionMatches = manual.isIncome == isIncome
            val timeDiff = abs(manual.dateMillis - candidateTimestamp)

            if (amountMatches && directionMatches) {
                if (timeDiff <= TWENTY_FOUR_HOURS_MS) {
                    val merchantOverlap = isMerchantSimilar(candidateMerchant, manual.title)
                    if (merchantOverlap || timeDiff <= FIFTEEN_MINUTES_MS) {
                        return DeduplicationResult(
                            isDuplicate = true,
                            isExactMatch = false,
                            matchedManualId = manual.id,
                            confidence = 0.85f,
                            reason = "Matches existing manual entry '${manual.title}' (₹${String.format(java.util.Locale.US, "%.2f", manual.amount)}) on the same day"
                        )
                    }
                }
            }
        }

        return DeduplicationResult(
            isDuplicate = false,
            isExactMatch = false
        )
    }

    private fun isMerchantSimilar(nameA: String, nameB: String): Boolean {
        val a = nameA.trim().lowercase(java.util.Locale.ROOT)
        val b = nameB.trim().lowercase(java.util.Locale.ROOT)
        if (a.isBlank() || b.isBlank()) return false
        if (a == b) return true
        if (a.contains(b) || b.contains(a)) return true

        // Check word intersection
        val wordsA = a.split(" ").filter { it.length > 2 }
        val wordsB = b.split(" ").filter { it.length > 2 }
        return wordsA.any { wordsB.contains(it) }
    }
}
