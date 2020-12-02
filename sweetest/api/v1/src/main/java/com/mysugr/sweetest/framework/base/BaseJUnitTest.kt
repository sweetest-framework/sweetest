package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.MODULE_CONFIG_DEPRECATION_MESSAGE
import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import org.junit.After
import org.junit.Before

abstract class BaseJUnitTest
@Deprecated("$MODULE_CONFIG_DEPRECATION_MESSAGE.", ReplaceWith("BaseJUnitTest()"))
constructor(private val moduleTestingConfiguration: ModuleTestingConfiguration? = null) : CommonBase() {

    constructor() : this(moduleTestingConfiguration = null)

    open fun configure() = TestBuilder(testContext, moduleTestingConfiguration)

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
