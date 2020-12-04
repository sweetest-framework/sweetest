package dev.sweetest.demo.v1.example.auth

import dev.sweetest.demo.auth.AuthManager
import dev.sweetest.demo.auth.AuthManager.LoginOrRegisterResult
import dev.sweetest.demo.util.nonNullableAny
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import dev.sweetest.internal.TestContext
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class AuthManagerMockSteps(testContext: TestContext) : BaseSteps(testContext) {

    override fun configure() = super.configure()
        .provide<AuthManager> { mock(AuthManager::class.java) }

    private val instance by dependency<AuthManager>()

    fun givenNewUser() {
        `when`(instance.loginOrRegister(anyString(), anyString())).thenReturn(LoginOrRegisterResult.REGISTERED)
    }

    fun givenExistingUser() {
        `when`(instance.loginOrRegister(anyString(), anyString())).thenReturn(LoginOrRegisterResult.LOGGED_IN)
    }

    fun givenWrongPassword() {
        `when`(instance.loginOrRegister(anyString(), anyString())).then { throw AuthManager.WrongPasswordException() }
    }

    fun thenLoginOrRegisterIsCalled() {
        verify(instance).loginOrRegister(nonNullableAny(), nonNullableAny())
    }
}
