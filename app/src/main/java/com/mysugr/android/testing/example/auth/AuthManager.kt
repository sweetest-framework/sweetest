package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.net.AuthGateway
import com.mysugr.android.testing.example.net.UserDoesNotExistException
import com.mysugr.android.testing.example.net.UserGateway
import com.mysugr.android.testing.example.state.SessionStore

class AuthManager(
        private val authGateway: AuthGateway,
        private val userGateway: UserGateway,
        private val sessionStore: SessionStore) {

    fun login(email: String, password: String): LoginResult {
        val exists = authGateway.checkEmail(email)
        val result = if (exists) {
            if (authGateway.login(email, password)) {
                LoginResult.LOGGED_IN
            } else {
                throw WrongPasswordException()
            }
        } else {
            authGateway.register(email, password)
            LoginResult.REGISTERED
        }
        val user = userGateway.getUserData()
        sessionStore.beginSession(user)
        return result
    }

    fun logout() {
        TODO()
    }

    enum class LoginResult {
        LOGGED_IN,
        REGISTERED
    }

    class WrongPasswordException : Exception()

}
