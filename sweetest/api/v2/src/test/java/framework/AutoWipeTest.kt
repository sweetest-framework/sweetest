package framework

import com.mysugr.sweetest.usecases.resetEnvironmentFully
import org.junit.After

open class AutoWipeTest {

    @After
    open fun after() {
        resetEnvironmentFully()
    }
}
