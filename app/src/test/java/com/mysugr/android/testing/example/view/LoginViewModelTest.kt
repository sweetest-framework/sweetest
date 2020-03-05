package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManagerMockSteps
import com.mysugr.android.testing.example.net.FakeBackendUser
import com.mysugr.android.testing.example.view.LoginViewModel.State.Error
import com.mysugr.android.testing.example.view.LoginViewModel.State.LoggedIn
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

class LoginViewModelTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .onSetUp {
            sut.scope = scope
            sut.whenInitialized()
        }

    private val sut by steps<LoginViewModelSteps>()
    private val authManager by steps<AuthManagerMockSteps>()

    private val scope = TestCoroutineScope()
    private val user = FakeBackendUser("test@test.com", "supersecure")

    @Test
    fun `Login with existing email and correct password`() {
        sut {
            whenLoggingIn(user.email, user.password)
            whenWaitForState(LoggedIn::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedIn()
    }

    @Test
    fun `Login with non-existent email`() {
        sut {
            whenLoggingIn(user.email, user.password)
            whenWaitForState(LoggedIn::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedInAsNewUser()
    }

    @Test
    fun `Login with wrong password`() {
        sut {
            whenLoggingIn(user.email, user.password)
            whenWaitForState(Error::class.java)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsPasswordErrorWrongPassword()
    }
}
