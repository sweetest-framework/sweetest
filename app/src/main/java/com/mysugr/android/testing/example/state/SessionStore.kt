package com.mysugr.android.testing.example.state

import com.mysugr.android.testing.example.user.User

class SessionStore {

    private var _session: Session? = null
    var session: Session
        get() = _session ?: error("Session not started")
        private set(value) { _session = value }

    val started: Boolean
        get() = _session != null

    fun beginSession(user: User) {
        check(_session == null)
        _session = Session(user)
    }

    fun endSession() {
        _session = null
    }

}
