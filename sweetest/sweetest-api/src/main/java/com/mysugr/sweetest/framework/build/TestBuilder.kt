package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_STEPS
import com.mysugr.sweetest.usecases.subscribeWorkflow

class TestBuilder(testContext: TestContext, moduleTestingConfiguration: ModuleTestingConfiguration?) :
    BaseBuilder<TestBuilder>(testContext, moduleTestingConfiguration) {

    fun onInitializeSteps(run: () -> Unit) = apply {
        checkNotYetDone()
        subscribeWorkflow(testContext.workflow, INITIALIZE_STEPS, run)
    }
}
