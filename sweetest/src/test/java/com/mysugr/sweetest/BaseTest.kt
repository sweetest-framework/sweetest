package com.mysugr.sweetest

import com.mysugr.sweetest.framework.environment.TestEnvironment
import org.junit.Before

open class BaseTest {

    @Before
    open fun setUp() {
        TestEnvironment.fullReset()
    }
}