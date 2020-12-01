package dev.sweetest.api.v2.framework

import com.mysugr.sweetest.usecases.resetEnvironmentFully
import org.junit.After

open class BaseTest {

    @After
    open fun setUp() {
        resetEnvironmentFully()
    }
}
