package com.mysugr.android.testing.example.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mysugr.android.testing.example.dependency.DependencyFramework
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.example.view.LoginViewModel.State
import com.mysugr.android.testing.example.view.LoginViewModel.State.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.logging.Level
import java.util.logging.Logger

class LoginActivity : AppCompatActivity() {

    private val logger = Logger.getLogger(this::class.java.simpleName)
    private val viewModel = DependencyFramework.loginViewModel

    init {
        viewModel.stateListener = { this.runOnUiThread { onStateChange(it) } }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        wireViewModel()
    }

    private fun wireViewModel() {
        sign_in_button.setOnClickListener {
            viewModel.loginOrRegister(email.text.toString(), password.text.toString())
        }
        logout_button.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun onStateChange(state: LoginViewModel.State) {
        logger.log(Level.INFO, "State: $state")
        updateView(state)
    }

    private fun updateView(state: State) {
        login_progress.visibility = if (state is Busy) View.VISIBLE else View.GONE
        login_form.visibility = if (state is LoggedOut || state is Error) View.VISIBLE else
            View.GONE
        logout_button.visibility = if (state.loggedIn) View.VISIBLE else View.GONE
        email.error = (state as? Error)?.emailError?.let { this.resources.getString(it) }
        password.error = (state as? Error)?.passwordError?.let { this.resources.getString(it) }
        if (state is LoggedIn) {
            message.setText(if (state.isNewUser) R.string.login_new_user else
                R.string.login_existing_user)
        } else {
            message.text = ""
        }
    }
}
