package com.mysugr.android.testing.example.feature.auth

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.BackendGatewayMockSteps
import com.mysugr.android.testing.example.net.FakeBackendUser
import com.mysugr.android.testing.example.state.SessionStoreSteps
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.example.view.LoginViewModelSteps
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.context.TestContext
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import kotlinx.coroutines.test.TestCoroutineScope

class LoginIntegrationTestSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .requireReal<LoginViewModel>()
        .requireReal<AuthManager>()
        .onSetUp {
            loginViewModel.scope = scope
            loginViewModel.whenInitialized()
        }

    private val loginViewModel by steps<LoginViewModelSteps>()
    private val sessionStore by steps<SessionStoreSteps>()
    private val backendGateway by steps<BackendGatewayMockSteps>()
    private val user by steps<UserSteps>()

    private val scope = TestCoroutineScope()

    @Before("@login-integration")
    fun dummy() {
    } // forces instantiation of this class when run with Cucumber

    @Given("^there is a user existing with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun givenRegisteredUser(email: String, password: String) {
        backendGateway.givenUsers(FakeBackendUser(email, password))
    }

    @When("^trying to login or register with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun whenLoginOrRegister(email: String, password: String) {
        loginViewModel.whenLoggingIn(email, password)
    }

    @Then("^a new user with email \"([^\"]*)\" and password \"([^\"]*)\" is registered$")
    fun thenRegisteredNewUser(email: String, password: String) {
        loginViewModel.whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        backendGateway.thenRegistered(email, password)
    }

    @Then("^the user \"([^\"]*)\" is logged in as an existing user$")
    fun thenLoggedInExistingUser(email: String) {
        val authToken = backendGateway.getUser(email).authToken
        loginViewModel.whenWaitForState(LoginViewModel.State.LoggedIn::class.java)
        loginViewModel.thenStateIsLoggedInAsExistingUser()
        sessionStore.thenSessionIsStarted(email, authToken)
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
