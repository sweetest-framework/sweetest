package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.BackendGatewayMockSteps
import com.mysugr.android.testing.example.net.FakeBackendUser
import com.mysugr.android.testing.example.state.SessionStoreSteps
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

class LoginIntegrationTest : BaseJUnitTest(appModuleTestingConfiguration) {

    private val loginViewModel by steps<LoginViewModelSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewayMockSteps>()
    private val scope = TestCoroutineScope()

    private val existingUser = FakeBackendUser("existing@test.com", "supersecure1")
    private var newUser = FakeBackendUser("new@test.com", "supersecure2")

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireReal<AuthManager>()
        .onSetUp {
            loginViewModel.scope = scope
            backendGateway.givenUsers(existingUser)
        }

    @Test
    fun `Log in as an existing user`() {
        loginViewModel {
            whenInitialized()
            whenLoggingIn(existingUser.email, existingUser.password)
            thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = false))
        }
        sessionStore.thenSessionIsStarted(existingUser.email, existingUser.authToken)
        backendGateway {
            thenEmailIsChecked(existingUser.email)
            thenLoginAttempted(existingUser.email, existingUser.password)
        }
    }

    @Test
    fun `Log in as a new user`() {
        loginViewModel {
            whenInitialized()
            whenLoggingIn(newUser.email, newUser.password)
            thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = true))
            newUser = backendGateway.getUser(newUser.email)
        }
        sessionStore.thenSessionIsStarted(newUser.email, newUser.authToken)
        backendGateway {
            thenEmailIsChecked(newUser.email)
            thenRegistered(newUser.email, newUser.password)
        }
    }
}
