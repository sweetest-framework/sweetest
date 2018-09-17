package com.mysugr.android.testing.example.feature.auth

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import java.util.*

class UserSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    @Before("@login-integration")
    fun dummy() {} // forces instantiation of this class when run with Cucumber

    var existingEmail = "test@test.com"
    var nonExistingEmail = "other@test.com"
    var email = existingEmail

    var correctPassword = "supersecure"
    var incorrectPassword = "supercesure"
    var password = correctPassword

    val authToken = UUID.randomUUID().toString()

    fun givenRequestedUserDoesntExist() {
        this.email = nonExistingEmail
    }

    fun givenEnteredPasswordIsIncorrect() {
        this.password = incorrectPassword
    }

    @Given("^there is a user existing with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun givenRegisteredUser(email: String, password: String) {
        existingEmail = email
        correctPassword = password
    }

    fun isUserExisting(email: String) = email == existingEmail

    fun isPasswordCorrect(password: String) = password == correctPassword

}
