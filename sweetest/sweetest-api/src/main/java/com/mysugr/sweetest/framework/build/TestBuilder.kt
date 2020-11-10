package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_STEPS

class TestBuilder(testContext: TestContext, moduleTestingConfiguration: ModuleTestingConfiguration?) :
    BaseBuilder<TestBuilder>(testContext, moduleTestingConfiguration) {

    fun onInitializeSteps(run: () -> Unit) = apply {
        checkNotYetDone()
        testContext.workflowProvider.subscribe(INITIALIZE_STEPS, run)
    }
}
