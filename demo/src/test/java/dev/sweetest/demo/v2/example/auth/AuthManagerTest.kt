package dev.sweetest.demo.v2.example.auth

import dev.sweetest.demo.v2.example.net.BackendFakeSteps
import dev.sweetest.demo.v2.example.net.BackendFakeUser.Companion.TEST_USER
import dev.sweetest.demo.v2.example.state.SessionStoreSteps
import dev.sweetest.api.v2.framework.base.JUnit4Test
import org.junit.Test

class AuthManagerTest : JUnit4Test() {

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
