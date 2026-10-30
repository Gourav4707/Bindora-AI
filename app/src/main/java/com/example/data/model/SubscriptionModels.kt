package com.example.data.model

import java.text.DecimalFormat

enum class BillingCycle(val label: String, val months: Int) {
    MONTHLY("Monthly", 1),
    ANNUAL("Annual", 12)
}

enum class PaymentOrderStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED
}

enum class SubscriptionTier(val planId: String, val displayName: String) {
    FREE("free", "Bindora Free"),
    PLUS("bindora_plus", "Bindora Plus"),
    PRO("bindora_pro", "Bindora Pro")
}

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val slug: String,
    val tagline: String,
    val monthlyPrice: Double,
    val annualPrice: Double,
    val features: List<String>,
    val isRecommended: Boolean = false,
    val badge: String? = null
) {
    fun priceFor(cycle: BillingCycle): Double = when (cycle) {
        BillingCycle.MONTHLY -> monthlyPrice
        BillingCycle.ANNUAL -> annualPrice
    }

    fun monthlyEquivalentFor(cycle: BillingCycle): Double = when (cycle) {
        BillingCycle.MONTHLY -> monthlyPrice
        BillingCycle.ANNUAL -> annualPrice / 12.0
    }

    fun savingsPercentageAnnual(): Int {
        val fullYearMonthly = monthlyPrice * 12.0
        val saved = fullYearMonthly - annualPrice
        return ((saved / fullYearMonthly) * 100).toInt()
    }
}

object SubscriptionPlansCatalog {
    val PLANS = listOf(
        SubscriptionPlan(
            id = "bindora_plus",
            name = "Bindora Plus",
            slug = "plus",
            tagline = "Smart Budgeting & AI Insights for College Life",
            monthlyPrice = 99.0,
            annualPrice = 799.0,
            badge = "POPULAR",
            isRecommended = false,
            features = listOf(
                "Unlimited AI Coach questions & personal advice",
                "Multi-bank statement auto-categorization & OCR",
                "Up to 10 active visual savings goals",
                "Smart overspending & mess allowance alerts",
                "100% ad-free experience"
            )
        ),
        SubscriptionPlan(
            id = "bindora_pro",
            name = "Bindora Pro",
            slug = "pro",
            tagline = "Ultimate Financial Mastery & Career Wealth Suite",
            monthlyPrice = 199.0,
            annualPrice = 1499.0,
            badge = "RECOMMENDED",
            isRecommended = true,
            features = listOf(
                "Everything in Plus included",
                "Advanced paper trading with live market depth",
                "Education loan repayment & moratorium optimization",
                "Audited tax-ready PDF & Excel expense exports",
                "Priority 1-on-1 fintech mentoring webinars",
                "Exclusive student investor badge"
            )
        )
    )

    fun getPlanById(id: String): SubscriptionPlan? = PLANS.find { it.id == id }
}

data class PlanPricingBreakdown(
    val plan: SubscriptionPlan,
    val billingCycle: BillingCycle,
    val baseAmount: Double,
    val gstRate: Double = 0.18,
    val gstAmount: Double,
    val totalPayable: Double
) {
    companion object {
        fun calculate(plan: SubscriptionPlan, cycle: BillingCycle): PlanPricingBreakdown {
            val total = plan.priceFor(cycle)
            // Inclusive GST calculation: Base + 18% GST = Total
            val base = total / 1.18
            val gst = total - base
            return PlanPricingBreakdown(
                plan = plan,
                billingCycle = cycle,
                baseAmount = (base * 100).toLong() / 100.0,
                gstRate = 0.18,
                gstAmount = (gst * 100).toLong() / 100.0,
                totalPayable = total
            )
        }
    }
}

data class UpiAppInfo(
    val packageName: String,
    val appName: String,
    val iconName: String,
    val isInstalled: Boolean = true
)
