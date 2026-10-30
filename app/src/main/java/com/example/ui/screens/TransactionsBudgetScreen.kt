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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import com.example.data.model.GoalContributionEntity
import com.example.ui.components.BindoraGoalProgressBar
import com.example.ui.components.BindoraGoalProgressCardHeader
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.CategoryAvatar
import com.example.ui.components.formatRupee
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
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
fun TransactionsBudgetScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.transactionsTab.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val budget by viewModel.currentBudget.collectAsState()
    val goals by viewModel.savingsGoals.collectAsState()

    var showEditBudgetDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(PrimaryBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceBackground,
                contentColor = PrimaryDark,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryBrand
                    )
                },
                modifier = Modifier.border(width = 1.dp, color = SubtleBorder)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setTransactionsTab(0) },
                    text = { Text("Expenses", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) },
                    modifier = Modifier.testTag("tab_expenses")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setTransactionsTab(1) },
                    text = { Text("Category Budgets", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) },
                    modifier = Modifier.testTag("tab_budgets")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setTransactionsTab(2) },
                    text = { Text("Savings Goals", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) },
                    modifier = Modifier.testTag("tab_goals")
                )
            }

            when (selectedTab) {
                0 -> TransactionsListTab(viewModel = viewModel, transactions = transactions)
                1 -> CategoryBudgetsTab(
                    viewModel = viewModel,
                    transactions = transactions,
                    onEditLimitsClick = { showEditBudgetDialog = true }
                )
                2 -> SavingsGoalsTab(viewModel = viewModel, goals = goals)
            }
        }

        // Floating Action Buttons
        if (selectedTab == 0) {
            FloatingActionButton(
                onClick = { viewModel.showAddTransaction() },
                containerColor = PrimaryBrand,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("fab_add_expense")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        } else if (selectedTab == 2) {
            FloatingActionButton(
                onClick = { viewModel.showAddGoal() },
                containerColor = PrimaryBrand,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("fab_add_goal")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Savings Goal")
            }
        }
    }

    if (showEditBudgetDialog) {
        EditBudgetLimitsDialog(
            currentTotal = budget?.totalLimit ?: 5000.0,
            canteen = budget?.canteenLimit ?: 1600.0,
            groceries = budget?.groceriesLimit ?: 800.0,
            travel = budget?.travelLimit ?: 600.0,
            books = budget?.booksLimit ?: 500.0,
            entertainment = budget?.entertainmentLimit ?: 600.0,
            recharge = budget?.rechargeLimit ?: 300.0,
            onDismiss = { showEditBudgetDialog = false },
            onSave = { total, c, g, tr, bk, ent, rec ->
                viewModel.updateBudgetLimits(
                    total = total,
                    canteen = c,
                    groceries = g,
                    travel = tr,
                    books = bk,
                    entertainment = ent,
                    recharge = rec,
                    shopping = 400.0,
                    other = 200.0
                )
                showEditBudgetDialog = false
            }
        )
    }
}

@Composable
fun TransactionsListTab(
    viewModel: FinanceViewModel,
    transactions: List<TransactionEntity>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var selectedTypeFilter by remember { mutableStateOf<Boolean?>(null) }
    val pendingReviewCount by viewModel.pendingReviewCount.collectAsState()

    val filtered = transactions.filter { tx ->
        val matchesQuery = searchQuery.isBlank() || tx.title.contains(searchQuery, ignoreCase = true) || tx.category.contains(searchQuery, ignoreCase = true)
        val matchesCat = selectedCategoryFilter == null || tx.category.equals(selectedCategoryFilter, ignoreCase = true)
        val matchesType = selectedTypeFilter == null || tx.isIncome == selectedTypeFilter
        matchesQuery && matchesCat && matchesType
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Auto Tracking & Statement Ingestion Banner
        item {
            Surface(
                color = SoftMint,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryBrand.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    .testTag("budget_auto_tracking_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto Payment Tracking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PrimaryDark
                        )
                        Text(
                            text = if (pendingReviewCount > 0) "$pendingReviewCount transaction(s) ready for review" else "Zero-credential local tracking & CSV import",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(MainScreen.CONNECTED_ACCOUNTS) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("budget_banner_sources_btn")
                        ) {
                            Text("Sources", fontSize = 11.sp, color = PrimaryDark)
                        }
                        Button(
                            onClick = { viewModel.navigateTo(MainScreen.IMPORTED_TRANSACTIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("budget_banner_review_btn")
                        ) {
                            Text(
                                text = if (pendingReviewCount > 0) "Review ($pendingReviewCount)" else "Import",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search chai, metro, canteen, books...", fontSize = 13.sp, color = SecondaryText) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SecondaryText) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_search_field")
            )
        }

        // Filter Chips Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    EditorialFilterChip(
                        text = "All",
                        isSelected = selectedTypeFilter == null && selectedCategoryFilter == null,
                        onClick = {
                            selectedTypeFilter = null
                            selectedCategoryFilter = null
                        }
                    )
                }
                item {
                    EditorialFilterChip(
                        text = "Expenses",
                        isSelected = selectedTypeFilter == false,
                        onClick = { selectedTypeFilter = if (selectedTypeFilter == false) null else false }
                    )
                }
                item {
                    EditorialFilterChip(
                        text = "Allowance / Income",
                        isSelected = selectedTypeFilter == true,
                        onClick = { selectedTypeFilter = if (selectedTypeFilter == true) null else true }
                    )
                }
                items(FinanceConstants.EXPENSE_CATEGORIES) { cat ->
                    EditorialFilterChip(
                        text = cat.name,
                        isSelected = selectedCategoryFilter == cat.name,
                        onClick = { selectedCategoryFilter = if (selectedCategoryFilter == cat.name) null else cat.name }
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No matching transactions", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try resetting filters or search term.", fontSize = 12.sp, color = SecondaryText)
                    }
                }
            }
        } else {
            items(filtered) { tx ->
                PolishedTransactionRow(
                    transaction = tx,
                    onClick = { viewModel.showEditTransaction(tx) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun EditorialFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryBrand else SurfaceBackground)
            .border(width = 1.dp, color = if (isSelected) PrimaryBrand else SubtleBorder, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else PrimaryText
        )
    }
}

@Composable
fun CategoryBudgetsTab(
    viewModel: FinanceViewModel,
    transactions: List<TransactionEntity>,
    onEditLimitsClick: () -> Unit
) {
    val budget by viewModel.currentBudget.collectAsState()
    val currentMonthDate = Date()
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonthDate)
    val currentMonthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(currentMonthDate)

    val monthTransactions = transactions.filter {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(it.dateMillis)) == currentMonthKey && !it.isIncome
    }

    val categorySpent = monthTransactions.groupBy { it.category }.mapValues { it.value.sumOf { item -> item.amount } }

    val categoriesWithLimits = listOf(
        Pair("Food & Canteen", budget?.canteenLimit ?: 1600.0),
        Pair("Groceries", budget?.groceriesLimit ?: 800.0),
        Pair("Travel & Metro", budget?.travelLimit ?: 600.0),
        Pair("Books & Stationery", budget?.booksLimit ?: 500.0),
        Pair("Entertainment & OTT", budget?.entertainmentLimit ?: 600.0),
        Pair("Mobile & Internet", budget?.rechargeLimit ?: 300.0)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Month Selector Banner & Edit Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceBackground)
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PrimaryBrand, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(monthName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryText)
                }

                Button(
                    onClick = onEditLimitsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SoftMint),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("button_edit_budget_limits")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Limits", color = PrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(categoriesWithLimits) { (categoryName, limit) ->
            val spent = categorySpent[categoryName] ?: 0.0
            val progress = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f
            val percent = if (limit > 0) ((spent / limit) * 100).toInt() else 0
            val isOver80 = percent >= 80
            val isOver100 = percent >= 100

            val cardBg = when {
                isOver100 -> ErrorBackground
                isOver80 -> WarningBackground
                else -> SurfaceBackground
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryAvatar(categoryName = categoryName, size = 38)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(categoryName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                                Text("Limit: ${formatRupee(limit)}", fontSize = 11.sp, color = SecondaryText)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatRupee(spent), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (isOver100) ErrorColor else PrimaryText)
                            Text(
                                "$percent% used",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOver100) ErrorColor else if (isOver80) WarningColor else SuccessColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val barColor = if (isOver100) ErrorColor else if (isOver80) WarningColor else PrimaryBrand
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = barColor,
                        trackColor = PrimaryBackground
                    )

                    if (isOver80) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isOver100) ErrorColor else WarningColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOver100) "Over limit! Stick to hostel mess to avoid borrowing." else "Approaching budget threshold (>80% used).",
                                fontSize = 10.sp,
                                color = if (isOver100) ErrorColor else WarningColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

@Composable
fun SavingsGoalsTab(
    viewModel: FinanceViewModel,
    goals: List<SavingsGoalEntity>
) {
    var goalToContribute by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var contributeAmountText by remember { mutableStateOf("200") }
    var contributeNoteText by remember { mutableStateOf("Pocket money savings") }
    var goalToDelete by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var expandedGoalHistoryId by remember { mutableStateOf<Long?>(null) }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "COMPLETED"

    val totalSaved = goals.sumOf { it.savedAmount }
    val totalTarget = goals.sumOf { it.targetAmount }
    val overallProgress = if (totalTarget > 0) (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f) else 0f
    val overallPercent = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toInt() else 0
    val completedCount = goals.count { it.savedAmount >= it.targetAmount }
    val activeCount = goals.size - completedCount

    val filteredGoals = when (selectedFilter) {
        "ACTIVE" -> goals.filter { it.savedAmount < it.targetAmount }
        "COMPLETED" -> goals.filter { it.savedAmount >= it.targetAmount }
        else -> goals
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Screen Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Savings Goals & Milestones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PrimaryText
                    )
                    Text(
                        text = "Set, track & achieve intentional college savings with Bindora.",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
                IconButton(onClick = { viewModel.showAddGoal() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Goal",
                        tint = PrimaryBrand
                    )
                }
            }
        }

        // 1. Hero Portfolio Summary Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(18.dp))
                    .testTag("savings_summary_card")
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
                                    text = "TOTAL SAVINGS PORTFOLIO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryText,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${goals.size} Active & Completed Goals",
                                    fontSize = 11.sp,
                                    color = PrimaryDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Percentage chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftMint)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$overallPercent% Saved",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = formatRupee(totalSaved),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = "of ${formatRupee(totalTarget)} target accumulated across college goals",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Signature Bindora Progress Bar
                    BindoraGoalProgressBar(
                        progress = overallProgress,
                        height = 12.dp,
                        showMilestones = true,
                        modifier = Modifier.testTag("overall_savings_progress_bar")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryBackground)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "⚡ $activeCount In Progress",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryBackground)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🏆 $completedCount Achieved",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBrand
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.showAddGoal() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Goal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Filter Selector Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("ALL", "All (${goals.size})", "filter_all_goals"),
                    Triple("ACTIVE", "In Progress ($activeCount)", "filter_active_goals"),
                    Triple("COMPLETED", "Completed ($completedCount)", "filter_completed_goals")
                ).forEach { (key, label, tag) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PrimaryDark else SurfaceBackground)
                            .border(width = 1.dp, color = if (isSelected) PrimaryDark else SubtleBorder, shape = RoundedCornerShape(10.dp))
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag(tag)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else PrimaryText
                        )
                    }
                }
            }
        }

        // 3. Goal Items
        if (filteredGoals.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SoftMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Savings, contentDescription = null, tint = PrimaryBrand, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedFilter == "COMPLETED") "No Completed Goals Yet" else "No Goals Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Set a goal for laptop, hostel emergency fund, or college trip to start tracking.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.showAddGoal() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Create Savings Goal")
                        }
                    }
                }
            }
        } else {
            items(filteredGoals, key = { it.id }) { goal ->
                val progress = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                val percent = if (goal.targetAmount > 0) ((goal.savedAmount / goal.targetAmount) * 100).toInt() else 0
                val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)
                val isCompleted = goal.savedAmount >= goal.targetAmount

                val now = System.currentTimeMillis()
                val diffDays = ((goal.targetDateMillis - now) / (24L * 60 * 60 * 1000)).coerceAtLeast(0)
                val dailyPace = if (diffDays > 0 && remaining > 0) (remaining / diffDays).toInt() else 0

                val isHistoryExpanded = expandedGoalHistoryId == goal.id
                val contributionsFlow = remember(goal.id) { viewModel.getGoalContributionsFlow(goal.id) }
                val contributions by contributionsFlow.collectAsState(initial = emptyList())

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(18.dp))
                        .testTag("goal_card_${goal.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header with Category Chip, Title, Edit & Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SoftMint)
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = goal.category.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDark,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = goal.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = PrimaryText,
                                    maxLines = 1
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.showEditGoal(goal) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Goal",
                                        tint = SecondaryText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { goalToDelete = goal },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Goal",
                                        tint = ErrorColor.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Target Date and Pace Information
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = SecondaryText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCompleted) "Target Achieved!" else "Target: ${dateFormatter.format(Date(goal.targetDateMillis))} ($diffDays days left)",
                                    fontSize = 11.sp,
                                    color = if (isCompleted) PrimaryBrand else SecondaryText,
                                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            // Percentage / Completion Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCompleted) PrimaryGreen else SoftMint)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isCompleted) "COMPLETED 🎉" else "$percent% SAVED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) Color.White else PrimaryDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Saved vs Target Display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = formatRupee(goal.savedAmount),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "saved of ${formatRupee(goal.targetAmount)} target",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }

                            if (!isCompleted) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${formatRupee(remaining)} to go",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBrand
                                    )
                                    if (dailyPace > 0) {
                                        Text(
                                            text = "Save ~₹$dailyPace/day",
                                            fontSize = 10.sp,
                                            color = SecondaryText
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bindora Teal-and-Green Progress Bar
                        BindoraGoalProgressBar(
                            progress = progress,
                            height = 10.dp,
                            showMilestones = true,
                            testTag = "goal_progress_${goal.id}"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Deposit Buttons & Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (!isCompleted) {
                                    Button(
                                        onClick = {
                                            viewModel.addSavingsContribution(goal, 100.0, "Quick savings deposit")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBackground),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.border(1.dp, SubtleBorder, RoundedCornerShape(8.dp))
                                    ) {
                                        Text("+₹100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.addSavingsContribution(goal, 500.0, "Quick savings deposit")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBackground),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.border(1.dp, SubtleBorder, RoundedCornerShape(8.dp))
                                    ) {
                                        Text("+₹500", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDark)
                                    }
                                }
                                Button(
                                    onClick = {
                                        goalToContribute = goal
                                        contributeAmountText = "200"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftMint),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("button_deposit_goal_${goal.id}")
                                ) {
                                    Text("+ Deposit", color = PrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // View History toggle
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable {
                                        expandedGoalHistoryId = if (isHistoryExpanded) null else goal.id
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isHistoryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = SecondaryText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (isHistoryExpanded) "Hide" else "History (${contributions.size})",
                                    fontSize = 11.sp,
                                    color = SecondaryText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Expanded History Section
                        if (isHistoryExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryBackground)
                                    .border(1.dp, SubtleBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Deposit History:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryDark
                                    )
                                    if (contributions.isEmpty()) {
                                        Text(
                                            text = "No deposits logged yet. Add your first contribution above!",
                                            fontSize = 11.sp,
                                            color = SecondaryText
                                        )
                                    } else {
                                        contributions.take(5).forEach { contribution ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = if (contribution.note.isNotBlank()) contribution.note else "Savings Contribution",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = PrimaryText
                                                    )
                                                    Text(
                                                        text = dateFormatter.format(Date(contribution.dateMillis)),
                                                        fontSize = 9.sp,
                                                        color = SecondaryText
                                                    )
                                                }
                                                Text(
                                                    text = "+${formatRupee(contribution.amount)}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryBrand
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Deposit Modal Dialog
    if (goalToContribute != null) {
        val target = goalToContribute!!
        AlertDialog(
            onDismissRequest = { goalToContribute = null },
            title = {
                Text(
                    text = "Deposit to '${target.title}'",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Allocate savings from your monthly pocket money, stipend, or cashback.",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )

                    OutlinedTextField(
                        value = contributeAmountText,
                        onValueChange = { contributeAmountText = it },
                        label = { Text("Deposit Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_deposit_amount")
                    )

                    // Quick contribution chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("100", "200", "500", "1000", "2000").forEach { quickAmount ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (contributeAmountText == quickAmount) PrimaryBrand else PrimaryBackground)
                                    .border(
                                        width = 1.dp,
                                        color = if (contributeAmountText == quickAmount) PrimaryBrand else SubtleBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { contributeAmountText = quickAmount }
                                    .padding(horizontal = 9.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "₹$quickAmount",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (contributeAmountText == quickAmount) Color.White else PrimaryText
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = contributeNoteText,
                        onValueChange = { contributeNoteText = it },
                        label = { Text("Note (e.g. Canteen savings, Diwali gift)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = contributeAmountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.addSavingsContribution(
                                target,
                                amt,
                                contributeNoteText.ifBlank { "Savings contribution" }
                            )
                        }
                        goalToContribute = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_deposit_button")
                ) {
                    Text("Confirm Deposit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToContribute = null }) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (goalToDelete != null) {
        val target = goalToDelete!!
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = {
                Text("Delete '${target.title}'?", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText)
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this savings goal? Recorded milestone progress will be removed.",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSavingsGoal(target)
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete Goal", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Keep Goal", color = SecondaryText)
                }
            }
        )
    }
}

@Composable
fun EditBudgetLimitsDialog(
    currentTotal: Double,
    canteen: Double,
    groceries: Double,
    travel: Double,
    books: Double,
    entertainment: Double,
    recharge: Double,
    onDismiss: () -> Unit,
    onSave: (Double, Double, Double, Double, Double, Double, Double) -> Unit
) {
    var totalText by remember { mutableStateOf(currentTotal.toInt().toString()) }
    var canteenText by remember { mutableStateOf(canteen.toInt().toString()) }
    var groceriesText by remember { mutableStateOf(groceries.toInt().toString()) }
    var travelText by remember { mutableStateOf(travel.toInt().toString()) }
    var booksText by remember { mutableStateOf(books.toInt().toString()) }
    var entText by remember { mutableStateOf(entertainment.toInt().toString()) }
    var recText by remember { mutableStateOf(recharge.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Monthly Budget Limits", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = PrimaryText) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = totalText,
                        onValueChange = { totalText = it },
                        label = { Text("Total Monthly Pocket Money (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = canteenText,
                        onValueChange = { canteenText = it },
                        label = { Text("Food & Canteen Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = groceriesText,
                        onValueChange = { groceriesText = it },
                        label = { Text("Groceries Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = travelText,
                        onValueChange = { travelText = it },
                        label = { Text("Travel & Metro Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = booksText,
                        onValueChange = { booksText = it },
                        label = { Text("Books & Stationery Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = entText,
                        onValueChange = { entText = it },
                        label = { Text("Entertainment & Outing Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = recText,
                        onValueChange = { recText = it },
                        label = { Text("Mobile & Internet Recharge (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        totalText.toDoubleOrNull() ?: 5000.0,
                        canteenText.toDoubleOrNull() ?: 1600.0,
                        groceriesText.toDoubleOrNull() ?: 800.0,
                        travelText.toDoubleOrNull() ?: 600.0,
                        booksText.toDoubleOrNull() ?: 500.0,
                        entText.toDoubleOrNull() ?: 600.0,
                        recText.toDoubleOrNull() ?: 300.0
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand)
            ) {
                Text("Save Limits", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}

