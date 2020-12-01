package dev.sweetest.api.v2.framework.base

import dev.sweetest.api.v2.framework.build.TestBuilder
import dev.sweetest.api.v2.framework.context.TestContext
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import org.junit.After
import org.junit.Before

abstract class JUnit4Test : CommonBase(TestContext()) {

    open fun configure() = TestBuilder(testContext)

    init {
        @Suppress("LeakingThis")
        configure().freeze()
    }

    @Before
    fun junitBefore() {
        proceedWorkflow(testContext.workflow)
    }

    @After
    fun junitAfter() {
        finishWorkflow(testContext.workflow)
    }
}
