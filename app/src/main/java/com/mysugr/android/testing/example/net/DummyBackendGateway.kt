package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.user.User
import java.util.*

/**
 * Gateway to the [DummyBackend]
 */
class DummyBackendGateway : BackendGateway {

    private var loggedInUser: DummyBackend.User? = null

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
        loggedInUser = remoteUser
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
        return loggedInUser?.toLocalUser() ?: throw NotLoggedInException()
    }
}
