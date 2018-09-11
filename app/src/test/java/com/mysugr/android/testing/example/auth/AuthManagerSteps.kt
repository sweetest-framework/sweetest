package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.UserSteps
import com.mysugr.testing.framework.base.BaseSteps
import com.mysugr.testing.framework.base.dependency
import com.mysugr.testing.framework.base.steps
import com.mysugr.testing.framework.context.TestContext
import com.mysugr.testing.util.isMock
import org.mockito.Mockito.*

class AuthManagerSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure()= super.configure()
            .onSetUp(this::setUpMock)

    private val instance by dependency<AuthManager>()
    private val user by steps<UserSteps>()

    private fun setUpMock() {
        if (instance.isMock) {
            stubLogin()
        }
    }

    private fun stubLogin() {
        `when`(instance.login(anyString(), anyString())).then {
            if (user.correctPassword) {
                if (user.exists) {
                    AuthManager.LoginResult.LOGGED_IN
                } else {
                    AuthManager.LoginResult.REGISTERED
                }
            } else {
                throw AuthManager.WrongPasswordException()
            }
        }
    }

    fun thenLoginCalled() {
        verify(instance).login(user.email, user.password)
    }

}
