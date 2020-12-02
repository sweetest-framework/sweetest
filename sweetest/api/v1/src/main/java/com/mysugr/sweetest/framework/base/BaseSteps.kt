package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.build.StepsBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.internal.Steps
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

abstract class BaseSteps
@Deprecated("No module configuration needed anymore.", ReplaceWith("BaseSteps(testContext)"))
constructor(
    testContext: TestContext,
    private val moduleTestingConfiguration: ModuleTestingConfiguration? = null
) : CommonBase(testContext), Steps, CoroutineScope {

    @Suppress("DEPRECATION")
    constructor(testContext: TestContext) : this(testContext, null)

    open fun configure() = StepsBuilder(this, testContext, moduleTestingConfiguration)

    init {
        @Suppress("LeakingThis")
        configure().freeze()
    }

    @Deprecated(
        "Please don't use steps classes as CoroutineScope! " +
            "This feature is deprecated and will be removed in the next API version!"
    )
    override val coroutineContext: CoroutineContext
        get() {
            // Unfortunately there is no way to mark the use of BaseSteps as CoroutineScope as deprecated for the IDE so we need to print the message
            println("\u001B[31mWARNING:\u001B[0m Please don't use steps classes as CoroutineScope! " +
                "This feature is deprecated and will be removed in the next API version!")
            return testContext[CoroutinesTestContext].coroutineContext
        }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
