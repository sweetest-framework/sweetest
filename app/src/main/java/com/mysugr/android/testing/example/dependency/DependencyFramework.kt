package com.mysugr.android.testing.example.dependency

import com.mysugr.android.testing.example.app.view.ILoginViewModel
import com.mysugr.android.testing.example.app.view.LoginViewModel
import com.mysugr.android.testing.example.auth.AuthManager
import com.mysugr.android.testing.example.net.AuthGateway
import com.mysugr.android.testing.example.net.UserGateway
import com.mysugr.android.testing.example.state.SessionStore

object DependencyFramework {

    private var _loginViewModel: ILoginViewModel? = null
    var loginViewModel: ILoginViewModel
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
                _authManager = AuthManager(authGateway, userGateway, sessionStore)
            }
            return _authManager!!
        }
        set(value) {
            _authManager = value
        }

    private var _authGateway: AuthGateway? = null
    var authGateway: AuthGateway
        get() {
            if (_authGateway == null) {
                _authGateway = AuthGateway()
            }
            return _authGateway!!
        }
        set(value) {
            _authGateway = value
        }

    private var _userGateway: UserGateway? = null
    var userGateway: UserGateway
        get() {
            if (_userGateway == null) {
                _userGateway = UserGateway()
            }
            return _userGateway!!
        }
        set(value) {
            _userGateway = value
        }

    private var _sessionStore: SessionStore? = null
    var sessionStore: SessionStore
        get() {
            if (_sessionStore == null) {
                _sessionStore = SessionStore()
            }
            return _sessionStore!!
        }
        set(value) {
            _sessionStore = value
        }

    fun reset() {
        _loginViewModel = null
        _authManager = null
        _userGateway = null
        _sessionStore = null
    }

}