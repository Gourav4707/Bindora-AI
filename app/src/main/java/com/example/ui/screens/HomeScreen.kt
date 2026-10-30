package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.components.BindoraGoalProgressBar
import com.example.ui.components.CategoryAvatar
import com.example.ui.components.formatRupee
import com.example.ui.components.getTimeBasedGreeting
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.WarningBackground
import com.example.ui.theme.WarningColor
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MainScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val budget by viewModel.currentBudget.collectAsState()
    val goals by viewModel.savingsGoals.collectAsState()
    val pendingReviewCount by viewModel.pendingReviewCount.collectAsState()

    var selectedUpcomingModule by remember { mutableStateOf<UpcomingModuleDetail?>(null) }
    var notificationSubscribed by remember { mutableStateOf(setOf<String>()) }

    // Greeting info
    val greeting = getTimeBasedGreeting()
    val firstName = profile?.name?.trim()?.split(" ")?.firstOrNull()?.ifBlank { "Student" } ?: "Student"
    val collegeName = profile?.college?.ifBlank { "College Campus" } ?: "College Campus"
    val isHosteller = profile?.isHosteller ?: true

    // Current month transactions or sample data
    val currentMonthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    val monthTransactions = transactions.filter {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(it.dateMillis)) == currentMonthKey
    }

    // Default sample figures if new user
    val monthlyBudgetLimit = budget?.totalLimit ?: (profile?.monthlyPocketMoney ?: 5000.0)
    val actualExpenses = monthTransactions.filter { !it.isIncome }.sumOf { it.amount }
    val totalExpenses = if (actualExpenses > 0) actualExpenses else 3000.0 // Sample display
    val remainingBudget = (monthlyBudgetLimit - totalExpenses).coerceAtLeast(0.0)
    val budgetProgress = if (monthlyBudgetLimit > 0) (totalExpenses / monthlyBudgetLimit).toFloat().coerceIn(0f, 1f) else 0.6f
    val budgetPercent = (budgetProgress * 100).toInt()

    val categoryExpenses = if (monthTransactions.isNotEmpty()) {
        monthTransactions.filter { !it.isIncome }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    } else {
        mapOf(
            "Food & Canteen" to 1500.0,
            "Travel & Metro" to 600.0,
            "Books & Stationery" to 450.0,
            "Groceries" to 300.0,
            "Other Expense" to 150.0
        )
    }

    val topCategoryEntry = categoryExpenses.maxByOrNull { it.value }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // 1. GREETING BANNER
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_greeting_section")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$greeting, $firstName 👋",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Here is your college money overview for this month.",
                            fontSize = 13.sp,
                            color = SecondaryText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isHosteller) SoftBlue else WarningBackground)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isHosteller) "Hostel Resident" else "Day Scholar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHosteller) SecondaryBlue else WarningColor
                        )
                    }
                }
            }
        }

        // 2. FINANCIAL OVERVIEW HERO SECTION (SAMPLE / LIVE DATA)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
                    .testTag("home_financial_overview_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MONTHLY BUDGET",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryText,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryBackground)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (actualExpenses > 0) "LIVE" else "SAMPLE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBrand
                                )
                            }
                        }

                        val badgeColor = when {
                            budgetPercent >= 100 -> ErrorColor
                            budgetPercent >= 80 -> WarningColor
                            else -> PrimaryBrand
                        }
                        val badgeBg = when {
                            budgetPercent >= 100 -> ErrorBackground
                            budgetPercent >= 80 -> WarningBackground
                            else -> SoftMint
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$budgetPercent% used",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = badgeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hero Financial Number
                    Text(
                        text = formatRupee(monthlyBudgetLimit),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${formatRupee(totalExpenses)} spent · ${formatRupee(remainingBudget)} remaining",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Indicator
                    val progressBarColor = when {
                        budgetPercent >= 100 -> ErrorColor
                        budgetPercent >= 80 -> WarningColor
                        else -> PrimaryBrand
                    }
                    LinearProgressIndicator(
                        progress = { budgetProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = progressBarColor,
                        trackColor = SoftMint,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Distinction note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryBackground, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "💡 Planned Allowance: Remaining budget represents your planned spending cap, separate from your physical bank account balance.",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // 3. QUICK ACTIONS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    icon = Icons.Default.Add,
                    label = "Add Expense",
                    accentColor = PrimaryBrand,
                    modifier = Modifier.weight(1f).testTag("quick_action_add_expense"),
                    onClick = { viewModel.showAddTransaction() }
                )
                QuickActionItem(
                    icon = Icons.Default.PieChart,
                    label = "View Budget",
                    accentColor = SecondaryBlue,
                    modifier = Modifier.weight(1f).testTag("quick_action_view_budget"),
                    onClick = {
                        viewModel.setTransactionsTab(1)
                        viewModel.navigateTo(MainScreen.TRANSACTIONS_BUDGET)
                    }
                )
                QuickActionItem(
                    icon = Icons.Default.AutoAwesome,
                    label = "Ask AI",
                    accentColor = PrimaryDark,
                    modifier = Modifier.weight(1f).testTag("quick_action_ask_ai"),
                    onClick = { viewModel.navigateTo(MainScreen.AI_COACH) }
                )
                QuickActionItem(
                    icon = Icons.Default.Savings,
                    label = "Savings Goals",
                    accentColor = WarningColor,
                    modifier = Modifier.weight(1f).testTag("quick_action_create_goal"),
                    onClick = {
                        viewModel.setTransactionsTab(2)
                        viewModel.navigateTo(MainScreen.TRANSACTIONS_BUDGET)
                    }
                )
            }
        }

        // Automatic UPI & Payment Tracking Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SoftMint),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = PrimaryBrand.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
                    .testTag("home_auto_tracking_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBrand.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = null,
                            tint = PrimaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto UPI & Payment Tracking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryDark
                        )
                        Text(
                            text = if (pendingReviewCount > 0) "$pendingReviewCount transaction(s) pending review" else "On-device payment alert listener active",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                    Button(
                        onClick = {
                            if (pendingReviewCount > 0) {
                                viewModel.navigateTo(MainScreen.IMPORTED_TRANSACTIONS)
                            } else {
                                viewModel.navigateTo(MainScreen.CONNECTED_ACCOUNTS)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("home_auto_tracking_action_button")
                    ) {
                        Text(
                            text = if (pendingReviewCount > 0) "Review" else "Accounts",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // SAVINGS GOALS SPOTLIGHT MODULE
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(18.dp))
                    .testTag("home_savings_goals_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(SoftMint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = PrimaryDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Savings Goals",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "${goals.size} active student goal(s)",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        Text(
                            text = "Manage All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBrand,
                            modifier = Modifier.clickable {
                                viewModel.setTransactionsTab(2)
                                viewModel.navigateTo(MainScreen.TRANSACTIONS_BUDGET)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (goals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryBackground)
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No savings goals set yet",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Start saving intentionally for a laptop, semester fees, or emergency buffer.",
                                    fontSize = 11.sp,
                                    color = SecondaryText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.showAddGoal() },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("+ Set Goal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        val topGoal = goals.firstOrNull { it.savedAmount < it.targetAmount } ?: goals.first()
                        val progress = if (topGoal.targetAmount > 0) (topGoal.savedAmount / topGoal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                        val percent = if (topGoal.targetAmount > 0) ((topGoal.savedAmount / topGoal.targetAmount) * 100).toInt() else 0
                        val remaining = (topGoal.targetAmount - topGoal.savedAmount).coerceAtLeast(0.0)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBackground)
                                .border(1.dp, SubtleBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = topGoal.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PrimaryText
                                    )
                                    Text(
                                        text = "${formatRupee(topGoal.savedAmount)} of ${formatRupee(topGoal.targetAmount)} saved",
                                        fontSize = 11.sp,
                                        color = SecondaryText
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (remaining <= 0) PrimaryGreen else SoftMint)
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (remaining <= 0) "100% DONE" else "$percent%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (remaining <= 0) Color.White else PrimaryDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Signature Bindora Progress Bar
                            BindoraGoalProgressBar(
                                progress = progress,
                                height = 8.dp,
                                showMilestones = false,
                                modifier = Modifier.testTag("home_goal_progress_bar")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (remaining <= 0) "🎉 Goal achieved!" else "${formatRupee(remaining)} remaining",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (remaining <= 0) PrimaryGreen else PrimaryBrand
                                )

                                Button(
                                    onClick = {
                                        viewModel.setTransactionsTab(2)
                                        viewModel.navigateTo(MainScreen.TRANSACTIONS_BUDGET)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftMint),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("+ Deposit", color = PrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. MODULE: SPENDING INSIGHTS ("Your money at a glance")
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(18.dp))
                    .testTag("module_spending_insights")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Spending Insights",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PrimaryText
                            )
                            Text(
                                text = "Category breakdown of pocket money",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                        Text(
                            text = "Set Limits",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBrand,
                            modifier = Modifier.clickable {
                                viewModel.setTransactionsTab(1)
                                viewModel.navigateTo(MainScreen.TRANSACTIONS_BUDGET)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val sorted = categoryExpenses.toList().sortedByDescending { it.second }.take(4)
                    sorted.forEach { (cat, amt) ->
                        val share = if (totalExpenses > 0) (amt / totalExpenses).toFloat() else 0f
                        val sharePercent = (share * 100).toInt()

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                                Text("${formatRupee(amt)} ($sharePercent%)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDark)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { share },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PrimaryBrand,
                                trackColor = PrimaryBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Alert observation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SoftMint)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PrimaryBrand, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Food & Canteen accounts for 50% of your expenses. Preparing room chai can save ₹400/month.",
                                fontSize = 11.sp,
                                color = PrimaryDark,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. MODULE: AI MONEY COACH (SIGNATURE CARD & SHORTCUTS)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(18.dp))
                    .testTag("module_ai_coach")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryBrand,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bindora AI Coach",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PrimaryText
                            )
                            Text(
                                text = "Every Rupee Has a Purpose • Answers in Hindi, Hinglish & English",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tap a question to ask Bindora AI:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "How can I save ₹500 from my pocket money?",
                            "Explain SIPs in simple Hinglish.",
                            "How to reduce canteen snacking expenses?"
                        ).forEach { prompt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryBackground)
                                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.sendMessageToAICoach(prompt)
                                        viewModel.navigateTo(MainScreen.AI_COACH)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(prompt, fontSize = 11.sp, color = PrimaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimaryBrand, modifier = Modifier.size(13.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.navigateTo(MainScreen.AI_COACH) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chat with Money Coach", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 6. MODULE: RECENT ACTIVITY
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Activity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PrimaryText
                    )
                    Text(
                        text = "Latest expenses & allowance receipts",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
                Text(
                    text = "See All (${transactions.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrand,
                    modifier = Modifier.clickable {
                        viewModel.setTransactionsTab(0)
                        viewModel.navigateTo(MainScreen.TRANSACTIONS_BUDGET)
                    }
                )
            }
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No expenses recorded yet", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Record your canteen snack or metro ride.", fontSize = 12.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.showAddTransaction() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Add Expense")
                        }
                    }
                }
            }
        } else {
            items(transactions.take(4)) { tx ->
                PolishedTransactionRow(
                    transaction = tx,
                    onClick = { viewModel.showEditTransaction(tx) }
                )
            }
        }

        // 7. PLACEHOLDERS FOR UPCOMING MODULES
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Upcoming Modules",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryText
                )
                Text(
                    text = "New tools currently in engineering development",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }

        item {
            UpcomingModuleCard(
                icon = Icons.Default.Sms,
                title = "Automated UPI SMS Parser",
                description = "Auto-detect debit and credit SMS from SBI, HDFC, and Paytm without manual typing.",
                tag = "COMING SOON",
                onClick = {
                    selectedUpcomingModule = UpcomingModuleDetail(
                        title = "Automated UPI SMS Parser",
                        tag = "COMING SOON",
                        icon = Icons.Default.Sms,
                        description = "Smart on-device detection of bank and UPI transactions to eliminate manual logging.",
                        details = "Designed specifically for Indian student banking patterns. It parses transactional SMS notifications from banks (SBI, HDFC, ICICI, PNB) and payment apps (GPay, PhonePe, Paytm) to automatically prompt and categorize canteen snacks, metro rides, and college supplies.",
                        keyFeatures = listOf(
                            "100% on-device local parsing — zero cloud or server transmission",
                            "Auto-detects merchant names (Canteen, Metro, Amazon, Swiggy)",
                            "Privacy-safe regex rules strictly ignoring OTPs and personal texts"
                        )
                    )
                }
            )
        }

        item {
            UpcomingModuleCard(
                icon = Icons.Default.Group,
                title = "Hostel Roommate Bill Splitter",
                description = "Seamlessly split midnight Maggie, chai, water jar, and room electricity dues.",
                tag = "IN DESIGN",
                onClick = {
                    selectedUpcomingModule = UpcomingModuleDetail(
                        title = "Hostel Roommate Bill Splitter",
                        tag = "IN DESIGN",
                        icon = Icons.Default.Group,
                        description = "Fair and painless expense sharing for room, flat, and hostel life.",
                        details = "Tired of awkward WhatsApp follow-ups for late-night Maggie, chai, Wi-Fi recharge, or 20-litre water cans? This module allows hostel roommates to create group pots, log shared purchases, calculate simplified debt transfers, and settle in one tap via UPI.",
                        keyFeatures = listOf(
                            "Equal & custom ratio splits (e.g., student who doesn't drink tea)",
                            "Debt minimization algorithm (fewest UPI transfers between friends)",
                            "One-tap 'Pay via UPI' deep links with prefilled VPA and amount"
                        )
                    )
                }
            )
        }

        item {
            UpcomingModuleCard(
                icon = Icons.Default.School,
                title = "Scholarship Deadlines Tracker",
                description = "Get notified about state and central scholarship portals before application windows close.",
                tag = "NEXT UP",
                onClick = {
                    selectedUpcomingModule = UpcomingModuleDetail(
                        title = "Scholarship Deadlines Tracker",
                        tag = "NEXT UP",
                        icon = Icons.Default.School,
                        description = "Curated Indian student scholarships, fee waivers, and merit grants.",
                        details = "Never miss financial aid deadlines again. We track National Scholarship Portal (NSP), state fee waivers (MahaDBT, e-Kalyan, Post-Matric), and private education trusts with eligibility filters tailored to engineering, medical, commerce, and arts colleges.",
                        keyFeatures = listOf(
                            "Filter by state domicile, course, caste category, and annual family income",
                            "Document checklist reminder (Income Certificate, Bonafide, Marksheet)",
                            "Timely push alerts 7 days before application portal closes"
                        )
                    )
                }
            )
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    selectedUpcomingModule?.let { module ->
        val isNotified = notificationSubscribed.contains(module.title)
        AlertDialog(
            onDismissRequest = { selectedUpcomingModule = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SoftMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = null,
                        tint = PrimaryBrand,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = module.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SoftBlue)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = module.tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryBlue
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = module.details,
                        fontSize = 13.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )

                    Text(
                        text = "Key Highlights:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryText
                    )

                    module.keyFeatures.forEach { feature ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✓ ",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBrand,
                                fontSize = 12.sp
                            )
                            Text(
                                text = feature,
                                fontSize = 12.sp,
                                color = PrimaryText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        notificationSubscribed = if (isNotified) {
                            notificationSubscribed - module.title
                        } else {
                            notificationSubscribed + module.title
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNotified) SuccessColor else PrimaryBrand
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isNotified) "✓ Subscribed for Beta" else "Notify When Ready",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUpcomingModule = null }) {
                    Text("Close", color = SecondaryText, fontSize = 12.sp)
                }
            },
            containerColor = SurfaceBackground,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

data class UpcomingModuleDetail(
    val title: String,
    val tag: String,
    val icon: ImageVector,
    val description: String,
    val details: String,
    val keyFeatures: List<String>
)

@Composable
fun UpcomingModuleCard(
    icon: ImageVector,
    title: String,
    description: String,
    tag: String,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PrimaryBackground)
                    .border(width = 1.dp, color = SubtleBorder, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SecondaryText,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryText)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SoftBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(tag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(description, fontSize = 11.sp, color = SecondaryText, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PolishedTransactionRow(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(transaction.dateMillis))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("transaction_row_${transaction.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryAvatar(categoryName = transaction.category, size = 40)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category,
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "•",
                        fontSize = 10.sp,
                        color = SubtleBorder
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = transaction.paymentMode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBrand
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (transaction.isIncome) "+ " else "- ") + formatRupee(transaction.amount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (transaction.isIncome) SuccessColor else ErrorColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = SecondaryText
                )
            }
        }
    }
}
