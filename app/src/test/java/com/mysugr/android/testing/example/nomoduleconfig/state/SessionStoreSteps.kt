package com.mysugr.android.testing.example.nomoduleconfig.state

import com.mysugr.android.testing.example.nomoduleconfig.auth.UserSteps
import com.mysugr.android.testing.example.state.SessionStore
import com.mysugr.android.testing.example.user.User
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class SessionStoreSteps(testContext: TestContext) : BaseSteps(testContext) {

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
