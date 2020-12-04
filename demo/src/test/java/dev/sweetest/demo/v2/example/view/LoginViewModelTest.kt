package dev.sweetest.demo.v2.example.view

import dev.sweetest.demo.view.LoginViewModel
import dev.sweetest.demo.v2.example.auth.AuthManagerMockSteps
import dev.sweetest.demo.v2.example.net.BackendFakeUser
import dev.sweetest.api.v2.framework.base.JUnit4Test
import org.junit.Test

class LoginViewModelTest : JUnit4Test() {

    private val sut by steps<LoginViewModelSteps>()
    private val authManager by steps<AuthManagerMockSteps>()

    private val user = BackendFakeUser("test@test.com", "supersecure")

    init {
        onSetUp { sut.whenInitialized() }
    }

    @Test
    fun `Login with existing email and correct password`() {
        authManager.givenExistingUser()
        sut {
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(
                LoginViewModel.State.LoggedIn(
                    isNewUser = false
                )
            )
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedIn()
    }

    @Test
    fun `Login with non-existent email`() {
        sut {
            authManager.givenNewUser()
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(
                LoginViewModel.State.LoggedIn(
                    isNewUser = true
                )
            )
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedInAsNewUser()
    }

    @Test
    fun `Login with wrong password`() {
        authManager.givenWrongPassword()
        sut {
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(LoginViewModel.State.Error::class)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsPasswordErrorWrongPassword()
    }
}
