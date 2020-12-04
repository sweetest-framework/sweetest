package com.mysugr.sweetest.framework.cucumber

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import com.mysugr.sweetest.usecases.startEnvironment
import cucumber.api.java.After
import cucumber.api.java.Before

/**
 * Use this steps class to wire Cucumber with sweetest by adding `com.mysugr.sweetest.framework.cucumber` to the list
 * of glue code packages.
 */
class HookSteps {

    private lateinit var workflowTestContext: WorkflowTestContext

    @Before(order = HookOrder.INITIALIZE_FRAMEWORK)
    fun initializeFramework() {
        val testContext = startEnvironment()
        workflowTestContext = testContext[WorkflowTestContext]
    }

    @Before(order = HookOrder.INITIALIZE_STEPS)
    fun initializeSteps() {
        proceedWorkflow(workflowTestContext, WorkflowStep.INITIALIZE_STEPS)
    }

    @Before(order = HookOrder.INITIALIZE_DEPENDENCIES)
    fun initializeDependencies() {
        proceedWorkflow(workflowTestContext, WorkflowStep.INITIALIZE_DEPENDENCIES)
    }

    @Before(order = HookOrder.BEFORE_SET_UP)
    fun beforeSetUp() {
        proceedWorkflow(workflowTestContext, WorkflowStep.BEFORE_SET_UP)
    }

    @Before(order = HookOrder.SET_UP)
    fun setUp() {
        proceedWorkflow(workflowTestContext, WorkflowStep.SET_UP)
    }

    @Before(order = HookOrder.AFTER_SET_UP)
    fun afterSetUp() {
        proceedWorkflow(workflowTestContext, WorkflowStep.AFTER_SET_UP)
    }

    @After(order = HookOrder.TEAR_DOWN)
    fun tearDown() {
        proceedWorkflow(workflowTestContext, WorkflowStep.TEAR_DOWN)
    }

    @After(order = HookOrder.AFTER_TEAR_DOWN)
    fun afterTearDown() {
        proceedWorkflow(workflowTestContext, WorkflowStep.AFTER_TEAR_DOWN)
    }

    @After(order = HookOrder.DONE)
    fun done() {
        finishWorkflow(workflowTestContext)
    }
}
