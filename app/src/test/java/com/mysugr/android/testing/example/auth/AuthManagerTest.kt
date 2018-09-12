package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.net.BackendGatewaySteps
import com.mysugr.android.testing.example.state.SessionStoreSteps
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.testing.framework.base.*

import org.junit.Test

class AuthManagerTest : BaseJUnitTest(appModuleTestingConfiguration) {
    
    override fun configure() = super.configure()
            .requireReal<AuthManager>()

    private val user by steps<UserSteps>()
    private val sut by steps<AuthManagerSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewaySteps>()
    
    @Test
    fun `Login as existing user`() {
        sut.whenLoggingInOrRegistering()
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenLoggingIn()
            thenCorrectAuthTokenIsSet()
        }
    }

    @Test(expected = AuthManager.WrongPasswordException::class)
    fun `Login as existing user with wrong password`() {
        user.correctPassword = false
        try {
            sut.whenLoggingInOrRegistering()
        } finally {
            sessionStore.thenSessionIsNotStarted()
            backendGateway {
                thenEmailIsChecked()
                thenLoggingIn()
                thenNoAuthTokenIsSet()
            }
        }
    }

    @Test
    fun `Register new user`() {
        user.exists = false
        sut.whenLoggingInOrRegistering()
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenRegistered()
            thenCorrectAuthTokenIsSet()
        }
    }

    @Test
    fun `Logging out`() {
        sut.whenLoggingOut()
        backendGateway.thenAuthTokenIsReset()
        sessionStore.thenSessionEnded()
    }
    
}