package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

class LoginTest : BaseJUnitTest(appModuleTestingConfiguration) {

    private val sut by steps<LoginSteps>()
    private val scope = TestCoroutineScope()

    override fun configure() = super.configure()
        .offerMockRequired { scope }
        .onSetUp { sut.scope = scope }

    @Test
    fun `Logging in checks email at backend`() {
        sut {
            givenExistingUser(email = EXISTING_EMAIL, password = EXISTING_PASSWORD, authToken = EXISTING_AUTH_TOKEN)
            whenLoggingIn(email = EXISTING_EMAIL, password = EXISTING_PASSWORD)
            thenEmailWasCheckedAtBackend(EXISTING_EMAIL)
        }
    }

    companion object {
        const val EXISTING_EMAIL = "existing@test.com"
        const val EXISTING_PASSWORD = "supersecure1"
        const val EXISTING_AUTH_TOKEN = "auth_token"
    }
}
