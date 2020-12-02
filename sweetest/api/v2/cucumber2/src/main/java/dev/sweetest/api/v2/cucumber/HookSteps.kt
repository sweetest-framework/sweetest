package dev.sweetest.api.v2.cucumber

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.usecases.proceedWorkflow
import cucumber.api.java.After
import cucumber.api.java.Before

/**
 * Use this steps class to wire Cucumber with sweetest by adding `dev.sweetest.api.v2.cucumber` to the list
 * of glue code packages.
 */
class HookSteps(private val testContext: TestContext) {

    @Before(order = HookOrder.INITIALIZE_FRAMEWORK)
    fun initializeFramework() {
        // nothing to do
    }

    @Before(order = HookOrder.INITIALIZE_STEPS)
    fun initializeSteps() {
        proceedWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_STEPS)
    }

    @Before(order = HookOrder.INITIALIZE_DEPENDENCIES)
    fun initializeDependencies() {
        proceedWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_DEPENDENCIES)
    }

    @Before(order = HookOrder.BEFORE_SET_UP)
    fun beforeSetUp() {
        proceedWorkflow(testContext[WorkflowTestContext], WorkflowStep.BEFORE_SET_UP)
    }

    @Before(order = HookOrder.SET_UP)
    fun setUp() {
        proceedWorkflow(testContext[WorkflowTestContext], WorkflowStep.SET_UP)
    }

    @Before(order = HookOrder.AFTER_SET_UP)
    fun afterSetUp() {
        proceedWorkflow(testContext[WorkflowTestContext], WorkflowStep.AFTER_SET_UP)
    }

    @After(order = HookOrder.TEAR_DOWN)
    fun tearDown() {
        proceedWorkflow(testContext[WorkflowTestContext], WorkflowStep.TEAR_DOWN)
    }

    @After(order = HookOrder.AFTER_TEAR_DOWN)
    fun afterTearDown() {
        proceedWorkflow(testContext[WorkflowTestContext], WorkflowStep.AFTER_TEAR_DOWN)
    }
}
