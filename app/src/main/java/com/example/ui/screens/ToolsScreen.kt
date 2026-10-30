package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SimulatedStock
import com.example.ui.components.formatRupee
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
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

@Composable
fun ToolsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.toolsTab.collectAsState()

    Column(modifier = modifier.fillMaxSize().background(PrimaryBackground)) {
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
                onClick = { viewModel.setToolsTab(0) },
                text = { Text("Education Loan Calculator", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.testTag("tab_loan_calculator")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { viewModel.setToolsTab(1) },
                text = { Text("Paper Trading Simulator", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.testTag("tab_paper_trading")
            )
        }

        when (selectedTab) {
            0 -> LoanCalculatorTab(viewModel = viewModel)
            1 -> PaperTradingTab(viewModel = viewModel)
        }
    }
}

@Composable
fun LoanCalculatorTab(viewModel: FinanceViewModel) {
    val loanResult by viewModel.currentLoanResult.collectAsState()
    val savedScenarios by viewModel.loanScenarios.collectAsState()

    var principal by remember { mutableDoubleStateOf(750000.0) }
    var annualRate by remember { mutableDoubleStateOf(9.5) }
    var tenureYears by remember { mutableIntStateOf(10) }
    var moratoriumMonths by remember { mutableIntStateOf(54) }
    var isSimpleInterestInMoratorium by remember { mutableStateOf(true) }
    var voluntaryMoratoriumMonthlyPayment by remember { mutableDoubleStateOf(0.0) }

    var showSaveScenarioDialog by remember { mutableStateOf(false) }
    var scenarioTitleInput by remember { mutableStateOf("SBI Scholar Loan") }

    fun updateCalculation() {
        viewModel.calculateLoan(
            principal = principal,
            annualRatePercent = annualRate,
            tenureYears = tenureYears,
            moratoriumMonths = moratoriumMonths,
            isSimpleInterestInMoratorium = isSimpleInterestInMoratorium,
            voluntaryMoratoriumMonthlyPayment = voluntaryMoratoriumMonthlyPayment
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Loan Calculator Input Form Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Education Loan Parameters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
                    Text("Calculates moratorium interest & repayment schedule accurately.", fontSize = 11.sp, color = SecondaryText)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Principal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Loan Principal Amount", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                        Text(formatRupee(principal), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBrand)
                    }
                    Slider(
                        value = principal.toFloat(),
                        onValueChange = {
                            principal = (it / 10000).toInt() * 10000.0
                            updateCalculation()
                        },
                        valueRange = 100000f..3000000f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBrand, activeTrackColor = PrimaryBrand)
                    )

                    // Annual Interest Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Annual Interest Rate", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                        Text("${"%.2f".format(annualRate)}% p.a.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBrand)
                    }
                    Slider(
                        value = annualRate.toFloat(),
                        onValueChange = {
                            annualRate = ((it * 10).toInt() / 10.0)
                            updateCalculation()
                        },
                        valueRange = 5f..16f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBrand, activeTrackColor = PrimaryBrand)
                    )

                    // Repayment Tenure
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Repayment Tenure", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                        Text("$tenureYears Years (${tenureYears * 12} Mos)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    }
                    Slider(
                        value = tenureYears.toFloat(),
                        onValueChange = {
                            tenureYears = it.toInt()
                            updateCalculation()
                        },
                        valueRange = 3f..15f,
                        steps = 11,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBrand, activeTrackColor = PrimaryBrand)
                    )

                    // Moratorium Period
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Moratorium (Course + Grace Period)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                        Text("$moratoriumMonths Months", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    }
                    Slider(
                        value = moratoriumMonths.toFloat(),
                        onValueChange = {
                            moratoriumMonths = (it / 6).toInt() * 6
                            updateCalculation()
                        },
                        valueRange = 0f..60f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBrand, activeTrackColor = PrimaryBrand)
                    )

                    // Simple interest toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Simple Interest during Study Period", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                            Text("Most Indian public banks apply simple interest during study", fontSize = 11.sp, color = SecondaryText)
                        }
                        Switch(
                            checked = isSimpleInterestInMoratorium,
                            onCheckedChange = {
                                isSimpleInterestInMoratorium = it
                                updateCalculation()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBrand)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Voluntary Payment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voluntary Monthly Interest during College", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryText)
                        Text(formatRupee(voluntaryMoratoriumMonthlyPayment), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessColor)
                    }
                    Slider(
                        value = voluntaryMoratoriumMonthlyPayment.toFloat(),
                        onValueChange = {
                            voluntaryMoratoriumMonthlyPayment = (it / 500).toInt() * 500.0
                            updateCalculation()
                        },
                        valueRange = 0f..5000f,
                        colors = SliderDefaults.colors(thumbColor = SuccessColor, activeTrackColor = SuccessColor)
                    )
                }
            }
        }

        // Calculation Results Card
        item {
            loanResult?.let { res ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ESTIMATED MONTHLY EMI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText, letterSpacing = 1.sp)
                                Text(formatRupee(res.monthlyEmi), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBrand)
                            }
                            Button(
                                onClick = { showSaveScenarioDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftMint),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", color = PrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Principal vs. Interest Visualization Bar
                        val total = res.totalRepayment
                        val principalShare = if (total > 0) (res.principal / total).toFloat() else 0.5f
                        val interestShare = if (total > 0) (res.totalInterestPaid / total).toFloat() else 0.5f

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryBrand))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Principal (${(principalShare * 100).toInt()}%)", fontSize = 11.sp, color = SecondaryText)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WarningColor))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Total Interest (${(interestShare * 100).toInt()}%)", fontSize = 11.sp, color = SecondaryText)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(principalShare.coerceAtLeast(0.01f))
                                        .fillMaxSize()
                                        .background(PrimaryBrand)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(interestShare.coerceAtLeast(0.01f))
                                        .fillMaxSize()
                                        .background(WarningColor)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            ResultMetric("Accrued Moratorium Interest", formatRupee(res.moratoriumInterest))
                            ResultMetric("Effective Principal", formatRupee(res.effectivePrincipal))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            ResultMetric("Total Interest Paid", formatRupee(res.totalInterestPaid))
                            ResultMetric("Total Repayment", formatRupee(res.totalRepayment))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftMint)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "💡 Section 80E Tax Benefit: 100% of the loan interest (${formatRupee(res.totalInterestPaid)}) can be deducted from your taxable salary for up to 8 years when you begin working.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = PrimaryDark
                            )
                        }
                    }
                }
            }
        }

        // Amortization Schedule Preview
        item {
            loanResult?.let { res ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Amortization Schedule (Yearly)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PrimaryBackground, shape = RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text("Year", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryText, modifier = Modifier.weight(0.8f))
                            Text("Principal", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryText, modifier = Modifier.weight(1.2f))
                            Text("Interest", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryText, modifier = Modifier.weight(1.2f))
                            Text("Balance", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PrimaryText, modifier = Modifier.weight(1.2f))
                        }

                        res.schedule.take(6).forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 8.dp)
                            ) {
                                Text("Yr ${row.year}", fontSize = 11.sp, color = SecondaryText, modifier = Modifier.weight(0.8f))
                                Text(formatRupee(row.principalPaid), fontSize = 11.sp, color = PrimaryText, modifier = Modifier.weight(1.2f))
                                Text(formatRupee(row.interestPaid), fontSize = 11.sp, color = PrimaryText, modifier = Modifier.weight(1.2f))
                                Text(formatRupee(row.closingBalance), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDark, modifier = Modifier.weight(1.2f))
                            }
                        }
                    }
                }
            }
        }

        // Saved Scenarios
        if (savedScenarios.isNotEmpty()) {
            item {
                Text("Saved Scenarios to Compare", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)
            }
            items(savedScenarios) { sc ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                            Text(
                                "Loan: ${formatRupee(sc.principal)} • EMI: ${formatRupee(sc.calculatedEmi)}/mo",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                        IconButton(onClick = { viewModel.deleteLoanScenario(sc) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SecondaryText, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    if (showSaveScenarioDialog) {
        AlertDialog(
            onDismissRequest = { showSaveScenarioDialog = false },
            title = { Text("Save Loan Scenario", color = PrimaryText) },
            text = {
                OutlinedTextField(
                    value = scenarioTitleInput,
                    onValueChange = { scenarioTitleInput = it },
                    label = { Text("Scenario Name (e.g. Canara Bank Vidya Tur)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (scenarioTitleInput.isNotBlank()) {
                            viewModel.saveCurrentLoanScenario(scenarioTitleInput.trim())
                            showSaveScenarioDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand)
                ) {
                    Text("Save Scenario")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveScenarioDialog = false }) { Text("Cancel", color = SecondaryText) }
            }
        )
    }
}

@Composable
fun PaperTradingTab(viewModel: FinanceViewModel) {
    val account by viewModel.paperAccount.collectAsState()
    val holdings by viewModel.paperHoldings.collectAsState()
    val stocks by viewModel.simulatedStocks.collectAsState()
    val orderStock by viewModel.showOrderDialogStock.collectAsState()
    val isBuyOrder by viewModel.orderDialogIsBuy.collectAsState()
    val feedbackMessage by viewModel.orderFeedbackMessage.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    val cashBalance = account?.cashBalance ?: 100000.0
    val holdingsValue = holdings.sumOf { holding ->
        val currentMarketPrice = stocks.firstOrNull { it.symbol == holding.symbol }?.currentPrice ?: holding.currentPrice
        holding.quantity * currentMarketPrice
    }
    val totalInvested = holdings.sumOf { it.quantity * it.avgBuyPrice }
    val unrealizedPnl = holdingsValue - totalInvested
    val totalPortfolioValue = cashBalance + holdingsValue

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Virtual Portfolio Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("VIRTUAL PORTFOLIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(WarningBackground)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("SIMULATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WarningColor)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatRupee(totalPortfolioValue), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Simulation", tint = SecondaryText)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Available Cash", fontSize = 11.sp, color = SecondaryText)
                            Text(formatRupee(cashBalance), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryBrand)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Unrealized P&L", fontSize = 11.sp, color = SecondaryText)
                            Text(
                                (if (unrealizedPnl >= 0) "+ " else "") + formatRupee(unrealizedPnl),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (unrealizedPnl >= 0) SuccessColor else ErrorColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "🛡️ Risk-Free Learning: Practise buying and selling without risking real money. Prices are simulated for Indian blue chips.",
                        fontSize = 10.sp,
                        color = SecondaryText,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Live Simulated Market Instruments
        item {
            Text("Simulated Indian Stocks & Benchmark", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)
        }

        items(stocks) { stock ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stock.symbol, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryBackground)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(stock.sector, fontSize = 9.sp, color = SecondaryText)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(stock.companyName, fontSize = 11.sp, color = SecondaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatRupee(stock.currentPrice), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isUp = stock.changePercent >= 0
                            Icon(
                                imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (isUp) SuccessColor else ErrorColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                "${if (isUp) "+" else ""}${"%.2f".format(stock.changePercent)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isUp) SuccessColor else ErrorColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { viewModel.openOrderDialog(stock, isBuy = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("BUY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.openOrderDialog(stock, isBuy = false) },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("SELL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Holdings
        if (holdings.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text("My Virtual Holdings (${holdings.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)
            }
            items(holdings) { holding ->
                val currentPrice = stocks.firstOrNull { it.symbol == holding.symbol }?.currentPrice ?: holding.currentPrice
                val marketVal = holding.quantity * currentPrice
                val costVal = holding.quantity * holding.avgBuyPrice
                val pnl = marketVal - costVal

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(holding.symbol, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                            Text("${holding.quantity} shares @ avg ${formatRupee(holding.avgBuyPrice)}", fontSize = 11.sp, color = SecondaryText)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatRupee(marketVal), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                            Text(
                                (if (pnl >= 0) "+ " else "") + formatRupee(pnl),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pnl >= 0) SuccessColor else ErrorColor
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    if (orderStock != null) {
        OrderExecutionDialog(
            stock = orderStock!!,
            isBuy = isBuyOrder,
            availableCash = cashBalance,
            ownedQuantity = holdings.firstOrNull { it.symbol == orderStock!!.symbol }?.quantity ?: 0,
            feedbackMessage = feedbackMessage,
            onDismiss = { viewModel.dismissOrderDialog() },
            onExecute = { qty ->
                viewModel.executePaperOrder(orderStock!!.symbol, orderStock!!.companyName, isBuyOrder, qty, orderStock!!.currentPrice)
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Simulation?", color = PrimaryText) },
            text = { Text("This will clear all your virtual stock holdings and reset your virtual balance back to ₹1,00,000 cash.", color = SecondaryText) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetPaperTrading()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) {
                    Text("Reset Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel", color = SecondaryText) }
            }
        )
    }
}

@Composable
fun ResultMetric(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, color = SecondaryText)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
    }
}

@Composable
fun OrderExecutionDialog(
    stock: SimulatedStock,
    isBuy: Boolean,
    availableCash: Double,
    ownedQuantity: Int,
    feedbackMessage: String?,
    onDismiss: () -> Unit,
    onExecute: (quantity: Int) -> Unit
) {
    var quantityText by remember { mutableStateOf("5") }
    val quantity = quantityText.toIntOrNull() ?: 0
    val totalCost = quantity * stock.currentPrice

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${if (isBuy) "Buy" else "Sell"} ${stock.symbol}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PrimaryText
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stock.companyName, fontSize = 12.sp, color = SecondaryText)
                Text("Price per share: ${formatRupee(stock.currentPrice)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBrand)

                if (isBuy) {
                    Text("Available Virtual Cash: ${formatRupee(availableCash)}", fontSize = 11.sp, color = PrimaryDark)
                } else {
                    Text("Shares currently owned: $ownedQuantity shares", fontSize = 11.sp, color = PrimaryDark)
                }

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("1", "5", "10", "25", "50").forEach { q ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBackground)
                                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(8.dp))
                                .clickable { quantityText = q }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(q, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Order Value:", fontSize = 13.sp, color = PrimaryText)
                    Text(formatRupee(totalCost), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryBrand)
                }

                if (feedbackMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        feedbackMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (feedbackMessage.contains("Success") || feedbackMessage.contains("Sold")) SuccessColor else ErrorColor
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (quantity > 0) onExecute(quantity) },
                colors = ButtonDefaults.buttonColors(containerColor = if (isBuy) SuccessColor else ErrorColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isBuy) "Execute Buy" else "Execute Sell", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}

