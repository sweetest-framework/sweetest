package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.user.User

/**
 * Simulates access to dummy backend
 */
object DummyBackend {

    private val existingUsers = mutableListOf(
            User("test1@test.com", "secure1"),
            User("test2@test.com", "secure2"))

    fun addUser(user: User) { existingUsers.add(user) }
    fun userExists(email: String) = getUser(email) != null
    fun getUser(email: String) = existingUsers.find { it.email == email }

    data class User(
            val email: String,
            val password: String
    ) {
        fun toLocalUser() = User(email)
    }
}
