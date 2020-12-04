package dev.sweetest.v1.internal.builder

import dev.sweetest.v1.ModuleTestingConfiguration
import dev.sweetest.internal.TestContext
import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.workflow.WorkflowStep.INITIALIZE_STEPS
import com.mysugr.sweetest.usecases.subscribeWorkflow

class TestBuilder(testContext: TestContext, moduleTestingConfiguration: ModuleTestingConfiguration?) :
    BaseBuilder<TestBuilder>(testContext, moduleTestingConfiguration) {

    fun onInitializeSteps(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], INITIALIZE_STEPS, run)
    }
}
