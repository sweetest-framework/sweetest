package com.mysugr.android.testing.v2.example.state

import com.mysugr.android.testing.example.state.SessionStore
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.util.nonNullable
import com.mysugr.android.testing.util.nonNullableAny
import dev.sweetest.api.v2.Steps
import dev.sweetest.api.v2.TestContext
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SessionStoreSteps(testContext: TestContext) : Steps(testContext) {

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
