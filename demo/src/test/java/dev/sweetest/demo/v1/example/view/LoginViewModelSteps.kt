package dev.sweetest.demo.v1.example.view

import dev.sweetest.demo.R
import dev.sweetest.demo.view.LoginViewModel
import dev.sweetest.demo.v1.example.coroutine.CoroutinesSteps
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import dev.sweetest.internal.TestContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import kotlin.reflect.KClass

class LoginViewModelSteps(testContext: TestContext) : BaseSteps(testContext) {

    override fun configure() = super.configure()
        .requireSteps<CoroutinesSteps>()
        .provide<LoginViewModel>()

    private val testCoroutineScope by dependency<TestCoroutineScope>()
    private val instance by dependency<LoginViewModel>()
    private val trackedStates = mutableListOf<LoginViewModel.State>()

    fun whenInitialized() {
        instance.state
            .onEach { trackedStates.add(it) }
            .launchIn(testCoroutineScope)
    }

    fun whenLoggingIn(email: String, password: String) {
        instance.loginOrRegister(email, password)
    }

    fun thenLastStateIs(expected: LoginViewModel.State) {
        val actual = trackedStates.lastOrNull()
        assertEquals(expected, actual)
    }

    fun thenLastStateIs(expectedType: KClass<*>) {
        val actual = trackedStates.lastOrNull()
        checkNotNull(actual)
        assertEquals(expectedType, actual::class)
    }

    fun thenStateChangeWasNotified() {
        assertTrue(trackedStates.size > 0)
    }

    fun thenStateIsLoggedIn() {
        thenStateChangeWasNotified()
        assertTrue(trackedStates.last() is LoginViewModel.State.LoggedIn)
    }

    fun thenStateIsNotLoggedIn() {
        thenStateChangeWasNotified()
        assertTrue(trackedStates.last() !is LoginViewModel.State.LoggedIn)
    }

    fun thenStateIsLoggedInAsNewUser() {
        thenStateChangeWasNotified()
        val last = trackedStates.last() as? LoginViewModel.State.LoggedIn
        assertNotNull(last)
        assertTrue(last!!.isNewUser)
    }

    fun thenStateIsLoggedInAsExistingUser() {
        thenStateChangeWasNotified()
        val last = trackedStates.last() as? LoginViewModel.State.LoggedIn
        assertNotNull(last)
        assertTrue(!last!!.isNewUser)
    }

    fun thenStateIsPasswordErrorWrongPassword() {
        thenStateChangeWasNotified()
        (trackedStates.last() as? LoginViewModel.State.Error)
            ?.also { assertTrue(it.passwordError == R.string.error_incorrect_password) }
            ?: fail("The last state is not of type Error")
    }
}
