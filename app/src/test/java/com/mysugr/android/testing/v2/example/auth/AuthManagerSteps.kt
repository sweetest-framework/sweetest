package com.mysugr.android.testing.v2.example.auth

import com.mysugr.android.testing.example.auth.AuthManager
import dev.sweetest.api.v2.Steps

class AuthManagerSteps : Steps() {

    private val instance by dependency<AuthManager>()

    init {
        provide<AuthManager>()
    }

    fun whenPassingCredentials(email: String, password: String) {
        instance.loginOrRegister(email, password)
    }
}
