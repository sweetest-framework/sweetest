package com.mysugr.android.testing.example.view

import com.mysugr.android.testing.example.app.R
import kotlin.concurrent.thread

import com.mysugr.android.testing.example.view.LoginViewModel.State.*
import com.mysugr.android.testing.example.auth.AuthManager

typealias StateListener = (LoginViewModel.State) -> Unit

class LoginViewModel(private val authManager: AuthManager) {

    lateinit var stateListener: StateListener

    var state: State = LoggedOut()
        private set(value) {
            field = value
            stateListener(value)
        }

    fun loginOrRegister(email: String, password: String) {

        if (!validateEmail(email)) {
            state = Error(emailError = R.string.error_invalid_email)
            return
        }

        if (!validatePassword(password)) {
            state = Error(passwordError = R. string.error_invalid_password)
            return
        }

        state = Busy()
        thread {
            state = try {
                val result = authManager.loginOrRegister(email, password)
                val isNewUser = result == AuthManager.LoginOrRegisterResult.REGISTERED
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

    fun logout() {
        authManager.logout()
        state = State.LoggedOut()
    }

    sealed class State(val loggedIn: Boolean) {
        class LoggedOut : State(false)
        class Busy : State(false)
        data class Error(val emailError: Int? = null, val passwordError: Int? = null) :
                State(false)
        data class LoggedIn(val isNewUser: Boolean) : State(true)
    }
}
