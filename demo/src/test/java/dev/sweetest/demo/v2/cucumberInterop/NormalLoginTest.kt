package dev.sweetest.demo.v2.cucumberInterop

import dev.sweetest.demo.v2.example.net.BackendFakeUser.Companion.TEST_USER
import dev.sweetest.v2.junit4.JUnit4Test
import org.junit.Test

// This is to show that the same steps class can be utilized with Cucumber as well as with JUnit
class NormalLoginTest : JUnit4Test() {

    private val sut by steps<LoginSteps>()

    @Test
    fun `Logging in checks email at backend`() = sut {
        backend.givenExistingUser(TEST_USER)
        whenLoginOrRegister(TEST_USER.email, TEST_USER.password)
        backend.thenEmailWasChecked(TEST_USER.email)
    }
}
