package dev.sweetest.api.v2

import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.usecases.getCurrentTestContext
import com.mysugr.sweetest.usecases.registerStepsInstance
import com.mysugr.sweetest.internal.Steps as InternalSteps

abstract class Steps : TestElement(), InternalSteps {

    override val testContext = getCurrentTestContext()

    init {
        registerStepsInstance(testContext[StepsTestContext], this)
    }
}
