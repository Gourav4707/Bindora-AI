package com.example.data.ingestion

import com.example.data.model.FinanceConstants
import java.util.Locale

data class ClassifiedCategoryResult(
    val cleanMerchant: String,
    val category: String,
    val confidenceScore: Float,
    val isLearnedRule: Boolean = false
)

object CategoryClassifier {

    /**
     * Cleans raw transaction narration or merchant string into a concise human-readable merchant title.
     * Handles UPI UTR strings, POS terminals, and Indian bank narration patterns.
     */
    fun cleanMerchantName(raw: String): String {
        if (raw.isBlank()) return "Unknown Transaction"

        var text = raw.trim()

        // Strip common UPI narration wrappers e.g. "UPI/428919283921/DR/ZOMATO LIMITED/ICICI/..."
        if (text.contains("UPI/", ignoreCase = true) || text.contains("UPI-", ignoreCase = true)) {
            val parts = text.split("/", "-", ":")
            val candidate = parts.firstOrNull { part ->
                val p = part.trim()
                p.length > 2 && 
                !p.all { char -> char.isDigit() } &&
                !p.equals("UPI", ignoreCase = true) &&
                !p.equals("DR", ignoreCase = true) &&
                !p.equals("CR", ignoreCase = true) &&
                !p.equals("PAY", ignoreCase = true) &&
                !p.equals("REF", ignoreCase = true)
            }
            if (candidate != null && candidate.isNotBlank()) {
                text = candidate.trim()
            }
        }

        // Remove trailing or leading bank identifiers like /ICICI, /SBI, /HDFC, @okhdfcbank, @oksbi, @paytm, @ybl
        text = text.replace(Regex("@[a-zA-Z0-9._]+"), "")
        text = text.replace(Regex("/(ICICI|SBI|HDFC|AXIS|YESB|PAYTM|PNB|BOB).*$", RegexOption.IGNORE_CASE), "")

        // Remove POS, VPA, NEFT, RTGS prefixes
        text = text.replace(Regex("^(POS\\s*\\d*|VPA|NEFT|RTGS|IMPS\\s*\\d*|TRANSFER\\s*TO|PAID\\s*TO|SENT\\s*TO)\\s*", RegexOption.IGNORE_CASE), "")

        // Remove common legal entity suffixes
        text = text.replace(Regex("\\b(PVT LTD|PRIVATE LIMITED|LIMITED|LTD|SERVICES|INDIA|ENTERPRISES)\\b", RegexOption.IGNORE_CASE), "")

        // Clean extra punctuation and double whitespace
        text = text.replace(Regex("[^a-zA-Z0-9 &.'-]"), " ").trim()
        text = text.replace(Regex("\\s+"), " ")

        if (text.isBlank()) {
            return "Payment"
        }

        // Title case formatting
        return text.split(" ").filter { it.isNotBlank() }.joinToString(" ") { word ->
            word.lowercase(Locale.ROOT).replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString()
            }
        }
    }

    /**
     * Determines the best category using:
     * 1. User-learned merchant overrides (highest priority)
     * 2. Direct keyword and brand heuristic classification
     * 3. Fallback category based on transaction direction
     */
    fun classify(
        rawDescription: String,
        direction: String,
        learnedMappings: Map<String, String> = emptyMap()
    ): ClassifiedCategoryResult {
        val cleanMerchant = cleanMerchantName(rawDescription)
        val upperMerchant = cleanMerchant.uppercase(Locale.ROOT)
        val upperRaw = rawDescription.uppercase(Locale.ROOT)

        // 1. Check user-learned merchant mappings
        for ((pattern, category) in learnedMappings) {
            val upperPattern = pattern.uppercase(Locale.ROOT)
            if (upperMerchant.contains(upperPattern) || upperRaw.contains(upperPattern)) {
                return ClassifiedCategoryResult(
                    cleanMerchant = cleanMerchant,
                    category = category,
                    confidenceScore = 1.0f,
                    isLearnedRule = true
                )
            }
        }

        // 2. Check for Credit / Refund / Allowance
        if (direction.equals("CREDIT", ignoreCase = true)) {
            return when {
                upperRaw.contains("REFUND") || upperMerchant.contains("REFUND") -> {
                    ClassifiedCategoryResult(cleanMerchant, "Shopping", 0.95f)
                }
                upperRaw.contains("SALARY") || upperRaw.contains("STIPEND") || upperRaw.contains("INTERN") -> {
                    ClassifiedCategoryResult(cleanMerchant, "Internship / Freelance", 0.95f)
                }
                upperRaw.contains("PAPA") || upperRaw.contains("MOM") || upperRaw.contains("ALLOWANCE") || upperRaw.contains("POCKET") -> {
                    ClassifiedCategoryResult(cleanMerchant, "Pocket Money / Allowance", 0.95f)
                }
                else -> {
                    ClassifiedCategoryResult(cleanMerchant, "Other Income", 0.75f)
                }
            }
        }

        // 3. Check student-centric expense categories
        // Food & Canteen
        val foodKeywords = listOf("SWIGGY", "ZOMATO", "CANTEEN", "CHAI", "COFFEE", "CAFE", "SAMOSA", "MAGGI", "PIZZA", "BURGER", "MCDONALD", "KFC", "DOMINO", "SUBWAY", "HALDIRAM", "DABA", "DHABA", "RESTAURANT", "BAKERY", "TEA", "MESS", "BIRYANI")
        if (foodKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Food & Canteen", 0.92f)
        }

        // Groceries & Quick Commerce
        val groceryKeywords = listOf("BLINKIT", "ZEPTO", "INSTAMART", "BIGBASKET", "DMART", "SUPERMARKET", "GROCERY", "KIRANA", "MILK", "FRUITS", "VEGETABLE")
        if (groceryKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Groceries", 0.92f)
        }

        // Travel & Metro
        val travelKeywords = listOf("DMRC", "METRO", "UBER", "OLA", "RAPIDO", "IRCTC", "RAILWAY", "BUS", "AUTO", "PETROL", "FUEL", "INDIAN OIL", "BPCL", "HPCL", "CHALO")
        if (travelKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Travel & Metro", 0.92f)
        }

        // Books & Stationery
        val bookKeywords = listOf("STATIONERY", "BOOK", "XEROX", "PHOTOCOPY", "PRINT", "NOTEBOOK", "PENCIL", "PEN", "EXAM", "LIBRARY", "REGISTER")
        if (bookKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Books & Stationery", 0.88f)
        }

        // Subscriptions & Entertainment
        val entertainmentKeywords = listOf("NETFLIX", "SPOTIFY", "PRIME", "HOTSTAR", "YOUTUBE", "CINEMA", "PVR", "INOX", "BOOKMYSHOW", "STEAM", "PLAYSTATION")
        if (entertainmentKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Entertainment", 0.90f)
        }

        // Mobile Recharge & Wi-Fi
        val rechargeKeywords = listOf("AIRTEL", "JIO", "VI", "VODAFONE", "RECHARGE", "BROADBAND", "WIFI", "ACT FIBERNET", "BSNL")
        if (rechargeKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Mobile Recharge & Wi-Fi", 0.90f)
        }

        // Shopping
        val shoppingKeywords = listOf("AMAZON", "FLIPKART", "MYNTRA", "MEESHO", "AJIO", "ZUDIO", "MAX", "H&M", "ZARA", "SHOPPING", "RETAIL")
        if (shoppingKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Shopping", 0.88f)
        }

        // Hostel & Rent
        val hostelKeywords = listOf("HOSTEL", "RENT", "PG", "MAINTENANCE", "LANDLORD", "ROOM")
        if (hostelKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Hostel & Rent", 0.88f)
        }

        // Education & Courses
        val educationKeywords = listOf("COLLEGE", "FEES", "UNIVERSITY", "UDEMY", "COURSERA", "TUTOR", "COACHING", "UNACADEMY")
        if (educationKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Education & Courses", 0.88f)
        }

        // Health & Medical
        val healthKeywords = listOf("PHARMACY", "MEDICINE", "APOLLO", "1MG", "CLINIC", "HOSPITAL", "DOCTOR", "CHEMIST")
        if (healthKeywords.any { upperRaw.contains(it) || upperMerchant.contains(it) }) {
            return ClassifiedCategoryResult(cleanMerchant, "Health & Medical", 0.85f)
        }

        // Default fallback
        return ClassifiedCategoryResult(
            cleanMerchant = cleanMerchant,
            category = "Other Miscellaneous",
            confidenceScore = 0.50f
        )
    }
}
