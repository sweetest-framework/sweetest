package dev.sweetest.api.v2.cucumber

import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.usecases.proceedWorkflow
import cucumber.api.java.After
import cucumber.api.java.Before
import dev.sweetest.api.v2.TestContext

/**
 * Use this steps class to wire Cucumber with sweetest by adding `dev.sweetest.api.v2.cucumber` to the list
 * of glue code packages.
 */
class HooksSteps(private val testContext: TestContext) {

    @Before(order = HookOrder.INITIALIZE_FRAMEWORK)
    fun initializeFramework() {
        // nothing to do
    }

    @Before(order = HookOrder.INITIALIZE_STEPS)
    fun initializeSteps() {
        proceedWorkflow(testContext.workflow, WorkflowStep.INITIALIZE_STEPS)
    }

    @Before(order = HookOrder.INITIALIZE_DEPENDENCIES)
    fun initializeDependencies() {
        proceedWorkflow(testContext.workflow, WorkflowStep.INITIALIZE_DEPENDENCIES)
    }

    @Before(order = HookOrder.BEFORE_SET_UP)
    fun beforeSetUp() {
        proceedWorkflow(testContext.workflow, WorkflowStep.BEFORE_SET_UP)
    }

    @Before(order = HookOrder.SET_UP)
    fun setUp() {
        proceedWorkflow(testContext.workflow, WorkflowStep.SET_UP)
    }

    @Before(order = HookOrder.AFTER_SET_UP)
    fun afterSetUp() {
        proceedWorkflow(testContext.workflow, WorkflowStep.AFTER_SET_UP)
    }

    @After(order = HookOrder.TEAR_DOWN)
    fun tearDown() {
        proceedWorkflow(testContext.workflow, WorkflowStep.TEAR_DOWN)
    }

    @After(order = HookOrder.AFTER_TEAR_DOWN)
    fun afterTearDown() {
        proceedWorkflow(testContext.workflow, WorkflowStep.AFTER_TEAR_DOWN)
    }
}
