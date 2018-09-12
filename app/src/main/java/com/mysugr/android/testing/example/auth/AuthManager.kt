package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.net.*
import com.mysugr.android.testing.example.state.SessionStore

class AuthManager(
        private val authGateway: AuthGateway,
        private val userGateway: UserGateway,
        private val sessionStore: SessionStore) {

    fun login(email: String, password: String): LoginResult {
        val exists = authGateway.checkEmail(email)
        val result = if (exists) {
            try {
                gatewayAuthToken = authGateway.login(email, password)
            } catch (exception: UsernameOrPasswordWrongException) {
                throw WrongPasswordException()
            }
            LoginResult.LOGGED_IN
        } else {
            gatewayAuthToken = authGateway.register(email, password)
            LoginResult.REGISTERED
        }
        val user = userGateway.getUserData()
        sessionStore.beginSession(user)
        return result
    }

    private var gatewayAuthToken: AuthToken? = null
        set(value) {
            field = value
            authGateway.authToken = value
            userGateway.authToken = value
        }

    fun logout() {
        sessionStore.endSession()
    }

    enum class LoginResult {
        LOGGED_IN,
        REGISTERED
    }

    class WrongPasswordException : Exception()

}
