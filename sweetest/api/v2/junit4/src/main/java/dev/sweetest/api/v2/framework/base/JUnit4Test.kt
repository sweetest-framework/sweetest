package dev.sweetest.api.v2.framework.base

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import dev.sweetest.api.v2.BaseTest
import org.junit.After
import org.junit.Before

abstract class JUnit4Test : BaseTest() {

    @Before
    fun junitBefore() {
        proceedWorkflow(testContext[WorkflowTestContext])
    }

    @After
    fun junitAfter() {
        finishWorkflow(testContext[WorkflowTestContext])
    }
}
