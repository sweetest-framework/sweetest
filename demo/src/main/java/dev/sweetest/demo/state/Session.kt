package dev.sweetest.demo.state

import dev.sweetest.demo.net.AuthToken
import dev.sweetest.demo.user.User

class Session(
    val authToken: AuthToken,
    val user: User
)
