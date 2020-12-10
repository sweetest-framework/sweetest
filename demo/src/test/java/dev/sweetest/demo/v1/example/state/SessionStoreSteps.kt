package dev.sweetest.demo.v1.example.state

import dev.sweetest.demo.state.SessionStore
import dev.sweetest.demo.user.User
import dev.sweetest.demo.util.nonNullable
import dev.sweetest.demo.util.nonNullableAny
import dev.sweetest.v1.BaseSteps
import dev.sweetest.internal.TestContext
import dev.sweetest.v1.dependency
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SessionStoreSteps(testContext: TestContext) : BaseSteps(testContext) {

    override fun configure() = super.configure()
        .provide<SessionStore> { mock(SessionStore::class.java) }

    private val instance by dependency<SessionStore>()

    fun thenASessionIsStarted() {
        verify(instance).beginSession(nonNullableAny(), nonNullableAny())
    }

    fun thenSessionIsStarted(email: String) {
        val expectedUser = User(email)
        verify(instance).beginSession(anyString().nonNullable, eq(expectedUser).nonNullable)
    }
}
