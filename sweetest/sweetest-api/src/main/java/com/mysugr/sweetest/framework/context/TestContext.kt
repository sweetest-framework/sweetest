package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.framework.dependency.DependencyInitializerContext
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.framework.flow.WorkflowProvider
import com.mysugr.sweetest.internal.TestContext

class TestContext internal constructor() : TestContext {

    @PublishedApi
    internal val steps = StepsTestContext(this)

    @PublishedApi
    internal val configurations = ConfigurationsTestContext()

    @PublishedApi
    internal val dependencies = DependenciesTestContext()

    internal val coroutines = CoroutinesTestContext()

    private val workflow = WorkflowTestContext(steps)
    val workflowController: WorkflowController = workflow
    val workflowProvider: WorkflowProvider = workflow

    init {
        val dependencyInitializerArgument = DependencyInitializerContext(dependencies)

        TestEnvironment.initialize(dependencyInitializerArgument)
        TestEnvironment.reset()
    }
}
