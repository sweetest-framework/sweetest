package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import org.junit.Before

abstract class BaseJUnitTest @Deprecated("Module testing configuration will be phased out") constructor(
    private val moduleTestingConfiguration: ModuleTestingConfiguration? = null
) : TestingAccessor {

    constructor() : this(null)

    open fun configure() = TestBuilder(moduleTestingConfiguration)

    override val accessor = configure().build()
    protected val dependencies = accessor.dependencies
    protected val delegates = accessor.delegates

    @Before
    fun junitBefore() {
        accessor.testContext.workflow.run()
    }
}
