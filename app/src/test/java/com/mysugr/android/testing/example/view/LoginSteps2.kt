package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManagerSteps
import com.mysugr.android.testing.example.net.BackendFakeSteps
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import kotlinx.coroutines.test.TestCoroutineScope

class LoginSteps2(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    lateinit var scope: TestCoroutineScope
    val backend by steps<BackendFakeSteps>()

    private val viewModel by dependency<LoginViewModel>()
    private val backendGateway by dependency<BackendGateway>()

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireSteps<AuthManagerSteps>()

    fun whenLoggingIn(email: String, password: String) {
        viewModel.loginOrRegister(email, password)
    }
}
