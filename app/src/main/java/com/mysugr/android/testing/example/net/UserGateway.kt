package com.mysugr.android.testing.example.net

import com.mysugr.android.testing.example.user.User

class UserGateway {

    fun getUserData(): User {
        return DummyBackend.loggedInUser ?: throw Exceptions()
    }

}
