@file:Suppress("DEPRECATION")

package dev.sweetest.v1.internal.builder

import dev.sweetest.internal.TestContext
import dev.sweetest.internal.steps.StepsTestContext
import dev.sweetest.internal.steps.registerStepsInstance
import dev.sweetest.v1.BaseSteps
import dev.sweetest.v1.ModuleTestingConfiguration

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
