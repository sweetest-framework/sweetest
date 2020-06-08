package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.AuthToken
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.context.TestContext
import kotlinx.coroutines.test.TestCoroutineScope
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class LoginSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    lateinit var scope: TestCoroutineScope

    private val viewModel by dependency<LoginViewModel>()
    private val backendGateway by dependency<BackendGateway>()

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireReal<AuthManager>()

    fun givenExistingUser(email: String, password: String, authToken: AuthToken) {
        `when`(backendGateway.checkEmail(email)).thenReturn(true)
        `when`(backendGateway.login(email, password)).thenReturn(authToken)
    }

    fun whenLoggingIn(email: String, password: String) {
        viewModel.loginOrRegister(email, password)
    }

    fun thenEmailWasCheckedAtBackend(email: String) {
        verify(backendGateway).checkEmail(email)
    }
}
