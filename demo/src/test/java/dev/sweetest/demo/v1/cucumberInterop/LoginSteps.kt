package dev.sweetest.demo.v1.cucumberInterop

import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.steps
import cucumber.api.java.Before
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import dev.sweetest.demo.R
import dev.sweetest.demo.v1.example.auth.AuthManagerSteps
import dev.sweetest.demo.v1.example.net.BackendFakeSteps
import dev.sweetest.demo.v1.example.state.SessionStoreSteps
import dev.sweetest.demo.v1.example.view.LoginViewModelSteps
import dev.sweetest.demo.view.LoginViewModel

class LoginSteps : BaseSteps() {

    override fun configure() = super.configure()
        .requireSteps<AuthManagerSteps>()
        .onSetUp {
            loginViewModel.whenInitialized()
        }

    val backend by steps<BackendFakeSteps>()

    private val loginViewModel by steps<LoginViewModelSteps>()
    private val sessionStore by steps<SessionStoreSteps>()

    @Before("@login-integration")
    fun dummy() {
    }

    @When("^trying to login or register with email address \"([^\"]*)\" and password \"([^\"]*)\"$")
    fun whenLoginOrRegister(email: String, password: String) {
        loginViewModel.whenLoggingIn(email, password)
    }

    @Then("^the user \"([^\"]*)\" is logged in as an existing user$")
    fun thenLoggedInExistingUser(email: String) {
        loginViewModel.thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = false))
        loginViewModel.thenStateIsLoggedInAsExistingUser()
        sessionStore.thenSessionIsStarted(email)
    }

    @Then("^the user \"([^\"]*)\" is logged in as a new user$")
    fun thenLoggedInNewUser(email: String) {
        loginViewModel.thenLastStateIs(LoginViewModel.State.LoggedIn(isNewUser = true))
        loginViewModel.thenStateIsLoggedInAsNewUser()
        sessionStore.thenSessionIsStarted(email)
    }

    @Then("^the user can't enter the app$")
    fun thenLoginFailed() {
        loginViewModel.thenStateIsNotLoggedIn()
    }

    @Then("^a wrong email address is detected$")
    fun thenLoginFailedIncorrectEmail() {
        loginViewModel.thenLastStateIs(LoginViewModel.State.Error(emailError = R.string.error_invalid_email))
    }
}
