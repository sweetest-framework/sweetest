package com.mysugr.testing.framework.context

import com.mysugr.testing.framework.environment.TestEnvironment

class TestContext internal constructor() {

    @PublishedApi
    internal val steps = StepsTestContext(this)

    @PublishedApi
    internal val factories = FactoriesTestContext(this)

    @PublishedApi
    internal val configurations = ConfigurationsTestContext(factories)

    @PublishedApi
    internal val dependencies = DependenciesTestContext()

    val workflow = WorkflowTestContext(steps)

    init {
        TestEnvironment.reset()
    }
}