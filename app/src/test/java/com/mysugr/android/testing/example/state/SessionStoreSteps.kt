package com.mysugr.android.testing.example.state

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.testing.framework.base.*
import com.mysugr.testing.framework.context.TestContext

import org.mockito.Mockito.*

class SessionStoreSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .requireMock<SessionStore>()

    private val user by steps<UserSteps>()

    val instance by dependency<SessionStore>()

    fun thenSessionIsStarted() {
        val expected = User(user.email)
        verify(instance).beginSession(user.authToken, expected)
    }

    fun thenSessionIsNotStarted() {
        verify(instance, never()).beginSession(any() ?: "", any() ?: User("dummy"))
    }

    fun thenSessionIsEnded() {
        verify(instance).endSession()
    }

}
