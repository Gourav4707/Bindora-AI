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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.data.model.ImportedTransactionEntity
import com.example.ui.components.formatRupee
import com.example.ui.theme.ExpenseColor
import com.example.ui.theme.IncomeColor
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.WarningColor
import com.example.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportedTransactionsReviewScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val allImported by viewModel.importedTransactions.collectAsState()
    val activeFilter by viewModel.selectedImportFilter.collectAsState()
    val feedbackMessage by viewModel.importActionFeedback.collectAsState()
    val showStatementDialog by viewModel.showStatementImportDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var editingTransaction by remember { mutableStateOf<ImportedTransactionEntity?>(null) }
    var selectedCategoryForEdit by remember { mutableStateOf("") }
    var editedMerchantName by remember { mutableStateOf("") }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearImportFeedback()
        }
    }

    // Filter transactions
    val filteredList = remember(allImported, activeFilter) {
        when (activeFilter) {
            "NEEDS_REVIEW" -> allImported.filter { it.status == "NEEDS_REVIEW" }
            "POSSIBLE_DUPLICATE" -> allImported.filter { it.status == "POSSIBLE_DUPLICATE" }
            "CONFIRMED" -> allImported.filter { it.status == "CONFIRMED" }
            "EXCLUDED" -> allImported.filter { it.status == "EXCLUDED" }
            else -> allImported
        }
    }

    val needsReviewCount = allImported.count { it.status == "NEEDS_REVIEW" }
    val duplicateCount = allImported.count { it.status == "POSSIBLE_DUPLICATE" }
    val confirmedCount = allImported.count { it.status == "CONFIRMED" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Imported Transactions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleBack() },
                        modifier = Modifier.testTag("imported_screen_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.openStatementImportDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("import_statement_csv_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Import CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Metrics Summary Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricChip(
                    label = "Needs Review",
                    count = needsReviewCount,
                    color = PrimaryDark,
                    bgColor = SoftMint,
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = "Duplicates",
                    count = duplicateCount,
                    color = WarningColor,
                    bgColor = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = "Confirmed",
                    count = confirmedCount,
                    color = IncomeColor,
                    bgColor = Color(0xFFDCFCE7),
                    modifier = Modifier.weight(1f)
                )
            }

            // Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    Pair("ALL", "All (${allImported.size})"),
                    Pair("NEEDS_REVIEW", "Needs Review ($needsReviewCount)"),
                    Pair("POSSIBLE_DUPLICATE", "Duplicates ($duplicateCount)"),
                    Pair("CONFIRMED", "Confirmed ($confirmedCount)"),
                    Pair("EXCLUDED", "Excluded")
                )

                items(filters) { (id, label) ->
                    FilterChip(
                        selected = activeFilter == id,
                        onClick = { viewModel.setImportFilter(id) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBrand,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_$id")
                    )
                }
            }

            // Transaction List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SoftMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = PrimaryBrand,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = "No transactions found in this view",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Import a bank statement CSV or trigger a test payment notification to see auto-tracking in action.",
                            fontSize = 13.sp,
                            color = SecondaryText,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { viewModel.openStatementImportDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand)
                        ) {
                            Text("Import Bank Statement")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        ImportedTransactionCard(
                            item = item,
                            onConfirm = {
                                viewModel.confirmImportedTransaction(
                                    item.id,
                                    item.userCategory ?: item.suggestedCategory,
                                    item.cleanMerchant
                                )
                            },
                            onExclude = { viewModel.excludeImportedTransaction(item.id) },
                            onResolveDuplicate = { keepExisting ->
                                viewModel.resolveDuplicate(item.id, keepExisting)
                            },
                            onEditCategory = {
                                editingTransaction = item
                                selectedCategoryForEdit = item.userCategory ?: item.suggestedCategory
                                editedMerchantName = item.cleanMerchant
                            }
                        )
                    }
                }
            }
        }
    }

    // Edit Category & Merchant Dialog
    editingTransaction?.let { tx ->
        EditCategoryDialog(
            transaction = tx,
            currentCategory = selectedCategoryForEdit,
            currentMerchant = editedMerchantName,
            onDismiss = { editingTransaction = null },
            onSave = { newCategory, newMerchant ->
                viewModel.confirmImportedTransaction(tx.id, newCategory, newMerchant)
                editingTransaction = null
            }
        )
    }

    // CSV Statement Import Dialog
    if (showStatementDialog) {
        StatementImportDialog(
            onDismiss = { viewModel.closeStatementImportDialog() },
            onImport = { csvContent, sourceName ->
                viewModel.importCsvStatement(csvContent, sourceName)
            }
        )
    }
}

@Composable
fun MetricChip(
    label: String,
    count: Int,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun ImportedTransactionCard(
    item: ImportedTransactionEntity,
    onConfirm: () -> Unit,
    onExclude: () -> Unit,
    onResolveDuplicate: (Boolean) -> Unit,
    onEditCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDebit = item.direction.equals("DEBIT", ignoreCase = true)
    val amountColor = if (isDebit) ExpenseColor else IncomeColor
    val amountSign = if (isDebit) "-" else "+"

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (item.status == "POSSIBLE_DUPLICATE") 1.5.dp else 1.dp,
                color = if (item.status == "POSSIBLE_DUPLICATE") Color(0xFFF59E0B) else SubtleBorder,
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Source Provider & Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (item.ingestionMethod) {
                                    "NOTIFICATION" -> Color(0xFFEEF2FF)
                                    "CSV_IMPORT" -> Color(0xFFF0FDF4)
                                    else -> Color(0xFFF3F4F6)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.sourceProviderName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (item.ingestionMethod) {
                                "NOTIFICATION" -> Color(0xFF4338CA)
                                "CSV_IMPORT" -> Color(0xFF15803D)
                                else -> Color(0xFF4B5563)
                            }
                        )
                    }

                    if (item.sourceAccountMasked.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.sourceAccountMasked,
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                }

                Text(
                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH).format(Date(item.transactionTimestamp)),
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Merchant, Category, and Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.cleanMerchant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Suggested Category Pill
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { onEditCategory() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category: ${item.userCategory ?: item.suggestedCategory}",
                            fontSize = 12.sp,
                            color = PrimaryDark,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit category",
                            modifier = Modifier.size(12.dp),
                            tint = PrimaryBrand
                        )
                    }
                }

                Text(
                    text = "$amountSign${formatRupee(item.rawAmount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = amountColor
                )
            }

            // Raw Description & Reference (if available)
            if (item.rawDescription.isNotBlank() || item.externalReference.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(8.dp)
                ) {
                    if (item.externalReference.isNotBlank()) {
                        Text(
                            text = "Ref/UTR: ${item.externalReference}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SecondaryText
                        )
                    }
                    Text(
                        text = item.rawDescription,
                        fontSize = 11.sp,
                        color = SecondaryText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Duplicate Alert Box (if duplicate)
            if (item.status == "POSSIBLE_DUPLICATE") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Possible Duplicate Detected",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = WarningColor
                            )
                        }
                        Text(
                            text = item.duplicateReason.ifBlank { "Matches an existing transaction recorded nearby." },
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onResolveDuplicate(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = WarningColor),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("resolve_duplicate_ignore_${item.id}"),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("Ignore Duplicate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onResolveDuplicate(false) },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("resolve_duplicate_keep_${item.id}"),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("Keep Both", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Action Buttons
            Spacer(modifier = Modifier.height(12.dp))
            when (item.status) {
                "NEEDS_REVIEW" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("confirm_imported_tx_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirm to Budget", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onExclude,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(0.7f)
                                .testTag("exclude_imported_tx_${item.id}")
                        ) {
                            Text("Exclude", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                "CONFIRMED" -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = IncomeColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reconciled with Budget",
                            color = IncomeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                "EXCLUDED" -> {
                    Text(
                        text = "Excluded from budget",
                        color = SecondaryText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EditCategoryDialog(
    transaction: ImportedTransactionEntity,
    currentCategory: String,
    currentMerchant: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(currentCategory) }
    var merchantName by remember { mutableStateOf(currentMerchant) }
    var isDropdownOpen by remember { mutableStateOf(false) }

    val allCategories = FinanceConstants.EXPENSE_CATEGORIES.map { it.name } +
            listOf("Pocket Money / Allowance", "Internship / Freelance", "Other Income", "Other Miscellaneous")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Merchant & Category", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = merchantName,
                    onValueChange = { merchantName = it },
                    label = { Text("Merchant / Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { isDropdownOpen = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Category: $selectedCategory", fontSize = 13.sp)
                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    DropdownMenu(
                        expanded = isDropdownOpen,
                        onDismissRequest = { isDropdownOpen = false }
                    ) {
                        allCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category, fontSize = 13.sp) },
                                onClick = {
                                    selectedCategory = category
                                    isDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Surface(
                    color = SoftMint,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 Bindora will remember '$merchantName' as '$selectedCategory' for all future automatic imports.",
                        fontSize = 11.sp,
                        color = PrimaryDark,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedCategory, merchantName) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand)
            ) {
                Text("Save & Add to Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StatementImportDialog(
    onDismiss: () -> Unit,
    onImport: (String, String) -> Unit
) {
    var csvText by remember { mutableStateOf("") }
    var sourceName by remember { mutableStateOf("SBI Bank Statement") }

    val sampleSbiCsv = """
Date,Narration,Chq/Ref No,Debit,Credit,Balance
29/10/2026,UPI/429182910291/DR/SWIGGY/ICICI,429182910291,240.00,,4760.00
29/10/2026,UPI/829102849182/DR/CHAI POINT/SBI,829102849182,45.00,,4715.00
28/10/2026,UPI/910293847581/DR/DMRC METRO/PAYTM,910293847581,80.00,,4635.00
27/10/2026,UPI/518291029384/DR/BLINKIT GROCERY,518291029384,185.00,,4450.00
25/10/2026,ALLOWANCE TRANSFER PAPA,48291823901,,5000.00,9450.00
""".trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = PrimaryBrand,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Import Bank Statement (CSV)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Upload or paste statement contents exported from SBI, HDFC, ICICI, Axis, or UPI apps. Columns supported: Date, Narration, Ref/UTR, Debit, Credit.",
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                OutlinedTextField(
                    value = sourceName,
                    onValueChange = { sourceName = it },
                    label = { Text("Account / Source Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = csvText,
                    onValueChange = { csvText = it },
                    label = { Text("CSV Statement Content") },
                    placeholder = { Text("Paste CSV rows here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                // Quick Load Sample Button
                OutlinedButton(
                    onClick = {
                        csvText = sampleSbiCsv
                        sourceName = "SBI Student Account (Sample)"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("load_sample_csv_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Load Sample Indian Bank CSV", fontSize = 12.sp, color = PrimaryDark)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (csvText.isNotBlank()) {
                        onImport(csvText, sourceName)
                    }
                },
                enabled = csvText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                modifier = Modifier.testTag("submit_csv_import_button")
            ) {
                Text("Parse & Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
