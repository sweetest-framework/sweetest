package dev.sweetest.api.v2

import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.usecases.subscribeWorkflow

open class BaseTest : TestElement(TestContext()) {

    fun onInitializeSteps(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext.workflow, WorkflowStep.INITIALIZE_STEPS, run)
    }
}
