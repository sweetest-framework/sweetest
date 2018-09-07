package com.mysugr.android.testing.example.app


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.mysugr.android.testing.example.app.dependency.DependencyFramework
import com.mysugr.android.testing.example.app.view.ILoginViewModel
import com.mysugr.android.testing.example.app.view.LoginViewModel.State
import com.mysugr.android.testing.example.app.view.LoginViewModel.State.*
import com.mysugr.android.testing.example.app.view.StateListener
import org.junit.After
import org.junit.Rule
import org.junit.Test


class LoginActivityTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(LoginActivity::class.java, true, false)

    @After
    fun after() {
        DependencyFramework.reset()
    }

    @Test
    fun stateChangeTest() {

        val mock = object : ILoginViewModel {
            override var stateListener: StateListener = {}
            override var state: State = LoggedOut()
            override fun attemptLogin(email: String, password: String) {}
            override fun logout() {}
        }

        fun setState(state: State) {
            mock.state = state
            mock.stateListener(state)
        }

        DependencyFramework.loginViewModel = mock

        activityTestRule.launchActivity(null)

        setState(LoggedOut())
        onView(withId(R.id.login_progress))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.login_form))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        setState(Busy())
        onView(withId(R.id.login_progress))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.login_form))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))

        setState(Error())
        onView(withId(R.id.login_progress))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.login_form))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        setState(LoggedIn(true))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.logout_button)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

}
