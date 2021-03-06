package com.mysugr.android.testing.example.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mysugr.android.testing.example.app.R
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.coroutine.DispatcherProvider
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authManager: AuthManager,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    val state: Flow<State>
        get() = stateChannel.asFlow()

    private val stateChannel = ConflatedBroadcastChannel<State>()

    fun loginOrRegister(email: String, password: String) {
        if (!validateEmail(email)) {
            stateChannel.offer(State.Error(emailError = R.string.error_invalid_email))
            return
        }

        if (!validatePassword(password)) {
            stateChannel.offer(State.Error(passwordError = R.string.error_invalid_password))
            return
        }

        stateChannel.offer(State.Busy)

        viewModelScope.launch(dispatcherProvider.io) {
            val newState = try {
                val result = authManager.loginOrRegister(email, password)
                val isNewUser = (result == AuthManager.LoginOrRegisterResult.REGISTERED)
                State.LoggedIn(isNewUser)
            } catch (exception: AuthManager.WrongPasswordException) {
                State.Error(passwordError = R.string.error_incorrect_password)
            }
            stateChannel.offer(newState)
        }
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun validateEmail(email: String): Boolean {
        return Regex("^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})\$")
            .matches(email)
    }

    fun logout() {
        authManager.logout()
        stateChannel.offer(State.LoggedOut)
    }

    sealed class State(val loggedIn: Boolean) {
        object LoggedOut : State(false)
        object Busy : State(false)
        data class Error(val emailError: Int? = null, val passwordError: Int? = null) : State(false)
        data class LoggedIn(val isNewUser: Boolean) : State(true)
    }
}
