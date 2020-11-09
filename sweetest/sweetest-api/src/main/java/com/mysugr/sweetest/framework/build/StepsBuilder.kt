package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext

class StepsBuilder(
    instance: BaseSteps,
    testContext: TestContext,
    moduleTestingConfiguration: ModuleTestingConfiguration?
) :
    BaseBuilder<StepsBuilder>(testContext, moduleTestingConfiguration) {

    init {
        testContext.steps.setUpInstance(instance)
    }
}
