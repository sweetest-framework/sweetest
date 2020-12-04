package dev.sweetest.v2

import dev.sweetest.internal.SweetestIntegrationsApi
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.environment.startEnvironment
import dev.sweetest.internal.workflow.WorkflowStep
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.subscribeWorkflow
import dev.sweetest.v2.internal.ApiTestElement

@SweetestIntegrationsApi
open class BaseTest : ApiTestElement() {

    override val testContext: TestContext = startEnvironment()

    fun onInitializeSteps(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_STEPS, run)
    }
}
