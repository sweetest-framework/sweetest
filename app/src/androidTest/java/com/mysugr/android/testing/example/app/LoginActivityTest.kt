package com.mysugr.android.testing.example.app

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.mysugr.android.testing.example.dependency.DependencyFramework
import com.mysugr.android.testing.example.view.LoginViewModel
import com.mysugr.android.testing.example.view.LoginViewModel.State
import com.mysugr.android.testing.example.view.LoginViewModel.State.*
import com.mysugr.android.testing.example.view.StateListener
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class LoginActivityTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(LoginActivity::class.java, true, false)

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var stateListener: StateListener

    private var state: State = LoggedOut()
        set(value) {
            field = value
            stateListener(value)
        }

    @Before
    fun before() {

        loginViewModel = mock(LoginViewModel::class.java)

        DependencyFramework.loginViewModel = loginViewModel

        stateListener = let { _ ->
            var waitForListener: StateListener? = null
            `when`(loginViewModel.run { stateListener = any<StateListener>() ?: {} }).then {
                @Suppress("UNCHECKED_CAST")
                waitForListener = it.arguments[0] as StateListener
                Any()
            }
            activityTestRule.launchActivity(null)
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            waitForListener ?: error("Listener not set by Activity")
        }
    }

    @After
    fun after() {
        DependencyFramework.reset()
    }

    @Test
    fun testStateChange() {

        state = LoggedOut()
        onView(withId(R.id.login_progress))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.login_form))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        state = Busy()
        onView(withId(R.id.login_progress))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.login_form))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))

        state = Error()
        onView(withId(R.id.login_progress))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.login_form))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        state = LoggedIn(true)
        onView(withId(R.id.logout_button))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testInteraction() {
        run {
            val expectedEmail = "email@test.com"
            val expectedPassword = "supersecure"
            state = LoggedOut()
            onView(withId(R.id.email))
                    .perform(replaceText(expectedEmail))
            onView(withId(R.id.password))
                    .perform(replaceText(expectedPassword))
            onView(withId(R.id.sign_in_button))
                    .perform(click())
            verify(loginViewModel).loginOrRegister(expectedEmail, expectedPassword)
        }

        run {
            state = LoggedIn(true)
            onView(withId(R.id.logout_button))
                    .perform(click())
            verify(loginViewModel).logout()
        }
    }
}