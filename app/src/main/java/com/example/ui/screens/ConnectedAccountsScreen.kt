package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.ingestion.SupportedProvidersCatalog
import com.example.data.model.ConnectedAccountEntity
import com.example.ui.theme.IncomeColor
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.WarningColor
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MainScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedAccountsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.connectedAccounts.collectAsState()
    val pendingReviewCount by viewModel.pendingReviewCount.collectAsState()
    val feedbackMessage by viewModel.importActionFeedback.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedAccountForDetails by remember { mutableStateOf<ConnectedAccountEntity?>(null) }
    var accountToDisconnect by remember { mutableStateOf<ConnectedAccountEntity?>(null) }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearImportFeedback()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Connected Accounts & Payments",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleBack() },
                        modifier = Modifier.testTag("connected_accounts_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.navigateTo(MainScreen.IMPORTED_TRANSACTIONS) },
                        modifier = Modifier.testTag("nav_to_imported_transactions_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Review (${pendingReviewCount})",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBrand,
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftMint),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBrand.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = PrimaryDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Zero-Credential Auto Tracking",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PrimaryDark
                                )
                                Text(
                                    text = "100% On-Device Privacy Guaranteed",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Bindora never asks for banking passwords, OTPs, or UPI PINs. Transactions are captured securely via local notifications and bank statement imports.",
                            fontSize = 13.sp,
                            color = PrimaryDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Quick Actions: Review Pending Transactions & Statement Import
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(MainScreen.IMPORTED_TRANSACTIONS) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_imported_transactions_banner_button")
                    ) {
                        Text(
                            text = if (pendingReviewCount > 0) "Review $pendingReviewCount Pending" else "View All Imported",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.openStatementImportDialog() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_import_statement_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = PrimaryBrand
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Import CSV",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBrand
                            )
                        }
                    }
                }
            }

            // SECTION A: Connected Sources
            item {
                SectionHeader(
                    title = "Connected Sources",
                    subtitle = "Authorized active payment monitoring channels"
                )
            }

            val connectedList = accounts.filter { it.connectionStatus == "Connected" }
            if (connectedList.isEmpty()) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SubtleBorder, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "No active sources connected yet. Select an available source below to start tracking.",
                            fontSize = 13.sp,
                            color = SecondaryText,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(connectedList, key = { it.id }) { account ->
                    ConnectedAccountCard(
                        account = account,
                        onDetailsClick = { selectedAccountForDetails = account },
                        onDisconnectClick = { accountToDisconnect = account },
                        onSyncClick = { viewModel.connectProvider(account.providerId) }
                    )
                }
            }

            // SECTION B: Available Connections & Honest Disclosure
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Available Integrations",
                    subtitle = "Legitimate supported banking & tracking connections"
                )
            }

            val otherAccounts = accounts.filter { it.connectionStatus != "Connected" }
            items(otherAccounts, key = { it.id }) { account ->
                AvailableIntegrationCard(
                    account = account,
                    onConnectClick = {
                        if (!account.isDirectAppNoticeOnly) {
                            viewModel.connectProvider(account.providerId)
                        } else {
                            selectedAccountForDetails = account
                        }
                    },
                    onDetailsClick = { selectedAccountForDetails = account }
                )
            }

            // SECTION C: Interactive Demo Simulator
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Interactive Telemetry Test",
                    subtitle = "Test local notification detection & privacy filters"
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SubtleBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Simulate Incoming Bank / UPI Alerts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Trigger test alerts to see how on-device regex extracts amounts and filters sensitive OTPs:",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.simulateIncomingPayment(
                                        title = "Google Pay",
                                        text = "Paid ₹240.00 to Swiggy via UPI Ref 429182910291 from SBI A/c ...4821"
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("simulate_swiggy_upi_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("GPay: Swiggy ₹240", fontSize = 11.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.simulateIncomingPayment(
                                        title = "SBI Alert",
                                        text = "Sent Rs.45 from SBI A/c ...4821 to Chai Point via UPI Ref 829102849182."
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("simulate_sbi_chai_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("SBI: Chai ₹45", fontSize = 11.sp, maxLines = 1)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.simulateIncomingPayment(
                                        title = "PhonePe",
                                        text = "Paid ₹80 to DMRC Metro Smart Card via UPI Ref 910293847581."
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("simulate_dmrc_metro_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Metro ₹80", fontSize = 11.sp, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.simulateIncomingPayment(
                                        title = "Bank OTP Alert",
                                        text = "492019 is your secret OTP for ICICI Bank login. Never share OTP with anyone."
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("simulate_otp_filter_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Test OTP (Filtered)", fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // SECTION D: Privacy & Zero-Credential Statement
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zero-Credential Security Architecture",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PrimaryDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• We NEVER request banking passwords, NetBanking PINs, or UPI PINs.\n" +
                                    "• Payment parsing executes completely on your Android device in local memory.\n" +
                                    "• One-time passwords (OTPs) and 2FA credentials are strictly scrubbed.\n" +
                                    "• Data is stored locally in your private on-device Room database.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    // Provider Details Dialog
    selectedAccountForDetails?.let { account ->
        AlertDialog(
            onDismissRequest = { selectedAccountForDetails = null },
            title = {
                Text(
                    text = account.providerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Status: ${account.connectionStatus}",
                        fontWeight = FontWeight.SemiBold,
                        color = getStatusColor(account.connectionStatus)
                    )
                    Text(
                        text = "Method: ${account.syncMethod}",
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Permissions: ${account.dataPermissionsGranted}",
                        fontSize = 13.sp
                    )
                    if (account.isDirectAppNoticeOnly) {
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFFDBA74), RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Honest Disclosure: Third-party apps cannot connect directly to Google Pay or PhonePe servers without enterprise banking contracts. Use our On-Device Payment Alert Listener or CSV Statement Import for 100% legal and private tracking.",
                                fontSize = 12.sp,
                                color = Color(0xFF9A3412),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedAccountForDetails = null },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand)
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Disconnect Confirmation Dialog
    accountToDisconnect?.let { account ->
        AlertDialog(
            onDismissRequest = { accountToDisconnect = null },
            title = { Text("Disconnect ${account.providerName}?") },
            text = {
                Text(
                    "Automatic transaction tracking from this provider will be paused. Existing imported transactions will be kept."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.disconnectProvider(account.providerId)
                        accountToDisconnect = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Disconnect")
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToDisconnect = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = SecondaryText
        )
    }
}

@Composable
fun ConnectedAccountCard(
    account: ConnectedAccountEntity,
    onDetailsClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SubtleBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SoftMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getProviderIcon(account.providerType),
                        contentDescription = null,
                        tint = PrimaryDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.providerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = account.accountNumberMasked.ifBlank { account.syncMethod },
                        fontSize = 12.sp,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Status Badge
                StatusBadge(account.connectionStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Last Sync info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last sync: ${formatSyncTime(account.lastSyncMillis)}",
                    fontSize = 11.sp,
                    color = SecondaryText
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = onDetailsClick,
                        modifier = Modifier.testTag("account_details_${account.providerId}")
                    ) {
                        Text("Details", fontSize = 12.sp, color = PrimaryDark)
                    }

                    OutlinedButton(
                        onClick = onDisconnectClick,
                        modifier = Modifier.testTag("account_disconnect_${account.providerId}"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Disconnect", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableIntegrationCard(
    account: ConnectedAccountEntity,
    onConnectClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SubtleBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getProviderIcon(account.providerType),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.providerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (account.isDirectAppNoticeOnly) "Notice Only (No Direct API)" else account.syncMethod,
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                StatusBadge(account.connectionStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = account.dataPermissionsGranted,
                fontSize = 12.sp,
                color = SecondaryText,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDetailsClick) {
                    Text("Info", fontSize = 12.sp, color = PrimaryDark)
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (!account.isDirectAppNoticeOnly) {
                    Button(
                        onClick = onConnectClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("connect_provider_${account.providerId}")
                    ) {
                        Text("Connect", fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onDetailsClick,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Read Notice", fontSize = 12.sp, color = WarningColor)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val bgColor = when (status) {
        "Connected" -> Color(0xFFDCFCE7)
        "Syncing" -> Color(0xFFDBEAFE)
        "Action required" -> Color(0xFFFEF3C7)
        "Connection expired" -> Color(0xFFFEE2E2)
        "Temporarily unavailable" -> Color(0xFFFFEDD5)
        else -> Color(0xFFF3F4F6)
    }
    val textColor = when (status) {
        "Connected" -> Color(0xFF15803D)
        "Syncing" -> Color(0xFF1D4ED8)
        "Action required" -> Color(0xFFB45309)
        "Connection expired" -> Color(0xFFB91C1C)
        "Temporarily unavailable" -> Color(0xFFC2410C)
        else -> Color(0xFF4B5563)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

fun getStatusColor(status: String): Color {
    return when (status) {
        "Connected" -> IncomeColor
        "Syncing" -> Color(0xFF1D4ED8)
        "Action required" -> WarningColor
        "Connection expired" -> Color(0xFFB91C1C)
        else -> Color(0xFF4B5563)
    }
}

fun getProviderIcon(type: String): ImageVector {
    return when (type) {
        "NOTIFICATION_LISTENER" -> Icons.Default.NotificationsActive
        "STATEMENT_IMPORT" -> Icons.Default.Description
        "ACCOUNT_AGGREGATOR" -> Icons.Default.AccountBalance
        else -> Icons.Default.Info
    }
}

fun formatSyncTime(millis: Long?): String {
    if (millis == null) return "Never"
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        else -> SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date(millis))
    }
}
