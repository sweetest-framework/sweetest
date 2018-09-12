package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.testing.framework.base.*
import com.mysugr.testing.framework.context.TestContext

import org.junit.Assert.*
import org.mockito.Mockito.*

class BackendGatewaySteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .requireMock<BackendGateway>()
            .onSetUp(this::setUp)

    val instance by dependency<BackendGateway>()

    private val user by steps<UserSteps>()

    private fun setUp() {
        `when`(instance.checkEmail(anyString())).then { user.exists }
        `when`(instance.login(anyString(), anyString())).then { user.authToken }
        `when`(instance.register(anyString(), anyString())).then { user.authToken }
        `when`(instance.getUserData()).then { User(user.email) }
    }

    fun thenEmailIsChecked() {
        verify(instance).checkEmail(user.email)
    }

    fun thenLoggedIn() {
        verify(instance).login(user.email, user.password)
    }

    fun thenRegistered() {
        verify(instance).register(user.email, user.password)
    }

    fun thenCorrectAuthTokenIsSet() {
        verify(instance).authToken = user.authToken
    }

}
