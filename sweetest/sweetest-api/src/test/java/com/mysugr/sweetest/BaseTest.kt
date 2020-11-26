package com.mysugr.sweetest

import com.mysugr.sweetest.usecases.resetEnvironmentFully
import org.junit.After

open class BaseTest {

    @After
    open fun setUp() {
        resetEnvironmentFully()
    }
}
