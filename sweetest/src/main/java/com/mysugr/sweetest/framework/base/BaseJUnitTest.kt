package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import org.junit.Before

abstract class BaseJUnitTest(private val moduleTestingConfiguration: ModuleTestingConfiguration) : TestingAccessor {

    open fun configure() = TestBuilder(moduleTestingConfiguration)

    override val accessor = configure().build()
    protected val dependencies = accessor.dependencies
    protected val delegates = accessor.delegates

    @Before
    fun junitBefore() {
        accessor.testContext.workflow.run()
    }
}
