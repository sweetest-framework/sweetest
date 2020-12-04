package com.mysugr.android.testing.v2.example.net

import com.mysugr.android.testing.example.net.AuthToken
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.net.UsernameOrPasswordWrongException
import com.mysugr.android.testing.example.user.User
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import dev.sweetest.api.v2.Steps
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class BackendFakeSteps : Steps() {

    // The steps class creates a fake and creates a Mockito spy from it (for the sake of being able to use `verify`)
    private val instance = spy(FakeBackendGateway())

    init {
        // Here we provide an instance of `BackendGateway` to sweetest's dependency management
        provide<BackendGateway> { instance }
    }

    @Before("@login-integration")
    fun dummy() {
    }

    @Given("^there is already a user at the backend with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun givenExistingUser(email: String, password: String) {
        givenExistingUser(BackendFakeUser(email, password))
    }

    // This adds a user to the fake backend
    fun givenExistingUser(backendFakeUser: BackendFakeUser) {
        instance.users += backendFakeUser
    }

    // This verifies the expected call to the fake
    fun thenEmailWasChecked(email: String) {
        verify(instance).checkEmail(email)
    }

    // Same here
    fun thenLoginWasAttempted(email: String, password: String) {
        verify(instance).login(email, password)
    }

    @Then("^there is a user at the backend with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun thenThereIsAUser(email: String, password: String) {
        assert(instance.users.any { it.email == email && it.password == password })
    }

    // The fake is private as a steps classes' aim is to abstract the technical implementation of test code.
    private class FakeBackendGateway : BackendGateway {

        // We leave the internals open to the outside class for simplicity's sake
        val users = mutableListOf<BackendFakeUser>()

        override fun checkEmail(email: String): Boolean = users.find { it.email == email } != null

        override fun login(email: String, password: String): AuthToken {
            val user = users.find { it.email == email && it.password == password }
                ?: throw UsernameOrPasswordWrongException()
            return user.authToken
        }

        override fun register(email: String, password: String): AuthToken {
            val registeredUser = BackendFakeUser(email, password)
            users += registeredUser
            return registeredUser.authToken
        }

        override fun getUserData(authToken: AuthToken): User {
            return users.find { it.authToken == authToken }
                ?.asUser()
                ?: error("User with authToken \"$authToken\" not found")
        }
    }
}
