package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManagerMockSteps
import com.mysugr.android.testing.example.net.FakeBackendUser
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import kotlin.coroutines.ContinuationInterceptor

class LoginViewModelTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .offerMockRequired { scope.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher }
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
            thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = false))
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedIn()
    }

    @Test
    fun `Login with non-existent email`() {
        sut {
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = true))
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsLoggedInAsNewUser()
    }

    @Test
    fun `Login with wrong password`() {
        sut {
            whenLoggingIn(user.email, user.password)
            thenLastStateIs(LoginViewModel.State.Error::class)
        }
        authManager.thenLoginOrRegisterIsCalled()
        sut.thenStateIsPasswordErrorWrongPassword()
    }
}
