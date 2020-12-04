@file:Suppress("DEPRECATION")

package dev.sweetest.v1.internal.builder

import dev.sweetest.v1.ModuleTestingConfiguration
import dev.sweetest.v1.BaseSteps
import dev.sweetest.internal.TestContext
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
