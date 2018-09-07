package com.mysugr.android.testing.example.app

import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.mysugr.android.testing.example.dependency.DependencyFramework

import com.mysugr.android.testing.example.app.view.LoginViewModel
import com.mysugr.android.testing.example.app.view.LoginViewModel.State
import com.mysugr.android.testing.example.app.view.LoginViewModel.State.*

import kotlinx.android.synthetic.main.activity_login.*
import java.util.logging.Level
import java.util.logging.Logger

class LoginActivity : AppCompatActivity() {

    private var logger = Logger.getLogger(this::class.java.simpleName)
    private var viewModel = DependencyFramework.loginViewModel

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
            viewModel.attemptLogin(email.text.toString(), password.text.toString())
        }
        logout_button.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun onStateChange(state: LoginViewModel.State) {
        logger.log(Level.INFO, "State: $state")
        updateView(state)
        if (state is LoggedIn) {
            //showWelcomeDialog(state)
        }
    }

    fun updateView(state: State) {
        login_progress.visibility = if (state is Busy) View.VISIBLE else View.GONE
        login_form.visibility = if (state is LoggedOut || state is Error) View.VISIBLE else
            View.GONE
        logout_button.visibility = if (state.loggedIn) View.VISIBLE else View.GONE
        email.error = (state as? Error)?.emailError?.let { this.resources.getString(it) }
        password.error = (state as? Error)?.passwordError?.let { this.resources.getString(it) }
        if (state is LoggedIn) {
            message.setText(if (state.newUser) R.string.login_new_user else
                R.string.login_existing_user)
        } else {
            message.text = ""
        }
    }

    private fun showWelcomeDialog(state: LoggedIn) {

        val title = if (state.newUser) "Your are now registered" else "Your are logged in"
        val message = if (state.newUser) "Welcome to our app!" else "Welcome back!"
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { _: DialogInterface?, _: Int -> }
                .show()
    }

}
