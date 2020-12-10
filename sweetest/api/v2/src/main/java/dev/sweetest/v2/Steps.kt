package dev.sweetest.v2

import dev.sweetest.internal.environment.getCurrentTestContext
import dev.sweetest.internal.steps.StepsTestContext
import dev.sweetest.internal.steps.registerStepsInstance
import dev.sweetest.v2.internal.ApiTestElement
import dev.sweetest.internal.InternalBaseSteps as InternalSteps

abstract class Steps : ApiTestElement(), InternalSteps {

    override val testContext = getCurrentTestContext()

    init {
        registerStepsInstance(testContext[StepsTestContext], this)
    }
}
