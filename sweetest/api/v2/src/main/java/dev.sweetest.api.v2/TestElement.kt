package dev.sweetest.api.v2

import com.mysugr.sweetest.framework.workflow.WorkflowStep
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.usecases.hasWorkflowAlreadyStarted
import com.mysugr.sweetest.usecases.configureDependencyProvision
import com.mysugr.sweetest.usecases.configureDependencyProvisionAutomatic
import com.mysugr.sweetest.usecases.getDependencyDelegate
import com.mysugr.sweetest.usecases.getStepsDelegate
import com.mysugr.sweetest.usecases.notifyStepsRequired
import com.mysugr.sweetest.usecases.subscribeWorkflow
import dev.sweetest.api.v2.internal.DependencyProvider
import dev.sweetest.api.v2.internal.asCoreDependencyProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

abstract class TestElement(protected val testContext: TestContext) : com.mysugr.sweetest.internal.TestElement {

    // --- region: Public configuration API

    // Caution: the following inline functions should just be wrappers over implementation functions!

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
    inline fun <reified T : Any> provide(noinline provider: DependencyProvider<T>) {
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
    inline fun <reified T : Any> provide() {
        provideInternal(T::class)
    }

    inline fun <reified T : Steps> requireSteps() {
        requireStepsInternal(T::class)
    }

    fun onInitializeDependencies(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext.workflow, WorkflowStep.INITIALIZE_DEPENDENCIES, run)
    }

    fun onBeforeSetUp(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext.workflow, WorkflowStep.BEFORE_SET_UP, run)
    }

    fun onSetUp(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext.workflow, WorkflowStep.SET_UP, run)
    }

    fun onAfterSetUp(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext.workflow, WorkflowStep.AFTER_SET_UP, run)
    }

    fun onTearDown(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext.workflow, WorkflowStep.TEAR_DOWN, run)
    }

    fun onAfterTearDown(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext.workflow, WorkflowStep.AFTER_TEAR_DOWN, run)
    }

    // --- region: Public consumption API

    inline fun <reified T : Any> dependency(): ReadOnlyProperty<TestElement, T> =
        dependencyInternal(this, T::class)

    inline fun <reified T : dev.sweetest.api.v2.Steps> steps(): ReadOnlyProperty<TestElement, T> =
        stepsInternal(this, T::class)

    // --- region: Internals

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkConfigurePossible()
        configureDependencyProvision(
            testContext.dependencies,
            dependencyType = type,
            provider = provider.asCoreDependencyProvider()
        )
    }

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>) {
        checkConfigurePossible()
        configureDependencyProvisionAutomatic(
            testContext.dependencies,
            dependencyType = type
        )
    }

    @PublishedApi
    internal fun <T : Steps> requireStepsInternal(stepsType: KClass<T>) {
        checkConfigurePossible()
        notifyStepsRequired(testContext.steps, stepsType)
    }

    @PublishedApi
    internal fun <T : Any> dependencyInternal(
        scope: TestElement,
        type: KClass<T>
    ): ReadOnlyProperty<TestElement, T> =
        getDependencyDelegate(scope.testContext.dependencies, type)

    @PublishedApi
    internal fun <T : dev.sweetest.api.v2.Steps> stepsInternal(
        scope: TestElement,
        type: KClass<T>
    ): ReadOnlyProperty<TestElement, T> =
        getStepsDelegate(scope.testContext.steps, type)

    protected fun checkConfigurePossible() {
        check(!hasWorkflowAlreadyStarted(testContext.workflow)) {
            "Can't perform configuration tasks, workflow has already started!"
        }
    }
}
