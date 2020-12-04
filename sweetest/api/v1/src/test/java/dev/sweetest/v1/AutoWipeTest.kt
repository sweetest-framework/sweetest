package dev.sweetest.v1

import dev.sweetest.internal.environment.resetEnvironmentFully
import org.junit.After

open class AutoWipeTest {

    @After
    fun after() {
        resetEnvironmentFully()
    }
}
