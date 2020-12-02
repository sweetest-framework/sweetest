package com.mysugr.android.testing.v2.example.auth

import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.auth.AuthManager.LoginOrRegisterResult
import com.mysugr.android.testing.util.nonNullableAny
import dev.sweetest.api.v2.Steps
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class AuthManagerMockSteps : Steps() {

    init {
        provide<AuthManager> { mock(AuthManager::class.java) }
    }

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
