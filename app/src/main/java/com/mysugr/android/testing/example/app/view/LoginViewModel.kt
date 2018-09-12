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
    private var _state: State = State.LoggedOut()

    override var state: State
        get() = _state
        private set(value) {
            _state = value
            stateListener(_state)
        }

    override fun loginOrRegister(email: String, password: String) {
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
