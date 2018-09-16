package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.sweetest.framework.base.*
import com.mysugr.sweetest.framework.context.TestContext

import org.mockito.Mockito.*

class BackendGatewaySteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
            .requireMock<BackendGateway>()
            .onSetUp(this::setUp)

    val instance by dependency<BackendGateway>()

    private val user by steps<UserSteps>()

    private fun setUp() {
        `when`(instance.checkEmail(anyString())).then {
            val email = it.arguments[0] as String
            user.isUserExisting(email)
        }
        `when`(instance.login(anyString(), anyString())).then {
            val email = it.arguments[0] as String
            val password = it.arguments[1] as String
            if (user.isUserExisting(email) && user.isPasswordCorrect(password)) {
                user.authToken
            } else {
                throw UsernameOrPasswordWrongException()
            }
        }
        `when`(instance.register(anyString(), anyString())).then { user.authToken }
        `when`(instance.getUserData(anyString())).then { User(user.email) }
    }

    fun thenEmailIsChecked() {
        verify(instance).checkEmail(user.email)
    }

    fun thenLoggedIn(email: String? = null) {
        verify(instance).login(email ?: user.email, user.password)
    }

    fun thenRegistered(email: String? = null, password: String? = null) {
        verify(instance).register(email ?: user.email, password ?: user.password)
    }

}
