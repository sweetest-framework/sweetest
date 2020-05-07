package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.user.User
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import com.mysugr.sweetest.framework.context.TestContext
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class BackendGatewayMockSteps(testContext: TestContext) : BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure().requireMock<BackendGateway>().onSetUp(this::setUp)

    private val instance by dependency<BackendGateway>()
    private var backendUsers = mutableListOf<BackendFakeUser>()

    private fun setUp() {
        `when`(instance.checkEmail(anyString())).then {
            val email = it.arguments[0] as String
            backendUsers.any { it.email == email }
        }
        `when`(instance.login(anyString(), anyString())).then {
            val email = it.arguments[0] as String
            val password = it.arguments[1] as String
            val foundUser = backendUsers.find {
                it.email == email && it.password == password
            }
            foundUser?.authToken ?: throw UsernameOrPasswordWrongException()
        }
        `when`(instance.register(anyString(), anyString())).then {
            val email = it.arguments[0] as String
            val password = it.arguments[1] as String
            val newUser = BackendFakeUser(email, password)
            backendUsers.add(newUser)
            newUser.authToken
        }
        `when`(instance.getUserData(anyString())).then {
            val authToken = it.arguments[0]
            val foundUser = backendUsers.find { it.authToken == authToken }
            foundUser?.let {
                User(it.email)
            } ?: error("Auth token $authToken not found")
        }
    }

    fun givenUsers(vararg backendFakeUsers: BackendFakeUser) {
        this.backendUsers = backendFakeUsers.toMutableList()
    }

    fun thenEmailIsChecked(email: String) {
        verify(instance).checkEmail(email)
    }

    fun thenLoginAttempted(email: String, password: String) {
        verify(instance).login(email, password)
    }

    fun thenRegistered(email: String, password: String) {
        verify(instance).register(email, password)
    }

    fun getUser(email: String): BackendFakeUser {
        return backendUsers.find { it.email == email } ?: error("User $email not found")
    }
}