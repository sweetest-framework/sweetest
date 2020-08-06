package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.StepsBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface Steps : CoroutineScope

abstract class BaseSteps(
    private val testContext: TestContext,
    private val moduleTestingConfiguration: ModuleTestingConfiguration
) : Steps, TestingAccessor {

    open fun configure() = StepsBuilder(this, testContext, moduleTestingConfiguration)

    @Deprecated(coroutineScopeImplementationDeprecation)
    override val coroutineContext: CoroutineContext
        get() {
            println(coroutineScopeImplementationDeprecation)
            return testContext.legacyCoroutines.coroutineContext
        }

    override val accessor = configure().build()
    protected val dependencies = accessor.dependencies
    protected val delegates = accessor.delegates

    companion object {
        private const val coroutineScopeImplementationDeprecation = "Using steps class as CoroutineScope is " +
            "deprecated, please use testCoroutineScope extension property instead!"
    }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
