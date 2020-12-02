package dev.sweetest.api.v2

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.usecases.registerStepsInstance
import com.mysugr.sweetest.internal.Steps as InternalSteps

abstract class Steps(testContext: TestContext) : TestElement(testContext), InternalSteps {

    init {
        registerStepsInstance(testContext[StepsTestContext], this)
    }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
