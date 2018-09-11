package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.user.User

object DummyBackend {

    var loggedInUser: User? = null
    val existingUsers = mutableListOf(
            RemoteUser("test1@test.com", "secure1"),
            RemoteUser("test2@test.com", "secure2"))
    fun addUser(user: RemoteUser) { existingUsers.add(user) }
    fun userExists(email: String) = getUser(email) != null
    fun getUser(email: String) = existingUsers.find { it.email == email }

    data class RemoteUser(
            val email: String,
            val password: String
    ) {
        fun toUser() = User(email)
    }

}
