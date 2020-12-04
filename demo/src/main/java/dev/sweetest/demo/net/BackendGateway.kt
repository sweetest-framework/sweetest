package dev.sweetest.demo.net

import dev.sweetest.demo.user.User

typealias AuthToken = String

interface BackendGateway {
    fun checkEmail(email: String): Boolean
    fun login(email: String, password: String): AuthToken
    fun register(email: String, password: String): AuthToken
    fun getUserData(authToken: AuthToken): User
}
