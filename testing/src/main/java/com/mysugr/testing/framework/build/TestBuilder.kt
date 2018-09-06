package com.mysugr.testing.framework.build

import com.mysugr.testing.framework.accessor.TestAccessor
import com.mysugr.testing.framework.configuration.ModuleTestingConfiguration
import com.mysugr.testing.framework.context.TestContext
import com.mysugr.testing.framework.flow.InitializationStep.INITIALIZE_STEPS

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
