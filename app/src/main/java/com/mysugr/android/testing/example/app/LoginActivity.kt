package com.mysugr.android.testing.example.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mysugr.android.testing.example.dependency.DependencyFramework
import com.mysugr.android.testing.example.view.LoginViewModel.State
import kotlinx.android.synthetic.main.activity_login.email
import kotlinx.android.synthetic.main.activity_login.login_form
import kotlinx.android.synthetic.main.activity_login.login_progress
import kotlinx.android.synthetic.main.activity_login.logout_button
import kotlinx.android.synthetic.main.activity_login.message
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.sign_in_button
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.logging.Level
import java.util.logging.Logger

@FlowPreview
@ExperimentalCoroutinesApi
class LoginActivity : AppCompatActivity() {

    private val logger = Logger.getLogger(this::class.java.simpleName)
    private val viewModel = DependencyFramework.loginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        wireViewModel()
    }

    private fun wireViewModel() {
        viewModel.state
            .onEach { onStateChange(it) }
            .launchIn(lifecycleScope)

        sign_in_button.setOnClickListener {
            viewModel.loginOrRegister(email.text.toString(), password.text.toString())
        }

        logout_button.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun onStateChange(state: State) {
        logger.log(Level.INFO, "State: $state")
        updateView(state)
    }

    private fun updateView(state: State) {
        login_progress.visibility = if (state is State.Busy) View.VISIBLE else View.GONE
        login_form.visibility = if (state is State.LoggedOut || state is Error) View.VISIBLE else View.GONE
        logout_button.visibility = if (state.loggedIn) View.VISIBLE else View.GONE
        email.error = (state as? State.Error)?.emailError?.let { this.resources.getString(it) }
        password.error = (state as? State.Error)?.passwordError?.let { this.resources.getString(it) }
        if (state is State.LoggedIn) {
            message.setText(if (state.isNewUser) R.string.login_new_user else R.string.login_existing_user)
        } else {
            message.text = ""
        }
    }
}
