package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.user.User
import java.util.*

typealias AuthToken = String

interface BackendGateway {
    fun checkEmail(email: String): Boolean
    fun login(email: String, password: String): AuthToken
    fun register(email: String, password: String): AuthToken
    fun getUserData(authToken: AuthToken): User
}

/**
 * Simulates access to dummy backend
 */
class DummyBackendGateway : BackendGateway {

    override fun checkEmail(email: String): Boolean {
        Thread.sleep(1000L)
        return DummyBackend.userExists(email)
    }

    override fun login(email: String, password: String): AuthToken {
        Thread.sleep(1000L)
        val remoteUser = DummyBackend.getUser(email)
        if (remoteUser == null || remoteUser.password != password) {
            throw UsernameOrPasswordWrongException()
        } else {
            return loginInternal(remoteUser)
        }
    }

    private fun loginInternal(remoteUser: DummyBackend.User): AuthToken {
        DummyBackend.loggedInUser = remoteUser
        return UUID.randomUUID().toString()
    }

    override fun register(email: String, password: String): AuthToken {
        Thread.sleep(1000L)
        check(!DummyBackend.userExists(email)) { "Can't register already existing user" }
        val remoteUser = DummyBackend.User(email, password)
        DummyBackend.addUser(remoteUser)
        return loginInternal(remoteUser)
    }

    override fun getUserData(authToken: AuthToken): User {
        return DummyBackend.loggedInUser?.toLocalUser() ?: throw NotLoggedInException()
    }

}
