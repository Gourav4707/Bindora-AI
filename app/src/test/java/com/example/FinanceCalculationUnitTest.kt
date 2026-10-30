package com.example

import com.example.data.model.AICoachEngine
import com.example.data.model.FinanceConstants
import com.example.data.model.LoanCalculatorHelper
import com.example.data.model.UserProfileEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinanceCalculationUnitTest {

    @Test
    fun testZeroInterestLoanCalculation() {
        val result = LoanCalculatorHelper.calculateLoan(
            principal = 120000.0,
            annualRatePercent = 0.0,
            tenureYears = 1,
            moratoriumMonths = 0,
            isSimpleInterestInMoratorium = true,
            voluntaryMoratoriumMonthlyPayment = 0.0
        )
        // EMI should be 120000 / 12 = 10000
        assertEquals(10000.0, result.monthlyEmi, 0.01)
        assertEquals(0.0, result.totalInterestPaid, 0.01)
        assertEquals(120000.0, result.totalRepayment, 0.01)
    }

    @Test
    fun testStandardEmiCalculation() {
        // Principal 1,00,000, 12% p.a., 1 year tenure, 0 moratorium
        // Monthly rate = 1% = 0.01
        // EMI = 100000 * 0.01 * (1.01^12) / (1.01^12 - 1) = 8884.88
        val result = LoanCalculatorHelper.calculateLoan(
            principal = 100000.0,
            annualRatePercent = 12.0,
            tenureYears = 1,
            moratoriumMonths = 0,
            isSimpleInterestInMoratorium = true
        )
        assertTrue(result.monthlyEmi > 8880.0 && result.monthlyEmi < 8890.0)
        assertTrue(result.totalInterestPaid > 6000.0 && result.totalInterestPaid < 7000.0)
    }

    @Test
    fun testMoratoriumSimpleInterestCalculation() {
        // Principal 10,00,000 at 10% annual simple interest for 2 years (24 months) moratorium
        // Interest = 10,00,000 * 0.10 * 2 = 2,00,000
        val result = LoanCalculatorHelper.calculateLoan(
            principal = 1000000.0,
            annualRatePercent = 10.0,
            tenureYears = 5,
            moratoriumMonths = 24,
            isSimpleInterestInMoratorium = true,
            voluntaryMoratoriumMonthlyPayment = 0.0
        )
        assertEquals(200000.0, result.moratoriumInterest, 0.1)
        assertEquals(1200000.0, result.effectivePrincipal, 0.1)
    }

    @Test
    fun testCategoryAutoSuggestion() {
        val catCanteen = FinanceConstants.suggestCategory("Evening Chai and Samosa with friends", isIncome = false)
        assertEquals("Food & Canteen", catCanteen)

        val catMetro = FinanceConstants.suggestCategory("Delhi Metro Smartcard recharge", isIncome = false)
        assertEquals("Travel & Metro", catMetro)

        val catBooks = FinanceConstants.suggestCategory("Engineering Mathematics Xerox print", isIncome = false)
        assertEquals("Books & Stationery", catBooks)

        val catAllowance = FinanceConstants.suggestCategory("Monthly Pocket Money from Papa", isIncome = true)
        assertEquals("Pocket Money", catAllowance)
    }

    @Test
    fun testAICoachResponseGeneration() {
        val profile = UserProfileEntity(
            name = "Aman",
            monthlyPocketMoney = 5000.0
        )
        val response = AICoachEngine.generateCoachingResponse(
            query = "Mere ₹4,000 pocket money mein se ₹800 kaise bachau?",
            profile = profile,
            totalExpensesThisMonth = 1500.0,
            totalIncomeThisMonth = 5000.0,
            budget = null,
            topExpenseCategory = "Food & Canteen",
            topCategoryAmount = 800.0,
            goals = emptyList()
        )
        assertTrue(response.contains("Pocket Money Saving Blueprint"))
        assertTrue(response.contains("Food & Canteen"))
    }
}
