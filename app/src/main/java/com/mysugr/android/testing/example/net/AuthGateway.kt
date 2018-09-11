package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.net.DummyBackend.addUser
import com.mysugr.android.testing.example.net.DummyBackend.getUser
import com.mysugr.android.testing.example.net.DummyBackend.userExists
import com.mysugr.android.testing.example.net.DummyBackend.RemoteUser
import com.mysugr.android.testing.example.net.DummyBackend.loggedInUser

/**
 * Simulates backend access
 */
class AuthGateway {

    fun checkEmail(email: String): Boolean {
        Thread.sleep(1000L)
        return userExists(email)
    }

    fun login(email: String, password: String): Boolean {
        Thread.sleep(1000L)
        val remoteUser = getUser(email)
        return if (remoteUser == null || remoteUser.password != password) {
            false
        } else {
            loggedInUser = remoteUser.toUser()
            true
        }
    }

    fun register(email: String, password: String): Boolean {
        Thread.sleep(1000L)
        check(!userExists(email)) { "Can't register already existing user" }
        addUser(RemoteUser(email, password))
        return true
    }

    fun logout() {
        Thread.sleep(1000L)
        check(loggedInUser != null) { "Already logged out" }
        loggedInUser = null
    }

}
