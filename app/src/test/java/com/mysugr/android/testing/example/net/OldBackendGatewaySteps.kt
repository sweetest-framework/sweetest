package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.feature.auth.UserSteps
import com.mysugr.android.testing.example.user.User
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class OldBackendGatewaySteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireMock<BackendGateway>()
        .onSetUp(this::setUp)

    private val instance by dependency<BackendGateway>()

    private val user by steps<UserSteps>()

    private fun setUp() {
        `when`(instance.register(anyString(), anyString())).thenReturn(AUTH_TOKEN)
    }

    fun givenUserExists() {
        `when`(instance.checkEmail(anyString())).thenReturn(true)
    }

    fun givenUserDoesNotExist() {
        `when`(instance.checkEmail(anyString())).thenReturn(false)
    }

    fun givenUsernameAndPasswordCorrect() {
        `when`(instance.login(anyString(), anyString())).thenReturn(AUTH_TOKEN)
    }

    fun givenUsernameOrPasswordWrong() {
        `when`(instance.login(anyString(), anyString())).thenThrow(UsernameOrPasswordWrongException())
    }

    fun givenUser(user: User) {
        `when`(instance.getUserData(anyString())).thenReturn(user)
    }

    fun thenEmailIsChecked() {
        verify(instance).checkEmail(user.email)
    }

    fun thenLoginAttempted(email: String? = null) {
        verify(instance).login(email ?: user.email, user.password)
    }

    fun thenRegistered(email: String? = null, password: String? = null) {
        verify(instance).register(email ?: user.email, password ?: user.password)
    }

    companion object {
        const val AUTH_TOKEN = "the_auth_token"
    }
}
