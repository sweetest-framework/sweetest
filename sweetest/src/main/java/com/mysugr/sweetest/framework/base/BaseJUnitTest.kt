package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.flow.InitializationStep
import org.junit.After
import org.junit.Before

abstract class BaseJUnitTest(private val moduleTestingConfiguration: ModuleTestingConfiguration) : TestingAccessor {

    open fun configure() = TestBuilder(moduleTestingConfiguration)

    override val accessor = configure().build()
    protected val dependencies = accessor.dependencies
    protected val delegates = accessor.delegates

    init {
        accessor.testContext.workflow.proceedTo(InitializationStep.INITIALIZE_FRAMEWORK)
    }

    @Before
    fun junitBefore() {
        accessor.testContext.workflow.run()
    }

    @After
    fun junitAfter() {
        accessor.testContext.workflow.finish()
    }
}
