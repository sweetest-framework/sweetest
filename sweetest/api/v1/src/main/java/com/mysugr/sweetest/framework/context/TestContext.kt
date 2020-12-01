@file:Suppress("DEPRECATION")

package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.coroutine.CoroutinesTestContext
import com.mysugr.sweetest.framework.dependency.DependencyProviderScope
import com.mysugr.sweetest.internal.TestContext
import com.mysugr.sweetest.usecases.initializeDependencies
import com.mysugr.sweetest.usecases.resetEnvironment

class TestContext internal constructor() : TestContext {

    internal val steps = StepsTestContext(this)
    internal val dependencies = DependenciesTestContext()
    internal val coroutines = CoroutinesTestContext()
    internal val workflow = WorkflowTestContext(steps)

    init {

        initializeDependencies(
            dependencies,
            dependencyProviderArgument = DependencyProviderScope(dependencies)
        )

        resetEnvironment()
    }
}