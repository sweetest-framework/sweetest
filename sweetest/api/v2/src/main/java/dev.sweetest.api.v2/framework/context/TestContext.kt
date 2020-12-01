package dev.sweetest.api.v2.framework.context

import com.mysugr.sweetest.framework.context.DependenciesTestContext
import com.mysugr.sweetest.framework.context.StepsTestContext
import com.mysugr.sweetest.framework.context.WorkflowTestContext
import dev.sweetest.api.v2.framework.dependency.DependencyProviderScope
import com.mysugr.sweetest.internal.TestContext
import com.mysugr.sweetest.usecases.initializeDependencies
import com.mysugr.sweetest.usecases.resetEnvironment

class TestContext : TestContext {

    // TODO restrict visibility
    val steps = StepsTestContext(this)
    val dependencies = DependenciesTestContext()
    val workflow = WorkflowTestContext(steps)

    init {

        initializeDependencies(
            dependencies,
            dependencyProviderArgument = DependencyProviderScope(
                dependencies
            )
        )

        resetEnvironment()
    }
}
