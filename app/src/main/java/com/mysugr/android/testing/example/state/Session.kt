package com.mysugr.android.testing.example.state

import com.mysugr.android.testing.example.net.AuthToken
import com.mysugr.android.testing.example.user.User

class Session(
        val authToken: AuthToken,
        val user: User
)
