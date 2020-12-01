package com.mysugr.android.testing.v1.example.view

import com.mysugr.android.testing.v1.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.v1.example.feature.auth.UserSteps
import com.mysugr.android.testing.v1.example.net.BackendGatewaySteps
import com.mysugr.android.testing.v1.example.state.SessionStoreSteps
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
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
