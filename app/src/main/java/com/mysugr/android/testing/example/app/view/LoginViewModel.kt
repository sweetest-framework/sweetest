package com.mysugr.android.testing.example.app.view

import com.mysugr.android.testing.example.app.R
import kotlin.concurrent.thread

import com.mysugr.android.testing.example.app.view.LoginViewModel.State.*
import com.mysugr.android.testing.example.auth.AuthManager

typealias StateListener = (LoginViewModel.State) -> Unit

interface ILoginViewModel {
    var stateListener: StateListener
    val state: LoginViewModel.State
    fun loginOrRegister(email: String, password: String)
    fun logout()
}

class LoginViewModel(private val authManager: AuthManager) : ILoginViewModel {

    override lateinit var stateListener: StateListener

    override var state: State = LoggedOut()
        private set(value) {
            field = value
            stateListener(value)
        }

    override fun loginOrRegister(email: String, password: String) {

        if (!validateEmail(email)) {
            state = Error(emailError = R.string.error_invalid_email)
            return
        }

        if (!validatePassword(password)) {
            state = Error(passwordError = R. string.error_invalid_password)
            return
        }

        state = State.Busy()
        thread {
            state = try {
                val result = authManager.loginOrRegister(email, password)
                val isNewUser = result == AuthManager.LoginResult.REGISTERED
                LoggedIn(isNewUser)
            } catch (exception: AuthManager.WrongPasswordException) {
                Error(passwordError = R.string.error_incorrect_password)
            }
        }

    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun validateEmail(email: String): Boolean {
        return Regex("^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})\$").matches(email)
    }

    override fun logout() {
        state = State.LoggedOut()
        authManager.logout()
    }

    sealed class State(val loggedIn: Boolean) {
        class LoggedOut : State(false)
        class Busy : State(false)
        data class Error(val emailError: Int? = null, val passwordError: Int? = null) :
                State(false)
        data class LoggedIn(val isNewUser: Boolean) : State(true)
    }

}
