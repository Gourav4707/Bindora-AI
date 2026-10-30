package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryMeta(
    val name: String,
    val isIncome: Boolean,
    val icon: ImageVector,
    val color: Color,
    val keywords: List<String> = emptyList()
)

object FinanceConstants {
    val EXPENSE_CATEGORIES = listOf(
        CategoryMeta(
            "Food & Canteen",
            false,
            Icons.Default.Fastfood,
            Color(0xFFF97316),
            listOf("chai", "canteen", "maggi", "samosa", "roll", "lunch", "dinner", "swiggy", "zomato", "cafe", "coffee", "thali", "dosa", "mess")
        ),
        CategoryMeta(
            "Groceries",
            false,
            Icons.Default.ShoppingCart,
            Color(0xFF10B981),
            listOf("milk", "bread", "eggs", "biscuit", "snacks", "market", "blinkit", "zepto", "fruits", "grocery")
        ),
        CategoryMeta(
            "Travel & Metro",
            false,
            Icons.Default.Train,
            Color(0xFF0EA5E9),
            listOf("metro", "auto", "bus", "uber", "ola", "rapido", "train", "ticket", "fuel", "petrol")
        ),
        CategoryMeta(
            "Books & Stationery",
            false,
            Icons.Default.Book,
            Color(0xFF8B5CF6),
            listOf("book", "xerox", "print", "pen", "notebook", "register", "stationery", "calculator", "notes", "exam fee")
        ),
        CategoryMeta(
            "Hostel & Rent",
            false,
            Icons.Default.Home,
            Color(0xFF6366F1),
            listOf("hostel", "pg", "rent", "electricity", "maid", "cooler", "laundry", "maintenance")
        ),
        CategoryMeta(
            "Mobile & Internet",
            false,
            Icons.Default.PhoneAndroid,
            Color(0xFF06B6D4),
            listOf("jio", "airtel", "recharge", "wifi", "data", "broadband", "sim")
        ),
        CategoryMeta(
            "Shopping & Clothes",
            false,
            Icons.Default.LocalMall,
            Color(0xFFEC4899),
            listOf("clothes", "shoes", "shirt", "myntra", "amazon", "flipkart", "dress", "bag")
        ),
        CategoryMeta(
            "Entertainment & OTT",
            false,
            Icons.Default.Movie,
            Color(0xFFA855F7),
            listOf("movie", "netflix", "spotify", "prime", "gaming", "outing", "party", "concert")
        ),
        CategoryMeta(
            "Health & Medical",
            false,
            Icons.Default.LocalPharmacy,
            Color(0xFFEF4444),
            listOf("medicine", "doctor", "tablet", "clinic", "bandaid", "hospital", "test")
        ),
        CategoryMeta(
            "Other Expense",
            false,
            Icons.Default.SwapHoriz,
            Color(0xFF64748B),
            emptyList()
        )
    )

    val INCOME_CATEGORIES = listOf(
        CategoryMeta(
            "Pocket Money",
            true,
            Icons.Default.Payments,
            Color(0xFF10B981),
            listOf("papa", "mummy", "home", "pocket money", "parents", "allowance")
        ),
        CategoryMeta(
            "Freelance / Intern",
            true,
            Icons.Default.Work,
            Color(0xFF0D9488),
            listOf("stipend", "internship", "project", "client", "freelance", "gig", "tutoring")
        ),
        CategoryMeta(
            "Scholarship",
            true,
            Icons.Default.School,
            Color(0xFF3B82F6),
            listOf("scholarship", "grant", "fellowship", "college award")
        ),
        CategoryMeta(
            "Other Income",
            true,
            Icons.Default.Payments,
            Color(0xFF64748B),
            listOf("gift", "cashback", "refund", "won")
        )
    )

    fun getCategoryMeta(categoryName: String): CategoryMeta {
        return (EXPENSE_CATEGORIES + INCOME_CATEGORIES).firstOrNull {
            it.name.equals(categoryName, ignoreCase = true)
        } ?: CategoryMeta(categoryName, false, Icons.Default.SwapHoriz, Color(0xFF64748B))
    }

    fun suggestCategory(title: String, isIncome: Boolean): String {
        val lower = title.lowercase()
        val list = if (isIncome) INCOME_CATEGORIES else EXPENSE_CATEGORIES
        for (cat in list) {
            for (kw in cat.keywords) {
                if (lower.contains(kw)) {
                    return cat.name
                }
            }
        }
        return if (isIncome) "Pocket Money" else "Food & Canteen"
    }
}

// Paper Trading Stock instrument
data class SimulatedStock(
    val symbol: String,
    val companyName: String,
    val sector: String,
    val basePrice: Double,
    var currentPrice: Double,
    var changePercent: Double = 0.0
)

object PaperTradingData {
    val INITIAL_STOCKS = listOf(
        SimulatedStock("NIFTY 50", "NIFTY 50 Benchmark Index", "Index", 24320.0, 24320.0, 0.45),
        SimulatedStock("RELIANCE", "Reliance Industries Ltd", "Energy & Telecom", 2740.0, 2740.0, 1.12),
        SimulatedStock("TCS", "Tata Consultancy Services", "IT & Services", 3890.0, 3890.0, -0.35),
        SimulatedStock("INFY", "Infosys Technologies Ltd", "IT Services", 1750.0, 1750.0, 0.85),
        SimulatedStock("TATAMOTORS", "Tata Motors Ltd", "Automobile & EV", 865.0, 865.0, 2.10),
        SimulatedStock("HDFCBANK", "HDFC Bank Ltd", "Banking & Finance", 1685.0, 1685.0, -0.20),
        SimulatedStock("ZOMATO", "Zomato Ltd (Eternal)", "Consumer Internet", 242.0, 242.0, 3.40),
        SimulatedStock("ITC", "ITC Ltd (FMCG & Hotels)", "FMCG", 488.0, 488.0, 0.25)
    )
}

// Education Academy Lesson and Quiz structure
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class AcademyLesson(
    val id: String,
    val title: String,
    val readingTime: String,
    val studentTag: String,
    val summary: String,
    val content: String,
    val studentExample: String,
    val keyTakeaways: List<String>,
    val quiz: List<QuizQuestion>
)
