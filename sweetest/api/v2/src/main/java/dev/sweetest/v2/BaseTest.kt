package dev.sweetest.v2

import dev.sweetest.internal.SweetestIntegrationsApi
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.environment.startEnvironment
import dev.sweetest.internal.workflow.WorkflowStep
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.subscribeWorkflow

@SweetestIntegrationsApi
open class BaseTest : TestElement() {

    override val testContext: TestContext = startEnvironment()

    fun onInitializeSteps(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_STEPS, run)
    }
}
