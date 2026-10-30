package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SurfaceBackground
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MainScreen

/**
 * Screen 4: New User Onboarding Screen.
 *
 * Implements:
 * - Collects user name, status (Student / Working Professional), optional institution,
 *   optional pocket money/income, and savings goal.
 * - Allows users to skip optional fields.
 * - Stores profile locally in Room and synchronizes to Supabase PostgreSQL profiles table.
 */
@Composable
fun OnboardingScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val session by viewModel.currentSession.collectAsState()
    val initialName = session?.user?.fullName ?: ""

    var name by remember(initialName) { mutableStateOf(initialName) }
    var userType by remember { mutableStateOf("Student") } // "Student" or "Professional"
    var college by remember { mutableStateOf("") }
    var isHosteller by remember { mutableStateOf(true) }
    var pocketMoneyText by remember { mutableStateOf("5000") }
    var targetSavingsText by remember { mutableStateOf("1000") }
    var selectedGoal by remember { mutableStateOf("Emergency Fund") }
    var language by remember { mutableStateOf("Hinglish") }

    fun finishOnboarding(skipOptional: Boolean = false) {
        val studentName = name.ifBlank {
            session?.user?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Student"
        }
        val institutionName = if (skipOptional) "Campus" else college.ifBlank { "Campus" }
        val pm = if (skipOptional) 5000.0 else (pocketMoneyText.toDoubleOrNull() ?: 5000.0)
        val st = if (skipOptional) 1000.0 else (targetSavingsText.toDoubleOrNull() ?: 1000.0)

        viewModel.completeProfileOnboarding(
            name = studentName,
            college = institutionName,
            isHosteller = isHosteller,
            pocketMoney = pm,
            savingsTarget = st,
            userType = userType,
            language = language
        )
        viewModel.navigateTo(MainScreen.HOME)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Bindora Hero Emblem & Logo
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(24.dp))
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bindora_emblem),
                    contentDescription = "Bindora Logo",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                )
            }
        }

        Text(
            text = "Welcome to Bindora",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = PrimaryText
        )

        // Tagline Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SoftMint)
                .border(width = 1.dp, color = PrimaryBrand.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Every Rupee Has a Purpose.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Personalize your money mentor profile. You can change these anytime in Settings.",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Setup Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Personalize Your Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryText)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    placeholder = { Text("e.g. Gourav Sharma") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input")
                )

                // Status: Student or Working Professional
                Text("I am a:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SecondaryText)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBackground)
                        .border(1.dp, SubtleBorder, RoundedCornerShape(12.dp))
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (userType == "Student") PrimaryBrand else Color.Transparent)
                            .clickable { userType = "Student" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                tint = if (userType == "Student") Color.White else PrimaryText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Student",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userType == "Student") Color.White else PrimaryText
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (userType == "Professional") PrimaryBrand else Color.Transparent)
                            .clickable { userType = "Professional" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.BusinessCenter,
                                contentDescription = null,
                                tint = if (userType == "Professional") Color.White else PrimaryText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Young Pro",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userType == "Professional") Color.White else PrimaryText
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College / Institution (optional)") },
                    placeholder = { Text("e.g. Delhi University / IIT") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (userType == "Student") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Living in Hostel / PG?", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = PrimaryText)
                            Text(if (isHosteller) "Yes, hostel resident" else "Day scholar", fontSize = 11.sp, color = SecondaryText)
                        }
                        Switch(
                            checked = isHosteller,
                            onCheckedChange = { isHosteller = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBrand)
                        )
                    }
                }

                OutlinedTextField(
                    value = pocketMoneyText,
                    onValueChange = { pocketMoneyText = it },
                    label = { Text(if (userType == "Student") "Monthly Allowance (₹ optional)" else "Monthly Income (₹ optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Primary Savings Goal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SecondaryText)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Emergency Fund", "New Laptop", "Books", "Travel").forEach { g ->
                        val isSelected = selectedGoal == g
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryBrand else PrimaryBackground)
                                .border(width = 1.dp, color = if (isSelected) PrimaryBrand else SubtleBorder, shape = RoundedCornerShape(8.dp))
                                .clickable { selectedGoal = g }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(g, fontSize = 11.sp, color = if (isSelected) Color.White else PrimaryText)
                        }
                    }
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { finishOnboarding(skipOptional = false) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("onboarding_get_started_button")
            ) {
                Text("Complete Profile & Start", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = { finishOnboarding(skipOptional = true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip Optional Questions", color = SecondaryText, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
