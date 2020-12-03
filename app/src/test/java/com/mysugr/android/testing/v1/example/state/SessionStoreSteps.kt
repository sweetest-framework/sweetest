package com.mysugr.android.testing.v1.example.state

import com.mysugr.android.testing.example.state.SessionStore
import com.mysugr.android.testing.example.user.User
import com.mysugr.android.testing.util.nonNullable
import com.mysugr.android.testing.util.nonNullableAny
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.dependency
import dev.sweetest.internal.TestContext
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
