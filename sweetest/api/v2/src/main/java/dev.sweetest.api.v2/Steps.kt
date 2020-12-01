package dev.sweetest.api.v2

import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.usecases.registerStepsInstance

abstract class Steps(testContext: TestContext) : TestElement(testContext), Steps {

    init {
        registerStepsInstance(testContext.steps, this)
    }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
