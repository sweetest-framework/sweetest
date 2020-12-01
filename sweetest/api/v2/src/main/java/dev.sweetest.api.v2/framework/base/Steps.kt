package dev.sweetest.api.v2.framework.base

import dev.sweetest.api.v2.framework.build.StepsBuilder
import dev.sweetest.api.v2.framework.context.TestContext
import com.mysugr.sweetest.internal.Steps

abstract class BaseSteps(testContext: TestContext) : CommonBase(testContext), Steps {

    open fun configure() = StepsBuilder(this, testContext)

    init {
        @Suppress("LeakingThis")
        configure().freeze()
    }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
