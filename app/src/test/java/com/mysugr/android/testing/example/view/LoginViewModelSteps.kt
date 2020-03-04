package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.app.R
import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail

class LoginViewModelSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    lateinit var scope: TestCoroutineScope

    private val instance by dependency<LoginViewModel>()
    private val user by steps<UserSteps>()

    private val recordedStateChanges = mutableListOf<LoginViewModel.State>()
    private val stateChangeSync = Object()

    fun givenStateListenerConnected() {
        instance.state
            .onEach {
                recordedStateChanges.add(it)
                synchronized(stateChangeSync) {
                    stateChangeSync.notify()
                }
            }
            .launchIn(scope)
    }

    fun whenLoggingIn() {
        instance.loginOrRegister(user.email, user.password)
    }

    fun <R> whenWaitForState(ofType: Class<R>) = whenWaitForState(ofType, false)
    fun <R> whenWaitForStateNot(ofType: Class<R>) = whenWaitForState(ofType, true)

    private fun <R> whenWaitForState(ofType: Class<R>, invert: Boolean): R {

        var resultState: R? = null
        val startedAtMillis = System.currentTimeMillis()
        val maxTimeMillis = 1000L

        fun checkLastState() =
            if (recordedStateChanges.size == 0) {
                false
            } else {
                val last = recordedStateChanges.last()
                val matchesType = if (invert) {
                    last::class.java != ofType
                } else {
                    last::class.java == ofType
                }
                if (matchesType) {
                    @Suppress("UNCHECKED_CAST")
                    resultState = last as R
                }
                matchesType
            }

        while (!checkLastState()) {
            val timeToGo = maxTimeMillis - (System.currentTimeMillis() - startedAtMillis)
            if (timeToGo > 0L) {
                synchronized(stateChangeSync) {
                    stateChangeSync.wait(timeToGo)
                }
            } else {
                fail("Timed out waiting for state of type $ofType")
            }
        }

        return resultState!!
    }

    fun thenStateChangeWasNotified() {
        assertTrue(recordedStateChanges.size > 0)
    }

    fun thenStateIsLoggedIn() {
        thenStateChangeWasNotified()
        assertTrue(recordedStateChanges.last() is LoginViewModel.State.LoggedIn)
    }

    fun thenStateIsNotLoggedIn() {
        thenStateChangeWasNotified()
        assertTrue(recordedStateChanges.last() !is LoginViewModel.State.LoggedIn)
    }

    fun thenStateIsLoggedInAsNewUser() {
        thenStateChangeWasNotified()
        val last = recordedStateChanges.last() as? LoginViewModel.State.LoggedIn
        assertNotNull(last)
        assertTrue(last!!.isNewUser)
    }

    fun thenStateIsLoggedInAsExistingUser() {
        thenStateChangeWasNotified()
        val last = recordedStateChanges.last() as? LoginViewModel.State.LoggedIn
        assertNotNull(last)
        assertTrue(!last!!.isNewUser)
    }

    fun thenStateIsError() {
        thenStateChangeWasNotified()
        assertTrue(recordedStateChanges.last() is LoginViewModel.State.Error)
    }

    fun thenStateIsPasswordErrorWrongPassword() {
        thenStateChangeWasNotified()
        (recordedStateChanges.last() as? LoginViewModel.State.Error)
            ?.also { assertTrue(it.passwordError == R.string.error_incorrect_password) }
            ?: fail("The last state is not of type Error")
    }
}
