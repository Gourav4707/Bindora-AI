package com.example.data.model

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

data class LoanCalculationResult(
    val principal: Double,
    val effectivePrincipal: Double,
    val monthlyEmi: Double,
    val moratoriumInterest: Double,
    val totalRepayment: Double,
    val totalInterestPaid: Double,
    val schedule: List<AmortizationYearRow>
)

data class AmortizationYearRow(
    val year: Int,
    val openingBalance: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val closingBalance: Double
)

object LoanCalculatorHelper {

    fun calculateLoan(
        principal: Double,
        annualRatePercent: Double,
        tenureYears: Int,
        moratoriumMonths: Int,
        isSimpleInterestInMoratorium: Boolean,
        voluntaryMoratoriumMonthlyPayment: Double = 0.0
    ): LoanCalculationResult {
        if (principal <= 0.0) {
            return LoanCalculationResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, emptyList())
        }

        val annualRateDecimal = annualRatePercent / 100.0
        val monthlyRate = annualRateDecimal / 12.0
        val tenureMonths = (tenureYears.coerceAtLeast(1)) * 12

        // 1. Calculate Moratorium Interest
        val totalMoratoriumYears = moratoriumMonths / 12.0
        val grossMoratoriumInterest = if (moratoriumMonths > 0) {
            if (isSimpleInterestInMoratorium) {
                principal * annualRateDecimal * totalMoratoriumYears
            } else {
                // Compound interest during moratorium
                principal * ((1.0 + monthlyRate).pow(moratoriumMonths)) - principal
            }
        } else {
            0.0
        }

        val totalVoluntaryPaid = voluntaryMoratoriumMonthlyPayment * moratoriumMonths
        val netInterestCapitalized = (grossMoratoriumInterest - totalVoluntaryPaid).coerceAtLeast(0.0)
        val effectivePrincipal = principal + netInterestCapitalized

        // 2. Calculate EMI on Effective Principal
        val emi = if (monthlyRate == 0.0) {
            effectivePrincipal / tenureMonths
        } else {
            val factor = (1.0 + monthlyRate).pow(tenureMonths)
            if (factor - 1.0 == 0.0) {
                effectivePrincipal / tenureMonths
            } else {
                (effectivePrincipal * monthlyRate * factor) / (factor - 1.0)
            }
        }

        val totalRepaymentDuringTenure = emi * tenureMonths
        val grandTotalRepayment = totalRepaymentDuringTenure + totalVoluntaryPaid
        val totalInterestPaid = grandTotalRepayment - principal

        // 3. Amortization Schedule by Year
        val schedule = mutableListOf<AmortizationYearRow>()
        var currentBalance = effectivePrincipal

        for (year in 1..tenureYears) {
            val yearOpen = currentBalance
            var yearPrincipal = 0.0
            var yearInterest = 0.0

            for (m in 1..12) {
                val interestForMonth = currentBalance * monthlyRate
                val principalForMonth = (emi - interestForMonth).coerceAtLeast(0.0)
                yearInterest += interestForMonth
                yearPrincipal += principalForMonth
                currentBalance = (currentBalance - principalForMonth).coerceAtLeast(0.0)
            }

            schedule.add(
                AmortizationYearRow(
                    year = year,
                    openingBalance = round(yearOpen),
                    principalPaid = round(yearPrincipal),
                    interestPaid = round(yearInterest),
                    closingBalance = round(currentBalance)
                )
            )
        }

        return LoanCalculationResult(
            principal = round(principal),
            effectivePrincipal = round(effectivePrincipal),
            monthlyEmi = round(emi),
            moratoriumInterest = round(grossMoratoriumInterest),
            totalRepayment = round(grandTotalRepayment),
            totalInterestPaid = round(totalInterestPaid),
            schedule = schedule
        )
    }

    private fun round(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
