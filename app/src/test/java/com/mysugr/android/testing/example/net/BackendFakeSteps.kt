package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class BackendFakeSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    private lateinit var instance: FakeBackendGateway

    override fun configure() = super.configure()
        .offerMockRequired<BackendGateway> { spy(FakeBackendGateway()).also { instance = it } }

    fun givenExistingUser(backendUser: FakeBackendUser) {
        instance.users += backendUser
    }

    fun thenEmailWasChecked(email: String) {
        verify(instance).checkEmail(email)
    }

    fun thenLoginWasAttempted(email: String, password: String) {
        verify(instance).login(email, password)
    }

    private class FakeBackendGateway : BackendGateway {
        val users = mutableListOf<FakeBackendUser>()

        override fun checkEmail(email: String): Boolean = users.find { it.email == email } != null

        override fun login(email: String, password: String): AuthToken {
            val user = users.find { it.email == email && it.password == password }
                ?: throw UsernameOrPasswordWrongException()
            return user.authToken
        }

        override fun register(email: String, password: String): AuthToken {
            if (checkEmail(email)) throw UserAlreadyExistsException()
            val newUser = FakeBackendUser(email, password)
            users += newUser
            return newUser.authToken
        }

        override fun getUserData(authToken: AuthToken): User {
            val fakeBackendUser = users.find { it.authToken == authToken }
                ?: throw UnknownAuthTokenException()
            return User(fakeBackendUser.email)
        }
    }
}


