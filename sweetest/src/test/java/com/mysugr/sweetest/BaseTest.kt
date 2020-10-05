package com.mysugr.sweetest

import com.mysugr.sweetest.framework.environment.TestEnvironment
import org.junit.After

open class BaseTest {

    @After
    open fun setUp() {
        TestEnvironment.fullReset()
    }
}
