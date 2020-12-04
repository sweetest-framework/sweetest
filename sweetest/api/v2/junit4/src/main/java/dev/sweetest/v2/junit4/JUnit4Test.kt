package dev.sweetest.v2.junit4

import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.finishWorkflow
import dev.sweetest.internal.workflow.proceedWorkflow
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
