package com.example.ui.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthStatus
import com.example.data.auth.SupabaseAuthManager
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SurfaceBackground
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.MainScreen

/**
 * Screen 2: Email entry for passwordless Supabase Auth OTP verification.
 */
@Composable
fun AuthEmailScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    var emailText by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    val authStatus by viewModel.authStatus.collectAsState()
    val authError by viewModel.authErrorMessage.collectAsState()

    val isLoading = authStatus == AuthStatus.AUTHENTICATING

    fun submitEmail() {
        val trimmed = emailText.trim()
        if (trimmed.isBlank()) {
            localError = "Please enter your email address."
            return
        }
        if (!SupabaseAuthManager.EMAIL_PATTERN.matcher(trimmed).matches()) {
            localError = "Please enter a valid email format (e.g. name@campus.edu)."
            return
        }
        localError = null
        viewModel.sendEmailOtp(trimmed)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))

            // Back button
            IconButton(
                onClick = {
                    viewModel.navigateTo(MainScreen.AUTH_WELCOME)
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceBackground)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Header Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = PrimaryBrand,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Continue with Email",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We will send a 6-digit verification code to your email. No passwords needed.",
                fontSize = 13.sp,
                color = SecondaryText,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email Input
            OutlinedTextField(
                value = emailText,
                onValueChange = {
                    emailText = it
                    if (localError != null) localError = null
                },
                label = { Text("College or Personal Email") },
                placeholder = { Text("you@example.com") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBrand)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submitEmail() }
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                enabled = !isLoading
            )

            val displayError = localError ?: authError
            if (displayError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorBackground)
                        .padding(10.dp)
                ) {
                    Text(
                        text = displayError,
                        color = ErrorColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Action Button
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { submitEmail() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_continue_email_button"),
                enabled = !isLoading && emailText.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Sending Code...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Send Verification Code", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
