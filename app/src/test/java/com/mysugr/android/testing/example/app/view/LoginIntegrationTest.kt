package com.mysugr.android.testing.example.app.view

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.auth.AuthManagerSteps
import com.mysugr.android.testing.example.net.BackendGatewaySteps
import com.mysugr.android.testing.example.state.SessionStoreSteps
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.testing.framework.base.*

import org.junit.Test

class LoginIntegrationTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .requireReal<LoginViewModel>()
            .requireReal<AuthManager>()

    private val user by steps<UserSteps>()
    private val loginViewModel by steps<LoginViewModelSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewaySteps>()

    @Test
    fun `Log in as an existing user`() {
        loginViewModel {
            givenStateListenerConnected()
            whenLoggingIn()
            whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        }
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenLoggingIn()
            thenCorrectAuthTokenIsSet()
        }
    }

    @Test
    fun `Log in as a new user`() {
        user.exists = false
        loginViewModel {
            givenStateListenerConnected()
            whenLoggingIn()
            whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        }
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenRegistered()
            thenCorrectAuthTokenIsSet()
        }
    }

}
