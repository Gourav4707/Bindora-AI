package com.example.data.auth

data class SupabaseUser(
    val id: String,
    val email: String,
    val fullName: String = "",
    val avatarUrl: String = "",
    val provider: String = "email_otp"
)

data class SupabaseSession(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
    val expiresAtMillis: Long = System.currentTimeMillis() + (expiresInSeconds * 1000),
    val tokenType: String = "bearer",
    val user: SupabaseUser
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() >= (expiresAtMillis - 60_000) // 1 min buffer
}

enum class AuthStatus {
    INITIALIZING,
    SIGNED_OUT,
    AUTHENTICATING,
    OTP_SENT,
    ONBOARDING_REQUIRED,
    AUTHENTICATED,
    ERROR
}

sealed class AuthResult {
    data class Success(val session: SupabaseSession, val isOnboardingRequired: Boolean) : AuthResult()
    data class OtpSent(val email: String, val message: String) : AuthResult()
    data class Error(val message: String, val code: String? = null) : AuthResult()
}
