package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManagerMockSteps
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.android.testing.example.view.LoginViewModel.State.Error
import com.mysugr.android.testing.example.view.LoginViewModel.State.LoggedIn
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import org.junit.Test

class LoginViewModelTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .onSetUp { sut.givenStateListenerConnected() }

    private val sut by steps<LoginViewModelSteps>()
    private val authManager by steps<AuthManagerMockSteps>()
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
        user.givenRequestedUserDoesntExist()
        sut {
            whenLoggingIn()
            whenWaitForState(LoggedIn::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedInAsNewUser()
    }

    @Test
    fun `Login with wrong password`() {
        user.givenEnteredPasswordIsIncorrect()
        sut {
            whenLoggingIn()
            whenWaitForState(Error::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsPasswordErrorWrongPassword()
    }
}
