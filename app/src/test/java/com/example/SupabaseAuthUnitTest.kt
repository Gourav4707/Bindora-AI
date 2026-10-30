package com.example

import com.example.data.auth.AuthResult
import com.example.data.auth.AuthStatus
import com.example.data.auth.SupabaseAuthManager
import com.example.data.auth.SupabaseSession
import com.example.data.auth.SupabaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseAuthUnitTest {

    @Test
    fun testEmailPatternValidation() {
        val pattern = SupabaseAuthManager.EMAIL_PATTERN

        // Valid emails
        assertTrue(pattern.matcher("student@college.edu").matches())
        assertTrue(pattern.matcher("aman.sharma@dtu.ac.in").matches())
        assertTrue(pattern.matcher("user+tag@gmail.com").matches())
        assertTrue(pattern.matcher("contact@bindora.app").matches())

        // Invalid emails
        assertFalse(pattern.matcher("invalid-email").matches())
        assertFalse(pattern.matcher("@missingusername.com").matches())
        assertFalse(pattern.matcher("user@.com").matches())
        assertFalse(pattern.matcher("").matches())
        assertFalse(pattern.matcher("user space@domain.com").matches())
    }

    @Test
    fun testOtpValidationLogic() {
        val validToken = "123456"
        assertTrue(validToken.length == 6 && validToken.all { it.isDigit() })

        val tooShortToken = "12345"
        assertFalse(tooShortToken.length == 6 && tooShortToken.all { it.isDigit() })

        val nonDigitToken = "12345A"
        assertFalse(nonDigitToken.length == 6 && nonDigitToken.all { it.isDigit() })

        val emptyToken = ""
        assertFalse(emptyToken.length == 6 && emptyToken.all { it.isDigit() })
    }

    @Test
    fun testOAuthFragmentParser() {
        val fragment = "access_token=fake_supabase_jwt_123&refresh_token=fake_refresh_456&expires_in=3600&token_type=bearer"
        val params = mutableMapOf<String, String>()
        fragment.split("&").forEach { pair ->
            val parts = pair.split("=", limit = 2)
            if (parts.size == 2) {
                params[parts[0]] = parts[1]
            }
        }

        assertEquals("fake_supabase_jwt_123", params["access_token"])
        assertEquals("fake_refresh_456", params["refresh_token"])
        assertEquals("3600", params["expires_in"])
        assertEquals("bearer", params["token_type"])
    }

    @Test
    fun testSupabaseSessionModel() {
        val user = SupabaseUser(
            id = "usr_1001",
            email = "student@bindora.app",
            fullName = "Aman Sharma",
            avatarUrl = "",
            provider = "google"
        )
        val now = System.currentTimeMillis()
        val session = SupabaseSession(
            accessToken = "sample_access_token",
            refreshToken = "sample_refresh_token",
            expiresInSeconds = 3600,
            expiresAtMillis = now + 3600000,
            user = user
        )

        assertEquals("usr_1001", session.user.id)
        assertEquals("student@bindora.app", session.user.email)
        assertEquals("google", session.user.provider)
        assertFalse(session.isExpired)
    }

    @Test
    fun testAuthResultTypes() {
        val success = AuthResult.Success(
            session = SupabaseSession(
                accessToken = "tok",
                refreshToken = "ref",
                expiresInSeconds = 3600,
                user = SupabaseUser(id = "1", email = "test@bindora.app")
            ),
            isOnboardingRequired = true
        )
        assertTrue(success.isOnboardingRequired)
        assertEquals("test@bindora.app", success.session.user.email)

        val otpSent = AuthResult.OtpSent(
            email = "test@bindora.app",
            message = "OTP sent successfully"
        )
        assertEquals("test@bindora.app", otpSent.email)

        val error = AuthResult.Error(
            message = "Rate limit exceeded. Please wait 60 seconds.",
            code = "429"
        )
        assertEquals("429", error.code)
    }
}
