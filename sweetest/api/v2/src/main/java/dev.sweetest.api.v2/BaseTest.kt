package dev.sweetest.api.v2

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.context.DependenciesTestContext
import com.mysugr.sweetest.framework.context.WorkflowTestContext
import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.usecases.initializeDependencies
import com.mysugr.sweetest.usecases.resetEnvironment
import com.mysugr.sweetest.usecases.subscribeWorkflow
import dev.sweetest.api.v2.internal.DependencyProviderScope

open class BaseTest : TestElement(TestContext()) {

    init {
        initializeDependencies(
            testContext[DependenciesTestContext],
            dependencyProviderArgument = DependencyProviderScope(testContext[DependenciesTestContext])
        )

        resetEnvironment()
    }

    fun onInitializeSteps(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_STEPS, run)
    }
}
