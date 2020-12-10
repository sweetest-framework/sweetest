package dev.sweetest.v1.internal.builder

import dev.sweetest.internal.TestContext
import dev.sweetest.internal.workflow.WorkflowStep.INITIALIZE_STEPS
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.subscribeWorkflow
import dev.sweetest.v1.ModuleTestingConfiguration

class TestBuilder(testContext: TestContext, moduleTestingConfiguration: ModuleTestingConfiguration?) :
    BaseBuilder<TestBuilder>(testContext, moduleTestingConfiguration) {

    fun onInitializeSteps(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], INITIALIZE_STEPS, run)
    }
}
