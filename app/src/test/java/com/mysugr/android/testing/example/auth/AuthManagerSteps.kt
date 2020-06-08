package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.net.BackendFakeSteps
import com.mysugr.android.testing.example.state.SessionStore
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.util.nonNullable
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify

class AuthManagerSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    val backend by steps<BackendFakeSteps>()

    private val instance by dependency<AuthManager>()
    private val sessionStore by dependency<SessionStore>()

    override fun configure() = super.configure()
        .requireReal<AuthManager>()

    fun whenPassingCredentials(email: String, password: String) {
        instance.loginOrRegister(email, password)
    }

    fun thenSessionWasStarted() {
        verify(sessionStore).beginSession(anyString().nonNullable, any<User>().nonNullable)
    }
}