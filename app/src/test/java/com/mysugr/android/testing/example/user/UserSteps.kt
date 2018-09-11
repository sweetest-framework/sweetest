package com.mysugr.android.testing.example.user

import com.mysugr.android.testing.example.app.appModuleTestingConfiguration
import com.mysugr.testing.framework.base.BaseSteps
import com.mysugr.testing.framework.context.TestContext

class UserSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    companion object {
        const val EMAIL = "test@test.com"
        const val EMAIL_INEXISTENT = "other@test.com"
        const val PASSWORD = "supersecure"
        const val PASSWORD_WRONG = "supercesure"
    }

    var exists = true
    var correctPassword = true

    val email get() = if (exists) EMAIL else EMAIL_INEXISTENT
    val password get() = if (correctPassword) PASSWORD else PASSWORD_WRONG

}
