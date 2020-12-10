package dev.sweetest.demo.v1.example.net

import dev.sweetest.demo.net.AuthToken
import dev.sweetest.demo.user.User
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
