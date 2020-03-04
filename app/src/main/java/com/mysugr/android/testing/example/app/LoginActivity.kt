package com.mysugr.android.testing.example.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mysugr.android.testing.example.app.databinding.ActivityLoginBinding
import com.mysugr.android.testing.example.dependency.DependencyFramework
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.example.view.LoginViewModel.State
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
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, DependencyFramework.viewModelProviderFactory)[LoginViewModel::class.java]
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachViewModel()
    }

    private fun attachViewModel() {
        viewModel.state
            .onEach { onStateChange(it) }
            .launchIn(lifecycleScope)

        binding.signInButton.setOnClickListener {
            viewModel.loginOrRegister(binding.email.text.toString(), binding.password.text.toString())
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun onStateChange(state: State) {
        logger.log(Level.INFO, "State: $state")
        updateView(state)
    }

    private fun updateView(state: State) {
        with(binding) {
            loginProgress.visibility = if (state is State.Busy) View.VISIBLE else View.GONE
            loginForm.visibility = if (state is State.LoggedOut || state is Error) View.VISIBLE else View.GONE
            logoutButton.visibility = if (state.loggedIn) View.VISIBLE else View.GONE
            email.error = (state as? State.Error)?.emailError?.let { resources.getString(it) }
            password.error = (state as? State.Error)?.passwordError?.let { resources.getString(it) }
            if (state is State.LoggedIn) {
                message.setText(if (state.isNewUser) R.string.login_new_user else R.string.login_existing_user)
            } else {
                message.text = ""
            }
        }
    }
}
