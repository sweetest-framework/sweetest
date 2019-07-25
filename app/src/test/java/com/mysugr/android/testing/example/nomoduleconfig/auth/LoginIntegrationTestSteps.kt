package com.mysugr.android.testing.example.nomoduleconfig.auth

import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.nomoduleconfig.net.BackendGatewaySteps
import com.mysugr.android.testing.example.nomoduleconfig.state.SessionStoreSteps
import com.mysugr.android.testing.example.nomoduleconfig.view.LoginViewModelSteps
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import cucumber.api.java.Before
import cucumber.api.java.en.Then
import cucumber.api.java.en.When

class LoginIntegrationTestSteps(testContext: TestContext) : BaseSteps(testContext) {

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireReal<AuthManager>()
        .onSetUp {
            loginViewModel.givenStateListenerConnected()
        }

    private val loginViewModel by steps<LoginViewModelSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewaySteps>()
    private val user by steps<UserSteps>()

    @Before("@login-integration")
    fun dummy() {
    } // forces instantiation of this class when run with Cucumber

    @When("^trying to login or register with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun whenLoginOrRegister(email: String, password: String) {
        user.email = email
        user.password = password
        loginViewModel.whenLoggingIn()
    }

    @When("^trying to login or register with email address \"([^\"]*)\"$")
    fun whenLoginOrRegister(email: String) {
        user.email = email
        loginViewModel.whenLoggingIn()
    }

    @Then("^a new user with email \"([^\"]*)\" and password \"([^\"]*)\" is registered$")
    fun thenRegisteredNewUser(email: String, password: String) {
        loginViewModel.whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        backendGateway.thenRegistered(email, password)
    }

    @Then("^the user \"([^\"]*)\" is logged in as an existing user$")
    fun thenLoggedInExistingUser(email: String) {
        loginViewModel.whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        loginViewModel.thenStateIsLoggedInAsExistingUser()
        sessionStore.thenSessionIsStarted(email)
        backendGateway.thenLoginAttempted(email)
    }

    @Then("^the user \"([^\"]*)\" is logged in as a new user$")
    fun thenLoggedInNewUser(email: String) {
        loginViewModel.whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        loginViewModel.thenStateIsLoggedInAsNewUser()
        sessionStore.thenSessionIsStarted(email)
    }

    @Then("^the user can't enter the app$")
    fun thenLoginFailed() {
        loginViewModel.whenWaitForStateNot(LoginViewModel.State.LoggedIn::class.java)
        loginViewModel.thenStateIsNotLoggedIn()
    }
}
