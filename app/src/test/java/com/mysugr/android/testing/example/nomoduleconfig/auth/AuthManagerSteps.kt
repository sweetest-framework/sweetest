package com.mysugr.android.testing.example.nomoduleconfig.auth

import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.util.isMock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify

class AuthManagerSteps(testContext: TestContext) : BaseSteps(testContext) {

    override fun configure() = super.configure()
        .onSetUp(this::setUp)

    private val instance by dependency<AuthManager>()
    private val user by steps<UserSteps>()

    private fun setUp() {
        if (instance.isMock) {
            `when`(instance.loginOrRegister(anyString(), anyString())).then {
                val password = it.arguments[1] as String
                if (user.isPasswordCorrect(password)) {
                    val email = it.arguments[0] as String
                    if (user.isUserExisting(email)) {
                        AuthManager.LoginOrRegisterResult.LOGGED_IN
                    } else {
                        AuthManager.LoginOrRegisterResult.REGISTERED
                    }
                } else {
                    throw AuthManager.WrongPasswordException()
                }
            }
        }
    }

    fun whenLoggingInOrRegistering(email: String? = null, password: String? = null) {
        instance.loginOrRegister(email ?: user.email, password ?: user.password)
    }

    fun whenLoggingOut() {
        instance.logout()
    }

    fun thenLoginOrRegisterIsCalled() {
        verify(instance).loginOrRegister(user.email, user.password)
    }
}
