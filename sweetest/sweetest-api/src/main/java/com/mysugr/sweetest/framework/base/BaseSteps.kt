package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.StepsBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.internal.Steps
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

private const val coroutineScopeDeprecationMessage =
    "Please don't use steps classes as CoroutineScope! " +
        "This feature is deprecated and will be removed in the next API version!"

abstract class BaseSteps @Deprecated(
    "No module configuration needed anymore.",
    ReplaceWith("BaseSteps(testContext)", imports = ["BaseSteps"])
) constructor(
    testContext: TestContext,
    private val moduleTestingConfiguration: ModuleTestingConfiguration? = null
) : CommonBase(testContext), Steps, CoroutineScope {

    constructor(testContext: TestContext) : this(testContext, null)

    open fun configure() = StepsBuilder(this, testContext, moduleTestingConfiguration)

    init {
        configure().setDone()
    }

    @Deprecated(coroutineScopeDeprecationMessage)
    override val coroutineContext: CoroutineContext
        get() {
            // Unfortunately there is no way to mark the use of BaseSteps as CoroutineScope as deprecated so we need to print the message
            println("\u001B[31mWARNING:\u001B[0m $coroutineScopeDeprecationMessage")
            return testContext.coroutines.coroutineContext
        }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
