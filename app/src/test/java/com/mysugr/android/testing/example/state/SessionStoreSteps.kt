package com.mysugr.android.testing.example.state

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.sweetest.framework.base.*
import com.mysugr.sweetest.framework.context.TestContext

import org.mockito.Mockito.*

class SessionStoreSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireMock<SessionStore>()

    private val user by steps<UserSteps>()

    private val instance by dependency<SessionStore>()

    fun thenSessionIsStarted(email: String? = null) {
        val expected = User(email ?: user.email)
        verify(instance).beginSession(user.authToken, expected)
    }

    fun thenSessionIsNotStarted() {
        verify(instance, never()).beginSession(any() ?: "", any() ?: User("dummy"))
    }

    fun thenSessionIsEnded() {
        verify(instance).endSession()
    }
}
