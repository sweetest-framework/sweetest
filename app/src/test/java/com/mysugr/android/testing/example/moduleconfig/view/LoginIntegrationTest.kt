package com.mysugr.android.testing.example.moduleconfig.view

import com.mysugr.android.testing.example.moduleconfig.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.moduleconfig.net.BackendGatewaySteps
import com.mysugr.android.testing.example.moduleconfig.state.SessionStoreSteps
import com.mysugr.android.testing.example.moduleconfig.auth.UserSteps
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.sweetest.framework.base.*

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
