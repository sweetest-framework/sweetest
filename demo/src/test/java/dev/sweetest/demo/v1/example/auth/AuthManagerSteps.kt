package dev.sweetest.demo.v1.example.auth

import dev.sweetest.demo.auth.AuthManager
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import dev.sweetest.internal.TestContext

class AuthManagerSteps(testContext: TestContext) : BaseSteps(testContext) {

    private val instance by dependency<AuthManager>()

    override fun configure() = super.configure()
        .provide<AuthManager>()

    fun whenPassingCredentials(email: String, password: String) {
        instance.loginOrRegister(email, password)
    }
}
