package dev.sweetest.v2.junit4

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import dev.sweetest.v2.BaseTest
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
