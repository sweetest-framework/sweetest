package com.mysugr.android.testing.example.feature

import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.cucumber.HookOrder
import com.mysugr.sweetest.framework.flow.InitializationStep
import cucumber.api.java.After
import cucumber.api.java.Before

class AppCucumberHooks(private val testContext: TestContext) {

    @Before(order = HookOrder.INITIALIZE_FRAMEWORK)
    fun initializeFramework() {
        testContext
    }

    @Before(order = HookOrder.INITIALIZE_STEPS)
    fun initializeSteps() {
        testContext.workflowController.proceedTo(InitializationStep.INITIALIZE_STEPS)
    }

    @Before(order = HookOrder.INITIALIZE_DEPENDENCIES)
    fun initializeDependencies() {
        testContext.workflowController.proceedTo(InitializationStep.INITIALIZE_DEPENDENCIES)
    }

    @Before(order = HookOrder.BEFORE_SET_UP)
    fun beforeSetUp() {
        testContext.workflowController.proceedTo(InitializationStep.BEFORE_SET_UP)
    }

    @Before(order = HookOrder.SET_UP)
    fun setUp() {
        testContext.workflowController.proceedTo(InitializationStep.SET_UP)
    }

    @Before(order = HookOrder.AFTER_SET_UP)
    fun afterSetUp() {
        testContext.workflowController.proceedTo(InitializationStep.AFTER_SET_UP)
    }

    @After(order = HookOrder.TEAR_DOWN)
    fun tearDown() {
        testContext.workflowController.proceedTo(InitializationStep.TEAR_DOWN)
    }

    @After(order = HookOrder.AFTER_TEAR_DOWN)
    fun afterTearDown() {
        testContext.workflowController.proceedTo(InitializationStep.AFTER_TEAR_DOWN)
    }
}
