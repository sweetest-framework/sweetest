package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.usecases.registerStepsInstance

class StepsBuilder(
    instance: BaseSteps,
    testContext: TestContext,
    moduleTestingConfiguration: ModuleTestingConfiguration?
) :
    BaseBuilder<StepsBuilder>(testContext, moduleTestingConfiguration) {

    init {
        registerStepsInstance(testContext[StepsTestContext], instance)
    }
}
