package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.net.AuthGateway
import com.mysugr.android.testing.example.net.UserDoesNotExistException
import com.mysugr.android.testing.example.net.UserGateway
import com.mysugr.android.testing.example.state.SessionStore

class AuthenticationManager(
        private val authGateway: AuthGateway,
        private val userGateway: UserGateway,
        private val sessionStore: SessionStore) {

    fun login(email: String, password: String) {
        val exists = authGateway.checkEmail(email)
        if (exists) {
            authGateway.login(email, password)
            val user = userGateway.getUserData()
            sessionStore.beginSession(user)
        } else {
            throw UserDoesNotExistException()
        }
    }

    fun logout() {

    }

}
