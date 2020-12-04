package dev.sweetest.demo.v1.example.auth

import dev.sweetest.demo.v1.example.net.BackendFakeSteps
import dev.sweetest.demo.v1.example.net.BackendFakeUser.Companion.TEST_USER
import dev.sweetest.demo.v1.example.state.SessionStoreSteps
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import org.junit.Test

class AuthManagerTest : BaseJUnitTest() {

    private val sut by steps<AuthManagerSteps>()
    private val backend by steps<BackendFakeSteps>()
    private val sessionStore by steps<SessionStoreSteps>()

    @Test
    fun `Login as existing user, checks for existing email`() = sut {
        backend.givenExistingUser(TEST_USER)
        whenPassingCredentials(TEST_USER.email, TEST_USER.password)
        backend.thenEmailWasChecked(TEST_USER.email)
    }

    @Test
    fun `Login as existing user, attempts login`() = sut {
        backend.givenExistingUser(TEST_USER)
        whenPassingCredentials(TEST_USER.email, TEST_USER.password)
        backend.thenLoginWasAttempted(TEST_USER.email, TEST_USER.password)
    }

    @Test
    fun `Login as existing user, starts session`() = sut {
        backend.givenExistingUser(TEST_USER)
        whenPassingCredentials(TEST_USER.email, TEST_USER.password)
        sessionStore.thenASessionIsStarted()
    }
}
