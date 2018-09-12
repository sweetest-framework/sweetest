package com.mysugr.android.testing.example.state

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.testing.framework.base.*
import com.mysugr.testing.framework.context.TestContext

import org.junit.Assert.*
import org.mockito.Mockito.*

class SessionStoreSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .requireMock<SessionStore>()
            .onSetUp(this::setUp)

    private val user by steps<UserSteps>()

    val instance by dependency<SessionStore>()

    private fun setUp() {

    }

    fun thenSessionIsStarted() {
        val expected = User(user.email)
        verify(instance).beginSession(expected)
    }

}
