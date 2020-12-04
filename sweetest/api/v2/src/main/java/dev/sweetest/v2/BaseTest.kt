package dev.sweetest.v2

import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.usecases.startEnvironment
import com.mysugr.sweetest.usecases.subscribeWorkflow
import dev.sweetest.internal.SweetestIntegrationsApi
import dev.sweetest.internal.TestContext

@SweetestIntegrationsApi
open class BaseTest : TestElement() {

    override val testContext: TestContext = startEnvironment()

    fun onInitializeSteps(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_STEPS, run)
    }
}
