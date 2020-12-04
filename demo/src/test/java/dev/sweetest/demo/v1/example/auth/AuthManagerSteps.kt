package dev.sweetest.demo.v1.example.auth

import dev.sweetest.demo.auth.AuthManager
import dev.sweetest.v1.BaseSteps
import dev.sweetest.internal.TestContext
import dev.sweetest.v1.dependency

class AuthManagerSteps(testContext: TestContext) : BaseSteps(testContext) {

    private val instance by dependency<AuthManager>()

    override fun configure() = super.configure()
        .provide<AuthManager>()

    fun whenPassingCredentials(email: String, password: String) {
        instance.loginOrRegister(email, password)
    }
}
