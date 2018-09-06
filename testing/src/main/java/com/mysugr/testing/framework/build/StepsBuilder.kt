package com.mysugr.testing.framework.build

import com.mysugr.testing.framework.configuration.ModuleTestingConfiguration
import com.mysugr.testing.framework.accessor.StepsAccessor
import com.mysugr.testing.framework.base.BaseSteps
import com.mysugr.testing.framework.context.TestContext

class StepsBuilder(
    instance: BaseSteps,
    testContext: TestContext,
    moduleTestingConfiguration: ModuleTestingConfiguration
) :
    BaseBuilder<StepsBuilder, StepsAccessor>(testContext, moduleTestingConfiguration) {

    override fun buildInternal(): StepsAccessor {
        return StepsAccessor(testContext)
    }

    init {
        testContext.steps.setUpInstance(instance)
    }
}
