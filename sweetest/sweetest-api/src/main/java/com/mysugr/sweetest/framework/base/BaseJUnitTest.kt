package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.usecases.finishWorkflow
import com.mysugr.sweetest.usecases.proceedWorkflow
import org.junit.After
import org.junit.Before

abstract class BaseJUnitTest
@Deprecated("No module configuration needed anymore.", ReplaceWith("BaseJUnitTest()"))
constructor(private val moduleTestingConfiguration: ModuleTestingConfiguration? = null) : CommonBase(TestContext()) {

    constructor() : this(moduleTestingConfiguration = null)

    open fun configure() = TestBuilder(testContext, moduleTestingConfiguration)

    init {
        configure().freeze()
    }

    @Before
    fun junitBefore() {
        proceedWorkflow(testContext.workflow)
    }

    @After
    fun junitAfter() {
        finishWorkflow(testContext.workflow)
    }
}
