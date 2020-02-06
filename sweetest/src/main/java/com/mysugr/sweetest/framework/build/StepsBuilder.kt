package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.accessor.StepsAccessor
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext

class StepsBuilder(
    instance: BaseSteps,
    testContext: TestContext,
    moduleTestingConfiguration: ModuleTestingConfiguration
) :
    BaseBuilder<StepsBuilder, StepsAccessor>(testContext, moduleTestingConfiguration) {

    init {
        testContext.steps.setUpInstance(instance)
    }

    override fun buildInternal(): StepsAccessor {
        return StepsAccessor(testContext)
    }

    /**
     * sweetest uses and exposes [kotlinx.coroutines.test.TestCoroutineScope] as a standard way to test with coroutines,
     * but before that was available sweetest had its own solution which can still be used by enabling this option (see
     * [com.mysugr.sweetest.framework.coroutine.testCoroutine]).
     *
     * Sidenote: This can just be configured on test level because coroutines capabilities need to be initialized very
     * early in the initialization process. But it can be put as an expectation in steps so you receive an exception
     * when the expectations are different from the test.
     */
    fun useLegacyCoroutineScope(value: Boolean) = apply {
        testContext.coroutines.configure {
            useLegacyCoroutineScopeOnStepsLevel(value)
        }
    }
}
