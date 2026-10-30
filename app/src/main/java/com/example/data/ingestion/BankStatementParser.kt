package com.example.data.ingestion

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ParsedStatementRow(
    val dateMillis: Long,
    val description: String,
    val reference: String,
    val amount: Double,
    val direction: String, // "DEBIT", "CREDIT"
    val balance: Double? = null
)

data class StatementParseResult(
    val success: Boolean,
    val totalRowsParsed: Int,
    val rows: List<ParsedStatementRow>,
    val detectedFormat: String,
    val errorMessages: List<String> = emptyList()
)

object BankStatementParser {

    private val DATE_FORMATS = listOf(
        "dd/MM/yyyy",
        "dd-MM-yyyy",
        "yyyy-MM-dd",
        "dd MMM yyyy",
        "dd-MMM-yyyy",
        "MM/dd/yyyy"
    )

    /**
     * Parses standard CSV bank statements (SBI, HDFC, ICICI, Axis, PNB, or generic export).
     */
    fun parseCsv(csvContent: String): StatementParseResult {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            return StatementParseResult(
                success = false,
                totalRowsParsed = 0,
                rows = emptyList(),
                detectedFormat = "Empty File",
                errorMessages = listOf("The provided CSV file contains no content.")
            )
        }

        // Find header line
        var headerIndex = -1
        var headerTokens = emptyList<String>()
        for (i in 0 until minOf(lines.size, 15)) {
            val tokens = splitCsvLine(lines[i]).map { it.lowercase(Locale.ROOT).trim() }
            if (tokens.any { it.contains("date") } && (tokens.any { it.contains("narration") || it.contains("description") || it.contains("particular") })) {
                headerIndex = i
                headerTokens = tokens
                break
            }
        }

        if (headerIndex == -1) {
            return StatementParseResult(
                success = false,
                totalRowsParsed = 0,
                rows = emptyList(),
                detectedFormat = "Unknown Format",
                errorMessages = listOf("Could not detect standard bank statement headers (e.g. 'Date', 'Narration/Description', 'Debit/Credit').")
            )
        }

        // Map column indices
        val dateIdx = headerTokens.indexOfFirst { it.contains("date") }
        val descIdx = headerTokens.indexOfFirst { it.contains("narration") || it.contains("description") || it.contains("particular") || it.contains("remark") }
        val refIdx = headerTokens.indexOfFirst { it.contains("ref") || it.contains("utr") || it.contains("chq") || it.contains("cheque") }
        val debitIdx = headerTokens.indexOfFirst { it.contains("debit") || it.contains("withdrawal") || it.contains("dr") }
        val creditIdx = headerTokens.indexOfFirst { it.contains("credit") || it.contains("deposit") || it.contains("cr") }
        val amountIdx = headerTokens.indexOfFirst { it == "amount" || it.contains("txn amount") }
        val balanceIdx = headerTokens.indexOfFirst { it.contains("balance") }

        val parsedRows = mutableListOf<ParsedStatementRow>()
        val errors = mutableListOf<String>()

        for (i in (headerIndex + 1) until lines.size) {
            val line = lines[i]
            val tokens = splitCsvLine(line)
            if (tokens.isEmpty() || tokens.size <= maxOf(dateIdx, descIdx)) continue

            val dateStr = tokens.getOrNull(dateIdx)?.trim() ?: ""
            val descStr = tokens.getOrNull(descIdx)?.trim() ?: ""
            val refStr = if (refIdx >= 0) tokens.getOrNull(refIdx)?.trim() ?: "" else ""

            if (dateStr.isBlank() && descStr.isBlank()) continue

            val dateMillis = parseDate(dateStr)
            val balance = if (balanceIdx >= 0) cleanNumber(tokens.getOrNull(balanceIdx)) else null

            var debit = if (debitIdx >= 0) cleanNumber(tokens.getOrNull(debitIdx)) else null
            var credit = if (creditIdx >= 0) cleanNumber(tokens.getOrNull(creditIdx)) else null

            // If separate Debit and Credit columns were not found, check single Amount column
            if (debit == null && credit == null && amountIdx >= 0) {
                val amt = cleanNumber(tokens.getOrNull(amountIdx))
                if (amt != null) {
                    if (amt < 0) {
                        debit = kotlin.math.abs(amt)
                    } else {
                        val typeCol = headerTokens.indexOfFirst { it.contains("type") }
                        val typeVal = if (typeCol >= 0) tokens.getOrNull(typeCol)?.lowercase(Locale.ROOT) ?: "" else ""
                        if (typeVal.contains("dr") || typeVal.contains("debit")) {
                            debit = amt
                        } else {
                            credit = amt
                        }
                    }
                }
            }

            if (debit != null && debit > 0.0) {
                parsedRows.add(
                    ParsedStatementRow(
                        dateMillis = dateMillis,
                        description = descStr,
                        reference = refStr,
                        amount = debit,
                        direction = "DEBIT",
                        balance = balance
                    )
                )
            } else if (credit != null && credit > 0.0) {
                parsedRows.add(
                    ParsedStatementRow(
                        dateMillis = dateMillis,
                        description = descStr,
                        reference = refStr,
                        amount = credit,
                        direction = "CREDIT",
                        balance = balance
                    )
                )
            }
        }

        val formatDesc = "Standard Indian Bank CSV (Cols: Date, Narration, ${if (refIdx >= 0) "Ref, " else ""}Debit/Credit)"

        return StatementParseResult(
            success = parsedRows.isNotEmpty(),
            totalRowsParsed = parsedRows.size,
            rows = parsedRows,
            detectedFormat = formatDesc,
            errorMessages = errors
        )
    }

    private fun parseDate(dateStr: String): Long {
        if (dateStr.isBlank()) return System.currentTimeMillis()
        val cleaned = dateStr.replace(",", " ").trim()
        for (pattern in DATE_FORMATS) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                sdf.isLenient = false
                val parsed = sdf.parse(cleaned)
                if (parsed != null) return parsed.time
            } catch (_: Exception) {
                // try next
            }
        }
        return System.currentTimeMillis()
    }

    private fun cleanNumber(str: String?): Double? {
        if (str.isNullOrBlank()) return null
        val cleaned = str.replace("₹", "")
            .replace("INR", "", ignoreCase = true)
            .replace("Rs.", "", ignoreCase = true)
            .replace(",", "")
            .trim()
        return cleaned.toDoubleOrNull()
    }

    private fun splitCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        var inQuotes = false
        val sb = StringBuilder()

        for (char in line) {
            when {
                char == '\"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    tokens.add(sb.toString().trim())
                    sb.clear()
                }
                else -> sb.append(char)
            }
        }
        tokens.add(sb.toString().trim())
        return tokens
    }
}
