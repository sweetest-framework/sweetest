package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.net.BackendFakeUser.Companion.USER_A
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

class LoginTest2 : BaseJUnitTest(appModuleTestingConfiguration) {

    private val sut by steps<LoginSteps2>()
    private val scope = TestCoroutineScope()

    override fun configure() = super.configure()
        .offerMockRequired { scope }
        .onSetUp { sut.scope = scope }

    @Test
    fun `Logging in checks email at backend`() = sut {
        backend.givenExistingUser(USER_A)
        whenLoggingIn(USER_A.email, USER_A.password)
        backend.thenEmailWasChecked(USER_A.email)
    }
}
