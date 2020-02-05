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

    /**
     * sweetest uses and exposes [kotlinx.coroutines.test.TestCoroutineScope] as a standard way to test with coroutines,
     * but before that was available sweetest had its own solution which can still be used by enabling this option (see
     * [com.mysugr.sweetest.framework.coroutine.testCoroutine]).
     *
     * Sidenote: This can just be configured on test level because coroutines capabilities need to be initialized very
     * early in the initialization process.
     */
    fun useLegacyCoroutineScope() = apply {
        testContext.coroutines.configure {
            useLegacyCoroutineScope()
        }
    }
}
