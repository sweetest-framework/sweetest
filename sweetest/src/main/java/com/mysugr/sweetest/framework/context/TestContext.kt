package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.framework.environment.TestEnvironment

class TestContext internal constructor(@PublishedApi internal val testContextConfiguration: TestContextConfiguration) {

    @PublishedApi
    internal val steps = StepsTestContext(this)

    @PublishedApi
    internal val factories = FactoriesTestContext(this)

    @PublishedApi
    internal val configurations = ConfigurationsTestContext(factories)

    @PublishedApi
    internal val dependencies = DependenciesTestContext()

    internal val coroutines = CoroutinesTestContext()

    val workflow = WorkflowTestContext(steps)

    init {
        TestEnvironment.reset()
    }
}
