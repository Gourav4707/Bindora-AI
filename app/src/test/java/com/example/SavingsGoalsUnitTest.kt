package com.example

import com.example.data.model.SavingsGoalEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavingsGoalsUnitTest {

    @Test
    fun `test savings goal progress percentage calculation`() {
        val goal = SavingsGoalEntity(
            id = 1L,
            title = "Coding Laptop",
            targetAmount = 40000.0,
            savedAmount = 10000.0,
            category = "Gadgets"
        )

        val progress = (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
        val percent = ((goal.savedAmount / goal.targetAmount) * 100).toInt()
        val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)

        assertEquals(0.25f, progress, 0.001f)
        assertEquals(25, percent)
        assertEquals(30000.0, remaining, 0.001)
        assertFalse(goal.savedAmount >= goal.targetAmount)
    }

    @Test
    fun `test savings goal completion status`() {
        val completedGoal = SavingsGoalEntity(
            id = 2L,
            title = "Hostel Emergency Buffer",
            targetAmount = 5000.0,
            savedAmount = 5000.0,
            category = "Emergency"
        )

        val isCompleted = completedGoal.savedAmount >= completedGoal.targetAmount
        val remaining = (completedGoal.targetAmount - completedGoal.savedAmount).coerceAtLeast(0.0)
        val progress = (completedGoal.savedAmount / completedGoal.targetAmount).toFloat().coerceIn(0f, 1f)

        assertTrue(isCompleted)
        assertEquals(0.0, remaining, 0.001)
        assertEquals(1.0f, progress, 0.001f)
    }

    @Test
    fun `test savings goal over-contribution clamping`() {
        val overachievedGoal = SavingsGoalEntity(
            id = 3L,
            title = "Semester Fees",
            targetAmount = 3000.0,
            savedAmount = 3500.0,
            category = "Fees"
        )

        val progress = (overachievedGoal.savedAmount / overachievedGoal.targetAmount).toFloat().coerceIn(0f, 1f)
        val remaining = (overachievedGoal.targetAmount - overachievedGoal.savedAmount).coerceAtLeast(0.0)

        // Progress bar should clamp to 1f (100%)
        assertEquals(1.0f, progress, 0.001f)
        assertEquals(0.0, remaining, 0.001)
        assertTrue(overachievedGoal.savedAmount >= overachievedGoal.targetAmount)
    }

    @Test
    fun `test daily savings pace calculation`() {
        val now = System.currentTimeMillis()
        val fortyDaysMillis = 40L * 24 * 60 * 60 * 1000
        val targetDate = now + fortyDaysMillis

        val goal = SavingsGoalEntity(
            id = 4L,
            title = "Goa Semester Trip",
            targetAmount = 8000.0,
            savedAmount = 4000.0,
            targetDateMillis = targetDate,
            category = "Travel"
        )

        val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)
        val diffDays = ((goal.targetDateMillis - now) / (24L * 60 * 60 * 1000)).coerceAtLeast(1)
        val dailyPace = if (diffDays > 0 && remaining > 0) (remaining / diffDays).toInt() else 0

        assertEquals(4000.0, remaining, 0.001)
        assertEquals(40L, diffDays)
        // 4000 / 40 = 100 rupees per day
        assertEquals(100, dailyPace)
    }

    @Test
    fun `test zero target amount edge case does not divide by zero`() {
        val zeroTargetGoal = SavingsGoalEntity(
            id = 5L,
            title = "Free Goal",
            targetAmount = 0.0,
            savedAmount = 0.0,
            category = "Personal"
        )

        val progress = if (zeroTargetGoal.targetAmount > 0) {
            (zeroTargetGoal.savedAmount / zeroTargetGoal.targetAmount).toFloat().coerceIn(0f, 1f)
        } else 0f

        val percent = if (zeroTargetGoal.targetAmount > 0) {
            ((zeroTargetGoal.savedAmount / zeroTargetGoal.targetAmount) * 100).toInt()
        } else 0

        assertEquals(0f, progress, 0.001f)
        assertEquals(0, percent)
    }
}
