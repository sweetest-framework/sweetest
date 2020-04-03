package com.mysugr.android.testing.example.net

import java.util.UUID

data class FakeBackendUser(
    val email: String,
    val password: String,
    val authToken: AuthToken = UUID.randomUUID()
        .toString()
)