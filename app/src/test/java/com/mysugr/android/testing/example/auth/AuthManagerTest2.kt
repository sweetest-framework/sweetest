package com.mysugr.android.testing.example.auth

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.net.BackendFakeUser.Companion.USER_A
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import org.junit.Test

class AuthManagerTest2 : BaseJUnitTest(appModuleTestingConfiguration) {

    private val sut by steps<AuthManagerSteps2>()

    @Test
    fun `Login as existing user, checks for existing email`() = sut {
        backend.givenExistingUser(USER_A)
        whenPassingCredentials(USER_A.email, USER_A.password)
        backend.thenEmailWasChecked(USER_A.email)
    }

    @Test
    fun `Login as existing user, attempts login`() = sut {
        backend.givenExistingUser(USER_A)
        whenPassingCredentials(USER_A.email, USER_A.password)
        backend.thenLoginWasAttempted(USER_A.email, USER_A.password)
    }

    @Test
    fun `Login as existing user, starts session`() = sut {
        backend.givenExistingUser(USER_A)
        whenPassingCredentials(USER_A.email, USER_A.password)
        thenSessionWasStarted()
    }
}
