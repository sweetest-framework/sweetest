package com.mysugr.android.testing.example.dependency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.coroutine.DispatcherProvider
import com.mysugr.android.testing.example.coroutine.DefaultDispatcherProvider
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.net.DummyBackendGateway
import com.mysugr.android.testing.example.state.DummySessionStore
import com.mysugr.android.testing.example.state.SessionStore

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