package com.example.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.AuthStatus
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
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MainScreen

/**
 * Screen 1: Welcome & Login screen for Bindora.
 *
 * Implements:
 * - Brand identity with official Bindora emblem & logo
 * - "Continue with Google" OAuth button
 * - "Continue with Email" (passwordless 6-digit OTP) button
 * - Privacy Policy & Terms modal viewers
 * - Status indication of Supabase connection
 */
@Composable
fun AuthWelcomeScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authStatus by viewModel.authStatus.collectAsState()
    val authError by viewModel.authErrorMessage.collectAsState()
    val isConfigured = viewModel.supabaseAuthManager.isConfigured

    var showLegalDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Official Bindora Emblem
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(26.dp))
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bindora_emblem),
                        contentDescription = "Bindora Emblem",
                        modifier = Modifier.size(68.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Name & Tagline
            Text(
                text = "Bindora",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Every Rupee Has a Purpose.",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrand
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Take control of your money, one rupee at a time.",
                fontSize = 13.sp,
                color = SecondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Value props
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeaturePill(icon = Icons.Default.Shield, label = "Bank-Grade Safe")
                FeaturePill(icon = Icons.Default.Lock, label = "Zero-Password OTP")
            }

            // Supabase backend status pill
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isConfigured) SoftMint else PrimaryBackground)
                    .border(
                        width = 1.dp,
                        color = if (isConfigured) PrimaryBrand.copy(alpha = 0.3f) else SubtleBorder,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isConfigured) "⚡ Supabase Auth Connected" else "ℹ️ Supabase Ready (Demo Mode Active)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isConfigured) PrimaryDark else SecondaryText
                )
            }

            if (authError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorBackground)
                        .padding(12.dp)
                ) {
                    Text(
                        text = authError!!,
                        color = ErrorColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Method A: Continue with Google
            Button(
                onClick = {
                    viewModel.startGoogleSignIn(context)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceBackground,
                    contentColor = PrimaryText
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
                    .testTag("auth_google_button"),
                enabled = authStatus != AuthStatus.AUTHENTICATING
            ) {
                if (authStatus == AuthStatus.AUTHENTICATING) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryBrand)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Google "G" Badge
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryText
                        )
                    }
                }
            }

            // Method B: Continue with Email (Passwordless 6-digit OTP)
            Button(
                onClick = {
                    viewModel.navigateTo(MainScreen.AUTH_EMAIL_ENTRY)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_email_button"),
                enabled = authStatus != AuthStatus.AUTHENTICATING
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Terms & Privacy links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By continuing, you agree to our ",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
                Text(
                    text = "Terms",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrand,
                    modifier = Modifier.clickable {
                        showLegalDialog = Pair(
                            "Bindora Terms & Conditions",
                            "Welcome to Bindora. Bindora provides smart money management and budgeting tools for Indian college students. All financial records are stored securely with Row Level Security."
                        )
                    }
                )
                Text(
                    text = " and ",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
                Text(
                    text = "Privacy Policy",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrand,
                    modifier = Modifier.clickable {
                        showLegalDialog = Pair(
                            "Bindora Privacy Policy",
                            "Your personal financial data and email are protected with Supabase authentication and Row Level Security. We never sell your data or share transactional SMS logs with external parties."
                        )
                    }
                )
            }
        }
    }

    if (showLegalDialog != null) {
        val (title, content) = showLegalDialog!!
        AlertDialog(
            onDismissRequest = { showLegalDialog = null },
            title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText) },
            text = { Text(content, fontSize = 13.sp, color = SecondaryText, lineHeight = 18.sp) },
            confirmButton = {
                TextButton(onClick = { showLegalDialog = null }) {
                    Text("Close", color = PrimaryBrand, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceBackground)
            .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBrand,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryText
        )
    }
}
