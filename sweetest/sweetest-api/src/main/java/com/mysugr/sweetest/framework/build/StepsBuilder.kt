package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.usecases.initializeSteps

class StepsBuilder(
    instance: BaseSteps,
    testContext: TestContext,
    moduleTestingConfiguration: ModuleTestingConfiguration?
) :
    BaseBuilder<StepsBuilder>(testContext, moduleTestingConfiguration) {

    init {
        initializeSteps(testContext.steps, instance)
    }
}
