package com.mysugr.android.testing.example.dependency

import com.mysugr.android.testing.example.app.view.LoginViewModel
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.BackendGateway
import com.mysugr.android.testing.example.net.DummyBackendGateway
import com.mysugr.android.testing.example.state.DummySessionStore
import com.mysugr.android.testing.example.state.SessionStore

object DependencyFramework {

    private var _loginViewModel: LoginViewModel? = null
    var loginViewModel: LoginViewModel
        get() {
            if (_loginViewModel == null) {
                _loginViewModel = LoginViewModel(authManager)
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