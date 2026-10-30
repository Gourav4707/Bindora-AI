package com.example.data.ingestion

import java.util.regex.Pattern

data class ParsedPaymentNotification(
    val amount: Double,
    val direction: String, // "DEBIT", "CREDIT"
    val merchantOrSender: String,
    val externalReference: String,
    val maskedAccount: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val rawText: String
)

object NotificationParser {

    // Regex to detect sensitive OTP or 2FA messages to guarantee user privacy
    private val OTP_PATTERNS = listOf(
        Pattern.compile("\\b(OTP|one time password|verification code|secret code)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(do not share|never share|valid for \\d+ min)\\b", Pattern.CASE_INSENSITIVE)
    )

    // Regex to detect purely informational or promotional SMS
    private val PROMO_PATTERNS = listOf(
        Pattern.compile("\\b(congratulations|pre-approved|apply for|use code|voucher|discount|cashback offer)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(balance in your a/c|available balance is|clear balance)\\b", Pattern.CASE_INSENSITIVE)
    )

    // Regex to extract monetary amounts: "Rs. 240.00", "INR 1,200", "₹ 450", "Rs 50"
    private val AMOUNT_PATTERN = Pattern.compile(
        "(?:(?:Rs\\.?|INR|₹)\\s*)([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Alternate amount pattern: "debited by 240.00"
    private val ALT_AMOUNT_PATTERN = Pattern.compile(
        "(?:debited by|credited with|paid)\\s+(?:(?:Rs\\.?|INR|₹)?\\s*)([0-9]+(?:\\.[0-9]{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Reference / UTR pattern: "UPI Ref 429182910291", "Ref No 8291829", "Txn ID T261029", "UTR 429182"
    private val REF_PATTERNS = listOf(
        Pattern.compile("(?:UPI Ref(?: no)?|Ref No|Txn ID|UTR|Ref)\\s*[:\\-]?\\s*([a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?:UPI/)([0-9]{9,16})", Pattern.CASE_INSENSITIVE)
    )

    // Masked Account: "A/c ...4821", "A/C XX4821", "account *4821"
    private val ACCOUNT_PATTERN = Pattern.compile(
        "(?:A/c|Account|A/C)\\s*(?:no\\.?)?\\s*[*Xx.]+([0-9]{3,4})",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Determines if text is a valid payment notification or bank debit/credit alert.
     * Returns null if text is an OTP, promo message, or non-transactional notification.
     */
    fun parseNotification(title: String, text: String): ParsedPaymentNotification? {
        val combined = "$title $text".trim()

        // 1. Strict Privacy Check: Discard OTPs and 2FA credentials
        if (OTP_PATTERNS.any { it.matcher(combined).find() }) {
            return null
        }

        // 2. Discard promotional messages or pure balance inquiries without debit/credit
        val hasDebit = combined.contains("debited", ignoreCase = true) ||
                combined.contains("sent", ignoreCase = true) ||
                combined.contains("paid", ignoreCase = true) ||
                combined.contains("spent", ignoreCase = true) ||
                combined.contains("withdrawn", ignoreCase = true)

        val hasCredit = combined.contains("credited", ignoreCase = true) ||
                combined.contains("deposited", ignoreCase = true) ||
                combined.contains("received", ignoreCase = true)

        if (!hasDebit && !hasCredit) {
            return null
        }

        if (PROMO_PATTERNS.any { it.matcher(combined).find() } && !hasDebit && !hasCredit) {
            return null
        }

        // 3. Extract Amount
        var amount = 0.0
        val amountMatcher = AMOUNT_PATTERN.matcher(combined)
        if (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)?.replace(",", "")
            amount = amountStr?.toDoubleOrNull() ?: 0.0
        } else {
            val altMatcher = ALT_AMOUNT_PATTERN.matcher(combined)
            if (altMatcher.find()) {
                val amountStr = altMatcher.group(1)?.replace(",", "")
                amount = amountStr?.toDoubleOrNull() ?: 0.0
            }
        }

        if (amount <= 0.0) {
            return null
        }

        // 4. Direction
        val direction = if (hasCredit && !hasDebit) "CREDIT" else "DEBIT"

        // 5. Extract External Reference (UTR / UPI Ref)
        var externalReference = ""
        for (pattern in REF_PATTERNS) {
            val matcher = pattern.matcher(combined)
            if (matcher.find()) {
                externalReference = matcher.group(1)?.trim() ?: ""
                if (externalReference.isNotBlank()) break
            }
        }

        // 6. Extract Masked Account
        var maskedAccount = ""
        val accMatcher = ACCOUNT_PATTERN.matcher(combined)
        if (accMatcher.find()) {
            val lastDigits = accMatcher.group(1)
            maskedAccount = "••$lastDigits"
        }

        // 7. Extract Merchant or Counterparty
        val merchant = extractMerchant(combined, direction)

        return ParsedPaymentNotification(
            amount = amount,
            direction = direction,
            merchantOrSender = merchant,
            externalReference = externalReference,
            maskedAccount = maskedAccount,
            timestampMillis = System.currentTimeMillis(),
            rawText = combined
        )
    }

    private fun extractMerchant(text: String, direction: String): String {
        // Pattern 1: "paid to <Merchant> on/via" or "sent to <Merchant> via"
        val paidToPattern = Pattern.compile("(?:paid to|sent to|transfer to|transferred to)\\s+([A-Za-z0-9 &.'-]+?)(?:\\s+(?:on|via|through|using|ref|for|upi)|\\.|,|$)", Pattern.CASE_INSENSITIVE)
        val m1 = paidToPattern.matcher(text)
        if (m1.find()) {
            val match = m1.group(1)?.trim()
            if (!match.isNullOrBlank() && match.length in 2..40) {
                return match
            }
        }

        // Pattern 2: "at <Merchant>" e.g. "Spent Rs.240 at Starbucks"
        val atPattern = Pattern.compile("(?:at|to)\\s+([A-Za-z0-9 &.'-]+?)(?:\\s+(?:on|via|through|using|ref|for)|\\.|,|$)", Pattern.CASE_INSENSITIVE)
        val m2 = atPattern.matcher(text)
        if (m2.find()) {
            val match = m2.group(1)?.trim()
            if (!match.isNullOrBlank() && match.length in 2..40) {
                return match
            }
        }

        // Pattern 3: "VPA <merchant>@<bank>" e.g. "on VPA zomato@icici"
        val vpaPattern = Pattern.compile("(?:VPA|vpa)\\s+([a-zA-Z0-9._]+)@", Pattern.CASE_INSENSITIVE)
        val m3 = vpaPattern.matcher(text)
        if (m3.find()) {
            val match = m3.group(1)?.trim()
            if (!match.isNullOrBlank()) {
                return match
            }
        }

        // Pattern 4: "received from <Sender>"
        val receivedPattern = Pattern.compile("(?:received from|deposited by|from)\\s+([A-Za-z0-9 &.'-]+?)(?:\\s+(?:on|via|through|ref|for)|\\.|,|$)", Pattern.CASE_INSENSITIVE)
        val m4 = receivedPattern.matcher(text)
        if (m4.find()) {
            val match = m4.group(1)?.trim()
            if (!match.isNullOrBlank() && match.length in 2..40) {
                return match
            }
        }

        return if (direction == "DEBIT") "UPI Merchant" else "Allowance / Transfer"
    }
}
