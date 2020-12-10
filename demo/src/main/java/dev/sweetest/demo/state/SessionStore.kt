package dev.sweetest.demo.state

import dev.sweetest.demo.net.AuthToken
import dev.sweetest.demo.user.User

interface SessionStore {
    val session: Session
    val isStarted: Boolean
    fun beginSession(authToken: AuthToken, user: User)
    fun endSession()
}

class DummySessionStore : SessionStore {

    private var _session: Session? = null
    override var session: Session
        get() = _session ?: error("Session not started")
        private set(value) { _session = value }

    override val isStarted: Boolean
        get() = _session != null

    override fun beginSession(authToken: AuthToken, user: User) {
        check(_session == null)
        _session = Session(authToken, user)
    }

    override fun endSession() {
        _session = null
    }
}
