@file:Suppress("DEPRECATION")

package dev.sweetest.v1

import dev.sweetest.internal.TestContext
import dev.sweetest.internal.environment.startEnvironment
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.finishWorkflow
import dev.sweetest.internal.workflow.proceedWorkflow
import dev.sweetest.v1.internal.CommonBase
import dev.sweetest.v1.internal.builder.TestBuilder
import org.junit.After
import org.junit.Before

@Deprecated(BASE_CLASS_DEPRECATION_MESSAGE)
abstract class BaseJUnitTest
@Deprecated("$MODULE_CONFIG_DEPRECATION_MESSAGE.", ReplaceWith("BaseJUnitTest()"))
constructor(private val moduleTestingConfiguration: ModuleTestingConfiguration? = null) : CommonBase() {

    constructor() : this(moduleTestingConfiguration = null)

    override val testContext: TestContext = startEnvironment()

    open fun configure() =
        TestBuilder(testContext, moduleTestingConfiguration)

    init {
        @Suppress("LeakingThis")
        configure().freeze()
    }

    @Before
    fun junitBefore() {
        proceedWorkflow(testContext[WorkflowTestContext])
    }

    @After
    fun junitAfter() {
        finishWorkflow(testContext[WorkflowTestContext])
    }
}
