package com.mysugr.android.testing.example.app.view

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManagerSteps
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.sweetest.framework.base.*
import org.junit.Test

import com.mysugr.android.testing.example.app.view.LoginViewModel.State.*

class LoginViewModelTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .requireReal<LoginViewModel>()
            .onSetUp { sut.givenStateListenerConnected() }

    private val sut by steps<LoginViewModelSteps>()
    private val authManager by steps<AuthManagerSteps>()
    private val user by steps<UserSteps>()

    @Test
    fun `Login with existing email and correct password`() {
        sut {
            whenLoggingIn()
            whenWaitForState(LoggedIn::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedIn()
    }

    @Test
    fun `Login with non-existent email`() {
        user.exists = false
        sut {
            whenLoggingIn()
            whenWaitForState(LoggedIn::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedInWithNewUser()
    }

    @Test
    fun `Login with wrong password`() {
        user.correctPassword = false
        sut {
            whenLoggingIn()
            whenWaitForState(Error::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsPasswordErrorWrongPassword()
    }

}
