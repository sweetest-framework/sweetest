package dev.sweetest.v2

import dev.sweetest.internal.environment.getCurrentTestContext
import dev.sweetest.internal.steps.StepsTestContext
import dev.sweetest.internal.steps.registerStepsInstance
import dev.sweetest.internal.Steps as InternalSteps

abstract class Steps : TestElement(), InternalSteps {

    override val testContext = getCurrentTestContext()

    init {
        registerStepsInstance(testContext[StepsTestContext], this)
    }
}
