package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppTopHeader
import com.example.ui.screens.AddEditGoalDialog
import com.example.ui.screens.AddEditTransactionDialog
import com.example.ui.screens.AddGoalDialog
import com.example.ui.screens.AICoachScreen
import com.example.ui.screens.ConnectedAccountsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImportedTransactionsReviewScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ToolsScreen
import android.content.Intent
import com.example.ui.screens.TransactionsBudgetScreen
import com.example.ui.screens.auth.AuthEmailScreen
import com.example.ui.screens.auth.AuthOtpScreen
import com.example.ui.screens.auth.AuthWelcomeScreen
import com.example.ui.theme.BindoraTheme
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MainScreen

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle incoming OAuth deep link (e.g. bindora://auth/callback)
        intent?.data?.let { uri ->
            viewModel.handleAuthDeepLink(uri)
        }

        setContent {
            BindoraTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle incoming OAuth deep link when activity is already running (singleTask)
        intent.data?.let { uri ->
            viewModel.handleAuthDeepLink(uri)
        }
    }
}

@Composable
fun MainAppContainer(viewModel: FinanceViewModel) {
    val activeScreen by viewModel.activeScreen.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val showTransactionDialog by viewModel.showAddTransactionDialog.collectAsState()
    val transactionToEdit by viewModel.transactionToEdit.collectAsState()
    val showGoalDialog by viewModel.showAddGoalDialog.collectAsState()
    val goalToEdit by viewModel.goalToEdit.collectAsState()

    val isAuthScreen = activeScreen == MainScreen.AUTH_WELCOME ||
            activeScreen == MainScreen.AUTH_EMAIL_ENTRY ||
            activeScreen == MainScreen.AUTH_OTP_VERIFY ||
            activeScreen == MainScreen.ONBOARDING

    val isDetailScreen = activeScreen == MainScreen.PROFILE ||
            activeScreen == MainScreen.CONNECTED_ACCOUNTS ||
            activeScreen == MainScreen.IMPORTED_TRANSACTIONS

    // Android back navigation handling
    BackHandler(enabled = activeScreen != MainScreen.HOME && activeScreen != MainScreen.AUTH_WELCOME) {
        viewModel.handleBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAuthScreen && !isDetailScreen) {
                AppTopHeader(
                    studentName = profile?.name ?: "Student",
                    college = profile?.college ?: "College Campus",
                    isHosteller = profile?.isHosteller ?: true,
                    onProfileClick = { viewModel.navigateTo(MainScreen.PROFILE) }
                )
            }
        },
        bottomBar = {
            if (!isAuthScreen && !isDetailScreen) {
                AppBottomNavigationBar(
                    currentScreen = activeScreen,
                    onScreenSelected = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeScreen) {
                MainScreen.AUTH_WELCOME -> AuthWelcomeScreen(viewModel = viewModel)
                MainScreen.AUTH_EMAIL_ENTRY -> AuthEmailScreen(viewModel = viewModel)
                MainScreen.AUTH_OTP_VERIFY -> AuthOtpScreen(viewModel = viewModel)
                MainScreen.ONBOARDING -> OnboardingScreen(viewModel = viewModel)
                MainScreen.HOME -> HomeScreen(viewModel = viewModel)
                MainScreen.TRANSACTIONS_BUDGET -> TransactionsBudgetScreen(viewModel = viewModel)
                MainScreen.LEARN -> LearnScreen(viewModel = viewModel)
                MainScreen.AI_COACH -> AICoachScreen(viewModel = viewModel)
                MainScreen.TOOLS_SIM -> ToolsScreen(viewModel = viewModel)
                MainScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                MainScreen.CONNECTED_ACCOUNTS -> ConnectedAccountsScreen(viewModel = viewModel)
                MainScreen.IMPORTED_TRANSACTIONS -> ImportedTransactionsReviewScreen(viewModel = viewModel)
            }
        }
    }

    // Global Add / Edit Transaction Modal
    if (showTransactionDialog) {
        AddEditTransactionDialog(
            transactionToEdit = transactionToEdit,
            onDismiss = { viewModel.dismissTransactionDialog() },
            onSave = { title, amount, isIncome, category, notes, paymentMode, editId ->
                viewModel.saveTransaction(title, amount, isIncome, category, notes, paymentMode, editId)
            },
            onDelete = { tx ->
                viewModel.deleteTransaction(tx)
            }
        )
    }

    // Global Add / Edit Savings Goal Modal
    if (showGoalDialog) {
        AddEditGoalDialog(
            goalToEdit = goalToEdit,
            onDismiss = { viewModel.dismissGoalDialog() },
            onSave = { title, targetAmount, category, targetDays, initialDeposit, editId ->
                if (editId != null && editId > 0) {
                    viewModel.updateSavingsGoal(editId, title, targetAmount, category, targetDays)
                } else {
                    viewModel.createSavingsGoal(title, targetAmount, category, targetDays, initialDeposit)
                }
            },
            onDelete = { goal ->
                viewModel.deleteSavingsGoal(goal)
            }
        )
    }
}
