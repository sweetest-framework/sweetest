package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.util.isMock
import org.mockito.Mockito.*

class AuthManagerSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure()= super.configure()
            .onSetUp(this::setUp)

    private val instance by dependency<AuthManager>()
    private val user by steps<UserSteps>()

    private fun setUp() {
        if (instance.isMock) {
            `when`(instance.loginOrRegister(anyString(), anyString())).then {
                if (user.correctPassword) {
                    if (user.exists) {
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

    fun whenLoggingInOrRegistering() {
        instance.loginOrRegister(user.email, user.password)
    }

    fun whenLoggingOut() {
        instance.logout()
    }

    fun thenLoginOrRegisterIsCalled() {
        verify(instance).loginOrRegister(user.email, user.password)
    }

}
