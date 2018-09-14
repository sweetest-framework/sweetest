package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.accessor.TestAccessor
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_STEPS

class TestBuilder(moduleTestingConfiguration: ModuleTestingConfiguration) :
    BaseBuilder<TestBuilder, TestAccessor>(TestContext(), moduleTestingConfiguration) {

    override fun buildInternal(): TestAccessor {
        return TestAccessor(testContext)
    }

    fun onInitializeSteps(run: () -> Unit) = apply {
        checkNotYetBuilt()
        testContext.workflow.subscribe(INITIALIZE_STEPS, run)
    }
}
