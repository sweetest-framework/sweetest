package dev.sweetest.api.v2.framework.build

import dev.sweetest.api.v2.framework.base.BaseSteps
import dev.sweetest.api.v2.framework.context.TestContext
import com.mysugr.sweetest.usecases.registerStepsInstance

class StepsBuilder(instance: BaseSteps, testContext: TestContext) :
    BaseBuilder<StepsBuilder>(testContext) {

    init {
        registerStepsInstance(testContext.steps, instance)
    }
}
