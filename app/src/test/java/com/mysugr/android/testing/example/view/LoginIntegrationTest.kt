package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.android.testing.example.net.BackendGatewaySteps
import com.mysugr.android.testing.example.state.SessionStoreSteps
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.sweetest.framework.base.*
import kotlinx.coroutines.test.TestCoroutineScope

import org.junit.Test

class LoginIntegrationTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireReal<AuthManager>()
        .onSetUp {
            loginViewModel.scope = scope
        }

    private val user by steps<UserSteps>()
    private val loginViewModel by steps<LoginViewModelSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewaySteps>()

    private val scope = TestCoroutineScope()

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
            thenLoginAttempted()
        }
    }

    @Test
    fun `Log in as a new user`() {
        user.givenRequestedUserDoesntExist()
        loginViewModel {
            givenStateListenerConnected()
            whenLoggingIn()
            whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        }
        sessionStore.thenSessionIsStarted()
        backendGateway {
            thenEmailIsChecked()
            thenRegistered()
        }
    }
}
