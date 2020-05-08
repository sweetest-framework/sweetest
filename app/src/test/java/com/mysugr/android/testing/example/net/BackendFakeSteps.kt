package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.context.TestContext
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class BackendFakeSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    // The steps class creates a fake and creates a Mockito spy from it (for the sake of being able to use `verify`)
    private val instance = spy(FakeBackendGateway())

    private val instance2 by dependency<FakeBackendGateway>()

    // Here we provide an instance of `BackendGateway` to sweetest's dependency management
    override fun configure() = super.configure()
        .offerMockRequired<BackendGateway> { instance }

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
            TODO()
        }

        override fun getUserData(authToken: AuthToken): User {
            TODO()
        }
    }
}


