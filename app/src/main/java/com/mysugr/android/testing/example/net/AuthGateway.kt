package com.mysugr.android.testing.example.net

import java.util.*

/**
 * Simulates backend access
 */
class AuthGateway : BaseGateway() {

    fun checkEmail(email: String): Boolean {
        Thread.sleep(1000L)
        return DummyBackend.userExists(email)
    }

    fun login(email: String, password: String): AuthToken {
        Thread.sleep(1000L)
        val remoteUser = DummyBackend.getUser(email)
        if (remoteUser == null || remoteUser.password != password) {
            throw UsernameOrPasswordWrongException()
        } else {
            return loginInternal(remoteUser)
        }
    }
    private fun loginInternal(remoteUser: DummyBackend.RemoteUser): AuthToken {
        DummyBackend.loggedInUser = remoteUser.toUser()
        return UUID.randomUUID().toString()
    }

    fun register(email: String, password: String): AuthToken {
        Thread.sleep(1000L)
        check(!DummyBackend.userExists(email)) { "Can't register already existing user" }
        val remoteUser = DummyBackend.RemoteUser(email, password)
        DummyBackend.addUser(remoteUser)
        return loginInternal(remoteUser)
    }

    fun logout() {
        Thread.sleep(1000L)
        check(DummyBackend.loggedInUser != null) { "Already logged out" }
        DummyBackend.loggedInUser = null
    }

}
