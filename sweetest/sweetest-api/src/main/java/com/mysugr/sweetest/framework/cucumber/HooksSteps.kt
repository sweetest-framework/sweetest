package com.mysugr.sweetest.framework.cucumber

import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.usecases.proceedWorkflow
import cucumber.api.java.After
import cucumber.api.java.Before

/**
 * Use this steps class to wire Cucumber with sweetest by adding `com.mysugr.sweetest.framework.cucumber` to the list
 * of glue code packages.
 */
class HooksSteps(private val testContext: TestContext) {

    @Before(order = HookOrder.INITIALIZE_FRAMEWORK)
    fun initializeFramework() {
        // nothing to do
    }

    @Before(order = HookOrder.INITIALIZE_STEPS)
    fun initializeSteps() {
        proceedWorkflow(testContext.workflow, InitializationStep.INITIALIZE_STEPS)
    }

    @Before(order = HookOrder.INITIALIZE_DEPENDENCIES)
    fun initializeDependencies() {
        proceedWorkflow(testContext.workflow, InitializationStep.INITIALIZE_DEPENDENCIES)
    }

    @Before(order = HookOrder.BEFORE_SET_UP)
    fun beforeSetUp() {
        proceedWorkflow(testContext.workflow, InitializationStep.BEFORE_SET_UP)
    }

    @Before(order = HookOrder.SET_UP)
    fun setUp() {
        proceedWorkflow(testContext.workflow, InitializationStep.SET_UP)
    }

    @Before(order = HookOrder.AFTER_SET_UP)
    fun afterSetUp() {
        proceedWorkflow(testContext.workflow, InitializationStep.AFTER_SET_UP)
    }

    @After(order = HookOrder.TEAR_DOWN)
    fun tearDown() {
        proceedWorkflow(testContext.workflow, InitializationStep.TEAR_DOWN)
    }

    @After(order = HookOrder.AFTER_TEAR_DOWN)
    fun afterTearDown() {
        proceedWorkflow(testContext.workflow, InitializationStep.AFTER_TEAR_DOWN)
    }
}
