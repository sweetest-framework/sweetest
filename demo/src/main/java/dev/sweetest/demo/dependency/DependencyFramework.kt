package dev.sweetest.demo.dependency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.sweetest.demo.view.LoginViewModel
import dev.sweetest.demo.auth.AuthManager
import dev.sweetest.demo.coroutine.DispatcherProvider
import dev.sweetest.demo.coroutine.DefaultDispatcherProvider
import dev.sweetest.demo.net.BackendGateway
import dev.sweetest.demo.net.DummyBackendGateway
import dev.sweetest.demo.state.DummySessionStore
import dev.sweetest.demo.state.SessionStore

object DependencyFramework {

    var _dispatcherProvider: DispatcherProvider? = null
    var dispatcherProvider: DispatcherProvider
        get() {
            if (_dispatcherProvider == null) {
                _dispatcherProvider = DefaultDispatcherProvider()
            }
            return _dispatcherProvider!!
        }
        set(value) {
            _dispatcherProvider = value
        }

    val viewModelProviderFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return when (modelClass) {
                LoginViewModel::class.java -> loginViewModel
                else -> error("$modelClass not supported by factory")
            } as T
        }
    }
    private var _loginViewModel: LoginViewModel? = null
    var loginViewModel: LoginViewModel
        get() {
            if (_loginViewModel == null) {
                _loginViewModel = LoginViewModel(authManager, dispatcherProvider)
            }
            return _loginViewModel!!
        }
        set(value) {
            _loginViewModel = value
        }

    private var _authManager: AuthManager? = null
    var authManager: AuthManager
        get() {
            if (_authManager == null) {
                _authManager = AuthManager(backendGateway, sessionStore)
            }
            return _authManager!!
        }
        set(value) {
            _authManager = value
        }

    private var _backendGateway: BackendGateway? = null
    var backendGateway: BackendGateway
        get() {
            if (_backendGateway == null) {
                _backendGateway = DummyBackendGateway()
            }
            return _backendGateway!!
        }
        set(value) {
            _backendGateway = value
        }

    private var _sessionStore: SessionStore? = null
    var sessionStore: SessionStore
        get() {
            if (_sessionStore == null) {
                _sessionStore = DummySessionStore()
            }
            return _sessionStore!!
        }
        set(value) {
            _sessionStore = value
        }

    fun reset() {
        _loginViewModel = null
        _authManager = null
        _sessionStore = null
    }
}
