package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext

class AuthManagerRealSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireReal<AuthManager>()

    private val instance by dependency<AuthManager>()
    private val user by steps<UserSteps>()

    fun whenLoggingInOrRegistering(email: String? = null, password: String? = null) {
        instance.loginOrRegister(email ?: user.email, password ?: user.password)
    }

    fun whenLoggingOut() {
        instance.logout()
    }
}