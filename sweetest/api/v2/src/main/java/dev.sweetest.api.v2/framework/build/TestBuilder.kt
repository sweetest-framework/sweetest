package dev.sweetest.api.v2.framework.build

import com.mysugr.sweetest.framework.workflow.WorkflowStep.INITIALIZE_STEPS
import com.mysugr.sweetest.usecases.subscribeWorkflow
import dev.sweetest.api.v2.framework.context.TestContext

class TestBuilder(testContext: TestContext) : BaseBuilder<TestBuilder>(testContext) {

    fun onInitializeSteps(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, INITIALIZE_STEPS, run)
    }
}
