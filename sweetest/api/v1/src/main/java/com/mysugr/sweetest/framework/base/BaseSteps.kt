package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.MODULE_CONFIG_DEPRECATION_MESSAGE
import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.build.StepsBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.usecases.getCurrentTestContext
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

private const val TEST_CONTEXT_DEPRECATION_MESSAGE =
    "of version 2.0.0 TestContext isn't needed and is in fact IGNORED."

private const val REPLACE_WITH = "BaseSteps()"

abstract class BaseSteps
@Deprecated("$MODULE_CONFIG_DEPRECATION_MESSAGE.", ReplaceWith("BaseSteps(testContext)"))
private constructor(
    private val moduleTestingConfiguration: ModuleTestingConfiguration? = null
) : CommonBase(), Steps, CoroutineScope {

    // Use this constructor!
    @Suppress("DEPRECATION")
    constructor() : this(null)

    @Deprecated(
        "$MODULE_CONFIG_DEPRECATION_MESSAGE, and as $TEST_CONTEXT_DEPRECATION_MESSAGE",
        ReplaceWith(REPLACE_WITH)
    )
    constructor(
        testContext: TestContext,
        moduleTestingConfiguration: ModuleTestingConfiguration? = null
    ) : this(moduleTestingConfiguration)

    @Suppress("DEPRECATION")
    @Deprecated(
        "As $TEST_CONTEXT_DEPRECATION_MESSAGE",
        ReplaceWith(REPLACE_WITH)
    )
    constructor(testContext: TestContext) : this(getCurrentTestContext(), null)

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
            println(
                "\u001B[31mWARNING:\u001B[0m Please don't use steps classes as CoroutineScope! " +
                    "This feature is deprecated and will be removed in the next API version!"
            )
            return testContext[CoroutinesTestContext].coroutineContext
        }
}

operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)
