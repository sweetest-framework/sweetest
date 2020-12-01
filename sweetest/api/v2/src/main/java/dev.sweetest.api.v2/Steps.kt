package dev.sweetest.api.v2

import com.mysugr.sweetest.internal.Steps as InternalSteps
import com.mysugr.sweetest.usecases.registerStepsInstance

abstract class Steps(testContext: TestContext) : TestElement(testContext), InternalSteps {

    init {
        registerStepsInstance(testContext.steps, this)
    }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
