package com.mysugr.android.testing.example.net

import java.util.UUID

data class BackendFakeUser(
    val email: String,
    val password: String,
    val authToken: AuthToken = UUID.randomUUID().toString()
) {
    companion object {
        val USER_A = BackendFakeUser("user.a@test.com", "supersecure_a")
        val USER_B = BackendFakeUser("user.b@test.com", "supersecure_b")
    }
}
