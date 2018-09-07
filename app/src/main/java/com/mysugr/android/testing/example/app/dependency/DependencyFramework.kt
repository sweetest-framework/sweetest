package com.mysugr.android.testing.example.app.dependency

import com.mysugr.android.testing.example.app.view.ILoginViewModel
import com.mysugr.android.testing.example.app.view.LoginViewModel

object DependencyFramework {

    private var _loginViewModel: ILoginViewModel? = null

    var loginViewModel: ILoginViewModel
        get() {
            if (_loginViewModel == null) {
                _loginViewModel = LoginViewModel()
            }
            return _loginViewModel!!
        }
        set(value) {
            _loginViewModel = value
        }

    fun reset() {
        _loginViewModel = null
    }

}