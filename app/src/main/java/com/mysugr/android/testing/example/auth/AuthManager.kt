package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.net.*
import com.mysugr.android.testing.example.state.SessionStore

class AuthManager(
        private val backendGateway: BackendGateway,
        private val sessionStore: SessionStore) {

    fun loginOrRegister(email: String, password: String): LoginResult {
        val exists = backendGateway.checkEmail(email)
        val result = if (exists) {
            try {
                authToken = backendGateway.login(email, password)
            } catch (exception: UsernameOrPasswordWrongException) {
                throw WrongPasswordException()
            }
            LoginResult.LOGGED_IN
        } else {
            authToken = backendGateway.register(email, password)
            LoginResult.REGISTERED
        }
        val user = backendGateway.getUserData()
        sessionStore.beginSession(user)
        return result
    }

    private var authToken: AuthToken? = null
        set(value) {
            field = value
            backendGateway.authToken = value
        }

    fun logout() {
        sessionStore.endSession()
        authToken = null
    }

    enum class LoginResult {
        LOGGED_IN,
        REGISTERED
    }

    class WrongPasswordException : Exception()

}
