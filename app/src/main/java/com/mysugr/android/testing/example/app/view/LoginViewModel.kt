package com.mysugr.android.testing.example.app.view

typealias StateListener = (LoginViewModel.State) -> Unit

interface ILoginViewModel {
    var stateListener: StateListener
    val state: LoginViewModel.State
    fun attemptLogin(email: String, password: String)
    fun logout()
}

class LoginViewModel : ILoginViewModel {

    override lateinit var stateListener: StateListener
    private var _state: State = State.LoggedOut()

    override var state: State
        get() = _state
        private set(value) {
            _state = value
            stateListener(_state)
        }

    override fun attemptLogin(email: String, password: String) {
        state = State.Busy()
        //TODO()
    }

    override fun logout() {
        state = State.LoggedOut()
        TODO()
    }

    sealed class State(val loggedIn: Boolean) {
        class LoggedOut : State(false)
        class Busy : State(false)
        data class Error(val emailError: Int? = null, val passwordError: Int? = null) :
                State(false)
        data class LoggedIn(val newUser: Boolean) : State(true)
    }

}
