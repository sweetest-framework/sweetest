package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.accessor.StepsAccessor
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext

class StepsBuilder(
    instance: BaseSteps,
    testContext: TestContext,
    moduleTestingConfiguration: ModuleTestingConfiguration?
) :
    BaseBuilder<StepsBuilder, StepsAccessor>(testContext, moduleTestingConfiguration) {

    override fun buildInternal(): StepsAccessor {
        return StepsAccessor(testContext)
    }

    init {
        testContext.steps.setUpInstance(instance)
    }
}
