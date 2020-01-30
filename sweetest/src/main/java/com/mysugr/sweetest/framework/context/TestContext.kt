package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContextProvider
import com.mysugr.sweetest.framework.environment.TestEnvironment

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

    @PublishedApi
    internal val coroutines = CoroutinesTestContextProvider(workflow)

    init {
        TestEnvironment.reset()
    }
}
