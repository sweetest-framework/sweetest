package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import org.junit.After
import org.junit.Before

abstract class BaseJUnitTest @Deprecated(
    "No module configuration needed anymore.",
    ReplaceWith("BaseJUnitTest()", imports = ["BaseJUnitTest"])
) constructor(private val moduleTestingConfiguration: ModuleTestingConfiguration? = null) : TestingAccessor {

    constructor() : this(moduleTestingConfiguration = null)

    open fun configure() = TestBuilder(moduleTestingConfiguration)

    override val accessor = configure().build()
    protected val dependencies = accessor.dependencies
    protected val delegates = accessor.delegates

    @Before
    fun junitBefore() {
        accessor.testContext.workflow.run()
    }

    @After
    fun junitAfter() {
        accessor.testContext.workflow.finish()
    }
}
