package dev.sweetest.api.v2.framework.build

import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.usecases.configureDependencyProvision
import com.mysugr.sweetest.usecases.configureDependencyProvisionAutomatic
import com.mysugr.sweetest.usecases.notifyStepsRequired
import com.mysugr.sweetest.usecases.subscribeWorkflow
import dev.sweetest.api.v2.framework.context.TestContext
import dev.sweetest.api.v2.framework.dependency.DependencyProvider
import dev.sweetest.api.v2.framework.dependency.asCoreDependencyProvider
import kotlin.reflect.KClass

abstract class BaseBuilder<TSelf>(internal val testContext: TestContext) {

    private var isFrozen = false

    // TODO restrict visibility
    fun freeze() {
        checkNotYetFrozen()
        isFrozen = true
    }

    protected fun checkNotYetFrozen() {
        if (isFrozen) {
            error("Can't apply configuration after configuration is finished")
        }
    }

    // This function is inlined so there won't be any external binary dependencies to it and it can be changed freely
    @PublishedApi
    internal inline fun apply(run: () -> Unit): TSelf {
        run()
        return this as TSelf
    }

    // --- region: Public API (the following inline functions should just be wrappers over implementation functions!)

    /**
     * Provides a [provider] for type [T] to sweetest's dependency management.
     *
     * That [provider] will be used when an instance of [T] is needed in the test.
     *
     * Can only by called once per type!
     *
     * **Legacy note:** [provide] doesn't know about constraints configured with [requireReal], [requireMock] etc.
     * (configuration in test and steps classes) or `realOnly` and `mockOnly` (module testing configuration) and thus
     * overrides these constraints.
     */
    inline fun <reified T : Any> provide(noinline provider: DependencyProvider<T>) = apply {
        provideInternal(T::class, provider)
    }

    /**
     * Provides an instance of [T] to sweetest that is automatically instantiated using the default constructor and the
     * built-in dependency injection.
     *
     * Can only by called once per type!
     *
     * **Legacy note:** [provide] doesn't know about constraints configured with [requireReal], [requireMock] etc.
     * (configuration in test and steps classes) or `realOnly` and `mockOnly` (module testing configuration) and thus
     * overrides these constraints.
     */
    inline fun <reified T : Any> provide() = apply {
        provideInternal(T::class)
    }

    inline fun <reified T : Steps> requireSteps() = apply {
        requireStepsInternal(T::class)
    }

    // --- region: Internal API

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        configureDependencyProvision(
            testContext.dependencies,
            dependencyType = type,
            provider = provider.asCoreDependencyProvider()
        )
    }

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>) {
        checkNotYetFrozen()
        configureDependencyProvisionAutomatic(
            testContext.dependencies,
            dependencyType = type
        )
    }

    @PublishedApi
    internal fun <T : Steps> requireStepsInternal(stepsType: KClass<T>) {
        checkNotYetFrozen()
        notifyStepsRequired(testContext.steps, stepsType)
    }

    // --- region: Callbacks

    fun onInitializeDependencies(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, WorkflowStep.INITIALIZE_DEPENDENCIES, run)
    }

    fun onBeforeSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, WorkflowStep.BEFORE_SET_UP, run)
    }

    fun onSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, WorkflowStep.SET_UP, run)
    }

    fun onAfterSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, WorkflowStep.AFTER_SET_UP, run)
    }

    fun onTearDown(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, WorkflowStep.TEAR_DOWN, run)
    }

    fun onAfterTearDown(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, WorkflowStep.AFTER_TEAR_DOWN, run)
    }
}
