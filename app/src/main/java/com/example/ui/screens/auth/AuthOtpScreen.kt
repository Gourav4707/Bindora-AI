package com.example.ui.screens.auth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay

/**
 * Screen 3: Dedicated 6-digit OTP verification screen.
 *
 * Implements:
 * - 6 distinct OTP digits with automated focus advancement & paste support
 * - Dynamic 60-second cooldown resend timer
 * - Destination email label and "Change email" option
 * - Clear validation and server error states
 */
@Composable
fun AuthOtpScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val pendingEmail by viewModel.pendingEmail.collectAsState()
    val authStatus by viewModel.authStatus.collectAsState()
    val authError by viewModel.authErrorMessage.collectAsState()

    val targetEmail = pendingEmail ?: "your email"
    val isLoading = authStatus == AuthStatus.AUTHENTICATING

    var otpCode by remember { mutableStateOf("") }
    var cooldownSeconds by remember { mutableIntStateOf(60) }
    val focusRequester = remember { FocusRequester() }

    // 60-second cooldown timer
    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000)
            cooldownSeconds -= 1
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Auto-verify when 6 digits are fully typed or pasted
    LaunchedEffect(otpCode) {
        if (otpCode.length == 6 && !isLoading) {
            viewModel.verifyEmailOtp(targetEmail, otpCode)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.cancelOtpFlow()
                        viewModel.navigateTo(MainScreen.AUTH_EMAIL_ENTRY)
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceBackground)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Change Email",
                        tint = PrimaryDark
                    )
                }

                TextButton(
                    onClick = {
                        viewModel.cancelOtpFlow()
                        viewModel.navigateTo(MainScreen.AUTH_EMAIL_ENTRY)
                    }
                ) {
                    Text("Change Email", color = PrimaryBrand, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lock Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockClock,
                    contentDescription = null,
                    tint = PrimaryBrand,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Verify your email",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We sent a 6-digit verification code to:",
                fontSize = 13.sp,
                color = SecondaryText
            )

            Text(
                text = targetEmail,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Hidden BasicTextField capturing keyboard & paste events
            BasicTextField(
                value = otpCode,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }.take(6)
                    otpCode = filtered
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .size(1.dp)
                    .testTag("auth_otp_hidden_input")
            )

            // 6-Digit Visual Pin Boxes
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { focusRequester.requestFocus() }
                    .testTag("auth_otp_boxes_row")
            ) {
                (0 until 6).forEach { index ->
                    val char = otpCode.getOrNull(index)?.toString() ?: ""
                    val isCurrent = index == otpCode.length
                    val isFilled = char.isNotBlank()

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceBackground)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = when {
                                    isCurrent -> PrimaryBrand
                                    isFilled -> PrimaryDark
                                    else -> SubtleBorder
                                },
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error display
            if (authError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorBackground)
                        .padding(10.dp)
                ) {
                    Text(
                        text = authError!!,
                        color = ErrorColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Resend OTP with cooldown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (cooldownSeconds > 0) {
                    Text(
                        text = "Resend code in ${cooldownSeconds}s",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                cooldownSeconds = 60
                                viewModel.sendEmailOtp(targetEmail)
                            }
                            .testTag("auth_otp_resend_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = PrimaryBrand,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Resend 6-Digit Code",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBrand
                        )
                    }
                }
            }
        }

        // Action Verify Button
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (otpCode.length == 6) {
                        viewModel.verifyEmailOtp(targetEmail, otpCode)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_otp_verify_button"),
                enabled = !isLoading && otpCode.length == 6
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Verifying...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Verify & Continue", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
