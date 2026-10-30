package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.SurfaceBackground
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MainScreen

@Composable
fun ProfileScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()

    val session by viewModel.currentSession.collectAsState()

    var name by remember(profile) { mutableStateOf(profile?.name ?: "Aman Sharma") }
    var college by remember(profile) { mutableStateOf(profile?.college ?: "Delhi Technological University") }
    var isHosteller by remember(profile) { mutableStateOf(profile?.isHosteller ?: true) }
    var pocketMoneyText by remember(profile) { mutableStateOf(profile?.monthlyPocketMoney?.toInt()?.toString() ?: "5000") }
    var targetSavingsText by remember(profile) { mutableStateOf(profile?.targetMonthlySavings?.toInt()?.toString() ?: "1000") }
    var language by remember(profile) { mutableStateOf(profile?.preferredLanguage ?: "Hinglish") }
    var savedSuccess by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showSignOutConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.handleBack() },
                modifier = Modifier.testTag("profile_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Profile & Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PrimaryText
            )
        }

        // Profile Avatar Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(SoftMint)
                        .border(width = 1.dp, color = PrimaryBrand.copy(alpha = 0.2f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (name.isNotBlank()) name.take(1).uppercase() else "S",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = PrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(name.ifBlank { "Student" }, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryText)
                Text(college.ifBlank { "College Campus" }, fontSize = 12.sp, color = SecondaryText)
            }
        }

        // Settings Form
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Personal & College Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_name")
                )

                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / University") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_college")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Hostel Resident", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = PrimaryText)
                        Text(if (isHosteller) "Living in hostel / PG" else "Day scholar (living with family)", fontSize = 11.sp, color = SecondaryText)
                    }
                    Switch(
                        checked = isHosteller,
                        onCheckedChange = { isHosteller = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBrand)
                    )
                }

                OutlinedTextField(
                    value = pocketMoneyText,
                    onValueChange = { pocketMoneyText = it },
                    label = { Text("Monthly Pocket Money / Allowance (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetSavingsText,
                    onValueChange = { targetSavingsText = it },
                    label = { Text("Target Monthly Savings Goal (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Money Coach Preferred Language", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SecondaryText)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Hinglish", "Hindi", "English").forEach { lang ->
                        val isSelected = language == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBrand else PrimaryBackground)
                                .border(width = 1.dp, color = if (isSelected) PrimaryBrand else SubtleBorder, shape = RoundedCornerShape(10.dp))
                                .clickable { language = lang }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                lang,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else PrimaryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val pm = pocketMoneyText.toDoubleOrNull() ?: 5000.0
                        val st = targetSavingsText.toDoubleOrNull() ?: 1000.0
                        viewModel.updateProfile(
                            name = name.trim(),
                            college = college.trim(),
                            isHosteller = isHosteller,
                            pocketMoney = pm,
                            savingsTarget = st,
                            language = language
                        )
                        savedSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_button")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }

                if (savedSuccess) {
                    Text(
                        "✅ Profile settings saved successfully!",
                        color = SuccessColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // Privacy & Disclaimers
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = PrimaryBrand, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Privacy & Educational Disclaimer", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryText)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Bindora is strictly an educational financial literacy and personal budgeting companion.\n" +
                            "• Tagline: Every Rupee Has a Purpose.\n" +
                            "• It does NOT process actual bank transfers, UPI execution, or hold real funds.\n" +
                            "• The Paper Trading simulator operates on virtual money for risk-free student learning.\n" +
                            "• All transaction records are encrypted and stored locally in your private Room database.\n" +
                            "• Not intended as regulated financial, tax, or legal investment advice.",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = SecondaryText
                )
            }
        }

        // Automatic Tracking & Connected Accounts
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Connected Accounts & Auto Tracking",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage on-device payment monitoring, RBI Account Aggregator consent, and imported bank statements.",
                    fontSize = 11.sp,
                    color = SecondaryText,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(MainScreen.CONNECTED_ACCOUNTS) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_manage_connected_accounts_button")
                    ) {
                        Text("Connected Sources", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(MainScreen.IMPORTED_TRANSACTIONS) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_review_imported_button")
                    ) {
                        Text("Review Imports", fontSize = 12.sp, color = PrimaryDark)
                    }
                }
            }
        }

        // Supabase Account & Security Card
        val userEmail = session?.user?.email ?: profile?.authEmail ?: "student@bindora.app"
        val userProvider = session?.user?.provider ?: profile?.authProvider ?: "email_otp"
        val providerLabel = if (userProvider == "google") "Google Account" else "Passwordless Email OTP"

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Supabase Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryText
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoftMint)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Secured with RLS",
                            color = PrimaryDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = PrimaryBrand,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = userEmail,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryText
                        )
                        Text(
                            text = "Method: $providerLabel",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showSignOutConfirmDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign Out of Bindora", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Data Management & Privacy
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Data Management",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You have full ownership of your data. You can erase all local expenses, budget limits, savings goals, and trade history at any time.",
                    fontSize = 11.sp,
                    color = SecondaryText,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showResetConfirmDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("profile_reset_data_button")
                ) {
                    Text("Clear All Data & Start Fresh", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // About Bindora Brand Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bindora_emblem),
                    contentDescription = "Bindora Emblem",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Bindora",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PrimaryText
                )
                Text(
                    text = "Every Rupee Has a Purpose.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrand
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "AI-powered personal finance and money management designed specifically for Indian college students and young adults.",
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = SecondaryText,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Version 1.2.0 • Production Build",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text("Reset All Data?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
            },
            text = {
                Text(
                    "This action will permanently delete all your recorded expenses, monthly budgets, savings goals, and paper trading holdings from this device. You will be redirected to the onboarding screen.",
                    fontSize = 13.sp,
                    color = SecondaryText,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        viewModel.resetAllUserData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete Everything", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel", color = SecondaryText, fontSize = 12.sp)
                }
            },
            containerColor = SurfaceBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showSignOutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmDialog = false },
            title = {
                Text("Sign Out?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
            },
            text = {
                Text(
                    "Are you sure you want to sign out? Your financial records and goals will remain saved in your Supabase account.",
                    fontSize = 13.sp,
                    color = SecondaryText,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutConfirmDialog = false
                        viewModel.signOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmDialog = false }) {
                    Text("Cancel", color = SecondaryText, fontSize = 12.sp)
                }
            },
            containerColor = SurfaceBackground,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
