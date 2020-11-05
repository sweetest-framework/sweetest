package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.framework.flow.WorkflowProvider
import com.mysugr.sweetest.internal.TestContext

class TestContext internal constructor() : TestContext {

    @PublishedApi
    internal val steps = StepsTestContext(this)

    @PublishedApi
    internal val factories = FactoriesTestContext(this)

    @PublishedApi
    internal val configurations = ConfigurationsTestContext(factories)

    @PublishedApi
    internal val dependencies = DependenciesTestContext()

    internal val coroutines = CoroutinesTestContext()

    private val workflow = WorkflowTestContext(steps)
    val workflowController: WorkflowController = workflow
    val workflowProvider: WorkflowProvider = workflow

    init {
        TestEnvironment // forces initialization of framework
        TestEnvironment.reset()
    }
}
