package com.mysugr.android.testing.example.nomoduleconfig.auth

import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.nomoduleconfig.net.BackendGatewaySteps
import com.mysugr.android.testing.example.nomoduleconfig.state.SessionStoreSteps
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import org.junit.Test

class AuthManagerTest : BaseJUnitTest() {

    override fun configure() = super.configure()
        .requireReal<AuthManager>()

    private val user by steps<UserSteps>()
    private val sut by steps<AuthManagerSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewaySteps>()

    @Test
    fun `Login as existing user`() {
        sut.whenLoggingInOrRegistering()
        backendGateway {
            thenEmailIsChecked()
            thenLoginAttempted()
        }
        sessionStore.thenSessionIsStarted()
    }

    @Test(expected = AuthManager.WrongPasswordException::class)
    fun `Login as existing user with wrong password`() {
        user.givenEnteredPasswordIsIncorrect()
        try {
            sut.whenLoggingInOrRegistering()
        } finally {
            backendGateway {
                thenEmailIsChecked()
                thenLoginAttempted()
            }
            sessionStore.thenSessionIsNotStarted()
        }
    }

    @Test
    fun `Register new user`() {
        user.givenRequestedUserDoesntExist()
        sut.whenLoggingInOrRegistering()
        backendGateway {
            thenEmailIsChecked()
            thenRegistered()
        }
        sessionStore.thenSessionIsStarted()
    }

    @Test
    fun `Logging out`() {
        sut.whenLoggingOut()
        sessionStore.thenSessionIsEnded()
    }
}