package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SurfaceBackground
import com.example.ui.theme.SuccessColor

@Composable
fun AddEditTransactionDialog(
    transactionToEdit: TransactionEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, isIncome: Boolean, category: String, notes: String, paymentMode: String, editId: Long?) -> Unit,
    onDelete: ((TransactionEntity) -> Unit)? = null
) {
    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var amountText by remember { mutableStateOf(transactionToEdit?.amount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var isIncome by remember { mutableStateOf(transactionToEdit?.isIncome ?: false) }
    var category by remember { mutableStateOf(transactionToEdit?.category ?: if (isIncome) "Pocket Money" else "Food & Canteen") }
    var paymentMode by remember { mutableStateOf(transactionToEdit?.paymentMode ?: "UPI") }
    var notes by remember { mutableStateOf(transactionToEdit?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val categories = if (isIncome) FinanceConstants.INCOME_CATEGORIES else FinanceConstants.EXPENSE_CATEGORIES

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (transactionToEdit == null) "Record Transaction" else "Edit Transaction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryText
                )
                if (transactionToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(transactionToEdit)
                            onDismiss()
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorColor)
                    }
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Type Selector (Expense vs Income)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBackground)
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isIncome) ErrorColor else Color.Transparent)
                            .clickable {
                                isIncome = false
                                category = "Food & Canteen"
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Expense",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (!isIncome) Color.White else SecondaryText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isIncome) SuccessColor else Color.Transparent)
                            .clickable {
                                isIncome = true
                                category = "Pocket Money"
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Allowance / Income",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isIncome) Color.White else SecondaryText
                        )
                    }
                }

                // Prominent Hero Amount Input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryBackground)
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "₹",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isIncome) SuccessColor else PrimaryBrand
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            placeholder = { Text("0", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SecondaryText.copy(alpha = 0.5f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dialog_transaction_amount_input")
                        )
                    }
                }

                // Title field with smart category suggestion
                OutlinedTextField(
                    value = title,
                    onValueChange = { newTitle ->
                        title = newTitle
                        if (newTitle.length >= 3) {
                            val suggested = FinanceConstants.suggestCategory(newTitle, isIncome)
                            category = suggested
                        }
                    },
                    label = { Text("Title (e.g. Canteen chai, Metro pass)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_transaction_title_input")
                )

                // Quick Amount Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val quicks = if (isIncome) listOf("2000", "4000", "5000", "8000") else listOf("30", "60", "150", "300")
                    quicks.forEach { q ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBackground)
                                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(8.dp))
                                .clickable { amountText = q }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("₹$q", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryText)
                        }
                    }
                }

                // Category selector
                Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        val isSelected = category.equals(cat.name, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) cat.color else SurfaceBackground)
                                .border(width = 1.dp, color = if (isSelected) cat.color else SubtleBorder, shape = RoundedCornerShape(10.dp))
                                .clickable { category = cat.name }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else cat.color,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else PrimaryText
                                )
                            }
                        }
                    }
                }

                // Payment Mode selector
                Text("Payment Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("UPI", "Cash", "Card").forEach { mode ->
                        val isSelected = paymentMode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryBrand else SurfaceBackground)
                                .border(width = 1.dp, color = if (isSelected) PrimaryBrand else SubtleBorder, shape = RoundedCornerShape(8.dp))
                                .clickable { paymentMode = mode }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                mode,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else PrimaryText
                            )
                        }
                    }
                }

                // Optional Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ErrorBackground)
                            .padding(8.dp)
                    ) {
                        Text(errorMessage!!, color = ErrorColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isSubmitting) return@Button
                    val parsedAmount = amountText.toDoubleOrNull()
                    if (title.isBlank()) {
                        errorMessage = "Please enter a transaction title"
                        return@Button
                    }
                    if (parsedAmount == null || parsedAmount <= 0.0) {
                        errorMessage = "Please enter a valid amount greater than 0"
                        return@Button
                    }
                    isSubmitting = true
                    onSave(
                        title.trim(),
                        parsedAmount,
                        isIncome,
                        category,
                        notes.trim(),
                        paymentMode,
                        transactionToEdit?.id
                    )
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_save_transaction_button")
            ) {
                Text(if (transactionToEdit == null) "Save Expense" else "Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}

@Composable
fun AddEditGoalDialog(
    goalToEdit: SavingsGoalEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, targetAmount: Double, category: String, targetDays: Int, initialDeposit: Double, editId: Long?) -> Unit,
    onDelete: ((SavingsGoalEntity) -> Unit)? = null
) {
    val initialDays = remember(goalToEdit) {
        if (goalToEdit != null) {
            val diffMillis = goalToEdit.targetDateMillis - System.currentTimeMillis()
            (diffMillis / (24L * 60 * 60 * 1000)).coerceAtLeast(1).toInt().toString()
        } else {
            "90"
        }
    }

    var title by remember { mutableStateOf(goalToEdit?.title ?: "") }
    var amountText by remember { mutableStateOf(goalToEdit?.targetAmount?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var initialDepositText by remember { mutableStateOf("") }
    var daysText by remember { mutableStateOf(initialDays) }
    var category by remember { mutableStateOf(goalToEdit?.category ?: "Gadgets") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val studentPresets = listOf(
        Triple("Semester Fees", 3000.0, "Fees"),
        Triple("Coding Laptop", 45000.0, "Gadgets"),
        Triple("Hostel Buffer", 5000.0, "Emergency"),
        Triple("College Trip", 8000.0, "Travel"),
        Triple("Online Course", 2500.0, "Books")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (goalToEdit == null) "Create Savings Goal" else "Edit Savings Goal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryText
                )
                if (goalToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(goalToEdit)
                            onDismiss()
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Goal", tint = ErrorColor)
                    }
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Track intentional savings for your college journey with Bindora.",
                    fontSize = 12.sp,
                    color = SecondaryText
                )

                // Presets (only when creating new goal)
                if (goalToEdit == null) {
                    Text(
                        text = "Quick Student Presets:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(studentPresets) { (pTitle, pAmount, pCat) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftMint)
                                    .clickable {
                                        title = pTitle
                                        amountText = pAmount.toInt().toString()
                                        category = pCat
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "$pTitle (₹${pAmount.toInt()})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryDark
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title (e.g. Coding Laptop, Goa Trip)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_goal_title_input")
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Target Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_goal_amount_input")
                )

                if (goalToEdit == null) {
                    OutlinedTextField(
                        value = initialDepositText,
                        onValueChange = { initialDepositText = it },
                        label = { Text("Initial Deposit (optional ₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it },
                    label = { Text("Target Timeline in Days (e.g. 45, 90, 180)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Gadgets", "Emergency", "Travel", "Fees", "Books").forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryBrand else PrimaryBackground)
                                .border(width = 1.dp, color = if (isSelected) PrimaryBrand else SubtleBorder, shape = RoundedCornerShape(8.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(cat, fontSize = 11.sp, color = if (isSelected) Color.White else PrimaryText)
                        }
                    }
                }

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ErrorBackground)
                            .padding(8.dp)
                    ) {
                        Text(errorMessage!!, color = ErrorColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    val days = daysText.toIntOrNull() ?: 90
                    val deposit = initialDepositText.toDoubleOrNull() ?: 0.0

                    if (title.isBlank()) {
                        errorMessage = "Please enter a goal title"
                        return@Button
                    }
                    if (amt == null || amt <= 0.0) {
                        errorMessage = "Please enter a valid target amount greater than 0"
                        return@Button
                    }
                    if (days <= 0) {
                        errorMessage = "Target timeline must be at least 1 day"
                        return@Button
                    }

                    onSave(title.trim(), amt, category, days, deposit, goalToEdit?.id)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_confirm_goal_button")
            ) {
                Text(if (goalToEdit == null) "Create Goal" else "Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}

// Backwards compatibility alias
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, targetAmount: Double, category: String, targetDays: Int) -> Unit
) {
    AddEditGoalDialog(
        goalToEdit = null,
        onDismiss = onDismiss,
        onSave = { title, targetAmount, category, targetDays, _, _ ->
            onSave(title, targetAmount, category, targetDays)
        }
    )
}
