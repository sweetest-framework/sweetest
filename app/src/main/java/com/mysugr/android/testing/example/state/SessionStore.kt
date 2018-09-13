package com.mysugr.android.testing.example.state

import com.mysugr.android.testing.example.net.AuthToken
import com.mysugr.android.testing.example.user.User

class SessionStore { // TODO abstract interface

    private var _session: Session? = null
    var session: Session
        get() = _session ?: error("Session not started")
        private set(value) { _session = value }

    val started: Boolean
        get() = _session != null

    fun beginSession(authToken: AuthToken, user: User) {
        check(_session == null)
        _session = Session(authToken, user)
    }

    fun endSession() {
        _session = null
    }

}
