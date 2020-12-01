package com.mysugr.android.testing.v1.example.net

import com.mysugr.android.testing.v1.example.appModuleTestingConfiguration
import com.mysugr.android.testing.v1.example.feature.auth.UserSteps
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.net.UsernameOrPasswordWrongException
import com.mysugr.android.testing.example.user.User
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class BackendGatewaySteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireMock<BackendGateway>()
        .onSetUp(this::setUp)

    private val instance by dependency<BackendGateway>()

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

    fun thenLoginAttempted(email: String? = null) {
        verify(instance).login(email ?: user.email, user.password)
    }

    fun thenRegistered(email: String? = null, password: String? = null) {
        verify(instance).register(email ?: user.email, password ?: user.password)
    }
}
