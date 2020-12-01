package com.mysugr.android.testing.v1.example.net

import com.mysugr.android.testing.example.net.AuthToken
import com.mysugr.android.testing.example.user.User
import java.util.UUID

data class BackendFakeUser(
    val email: String,
    val password: String,
    val authToken: AuthToken = UUID.randomUUID().toString()
) {
    companion object {
        val TEST_USER = BackendFakeUser(
            "user.a@test.com",
            "supersecure_a"
        )
    }
}

fun BackendFakeUser.asUser() = User(this.email)
