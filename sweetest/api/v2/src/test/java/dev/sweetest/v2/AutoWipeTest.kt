package dev.sweetest.v2

import com.mysugr.sweetest.usecases.resetEnvironmentFully
import org.junit.After

open class AutoWipeTest {

    @After
    fun after() {
        resetEnvironmentFully()
    }
}
