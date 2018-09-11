package com.mysugr.android.testing.example.app.view

import com.mysugr.android.testing.example.app.R
import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.testing.framework.base.*
import com.mysugr.testing.framework.context.TestContext
import org.junit.Assert.*

class LoginViewModelSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    private val instance by dependency<LoginViewModel>()
    private val user by steps<UserSteps>()

    private val recordedStateChanges = mutableListOf<LoginViewModel.State>()
    private val stateChangeSync = Object()

    fun givenStateListener() {
        instance.stateListener = {
            recordedStateChanges.add(it)
            synchronized(stateChangeSync) {
                stateChangeSync.notify()
            }
        }
    }

    fun whenLoggingIn() {
        instance.attemptLogin(user.email, user.password)
    }

    fun <R> whenWaitForState(ofType: Class<R>): R {

        var resultState: R? = null
        val startedAtMillis = System.currentTimeMillis()
        val maxTimeMillis = 1000L

        fun checkLastState(): Boolean {
            val last = recordedStateChanges.last()
            val isOfCorrectType = recordedStateChanges.size > 0 && last::class.java == ofType
            if (isOfCorrectType) {
                @Suppress("UNCHECKED_CAST")
                resultState = last as R
            }
            return isOfCorrectType
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

    fun thenStateIsLoggedInWithNewUser() {
        thenStateChangeWasNotified()
        val last = recordedStateChanges.last() as? LoginViewModel.State.LoggedIn
        assertNotNull(last)
        assertTrue(last!!.isNewUser)
    }

    fun thenStateIsErrorWrongPassword() {
        thenStateChangeWasNotified()
        (recordedStateChanges.last() as? LoginViewModel.State.Error)
                ?.let { assertTrue(it.passwordError == R.string.error_incorrect_password) }
                ?: fail()
    }

}
