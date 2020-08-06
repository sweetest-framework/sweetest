package com.mysugr.sweetest.framework.base

import com.mysugr.sweetest.framework.build.TestBuilder
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import org.junit.Before

abstract class BaseJUnitTest @Deprecated(
    "No module configuration needed anymore.",
    ReplaceWith("BaseJUnitTest()", imports = ["com.mysugr.sweetest.framework.base.BaseJUnitTest"])
) constructor(private val moduleTestingConfiguration: ModuleTestingConfiguration? = null) : TestingAccessor {

    constructor() : this(moduleTestingConfiguration = null)

    open fun configure() = TestBuilder(moduleTestingConfiguration)

    override val accessor = configure().build()
    protected val delegates = accessor.delegates

    @Before
    fun junitBefore() {
        accessor.testContext.workflow.run()
    }
}
