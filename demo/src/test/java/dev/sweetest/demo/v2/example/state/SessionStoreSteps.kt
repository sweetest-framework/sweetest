package dev.sweetest.demo.v2.example.state

import dev.sweetest.demo.state.SessionStore
import dev.sweetest.demo.user.User
import dev.sweetest.demo.util.nonNullable
import dev.sweetest.demo.util.nonNullableAny
import dev.sweetest.v2.Steps
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SessionStoreSteps : Steps() {

    init {
        provide<SessionStore> { mock(SessionStore::class.java) }
    }

    private val instance by dependency<SessionStore>()

    fun thenASessionIsStarted() {
        verify(instance).beginSession(nonNullableAny(), nonNullableAny())
    }

    fun thenSessionIsStarted(email: String) {
        val expectedUser = User(email)
        verify(instance).beginSession(anyString().nonNullable, eq(expectedUser).nonNullable)
    }
}
