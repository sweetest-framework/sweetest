package dev.sweetest.v2

import com.mysugr.sweetest.internal.Steps
import dev.sweetest.internal.SweetestIntegrationsApi
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.dependency.DependenciesTestContext
import dev.sweetest.internal.dependency.DependencyProvider
import dev.sweetest.internal.dependency.configureDependencyProvision
import dev.sweetest.internal.dependency.configureDependencyProvisionAutomatic
import dev.sweetest.internal.dependency.getDependencyDelegate
import dev.sweetest.internal.steps.StepsTestContext
import dev.sweetest.internal.steps.getStepsDelegate
import dev.sweetest.internal.workflow.WorkflowStep
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.hasWorkflowAlreadyStarted
import dev.sweetest.internal.workflow.subscribeWorkflow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

@SweetestIntegrationsApi
abstract class TestElement : com.mysugr.sweetest.internal.TestElement {

    protected abstract val testContext: TestContext

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

    fun onInitializeDependencies(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_DEPENDENCIES, run)
    }

    fun onBeforeSetUp(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.BEFORE_SET_UP, run)
    }

    fun onSetUp(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.SET_UP, run)
    }

    fun onAfterSetUp(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.AFTER_SET_UP, run)
    }

    fun onTearDown(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.TEAR_DOWN, run)
    }

    fun onAfterTearDown(run: () -> Unit) {
        checkConfigurePossible()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.AFTER_TEAR_DOWN, run)
    }

    // --- region: Public consumption API

    inline fun <reified T : Any> dependency(): ReadOnlyProperty<TestElement, T> =
        dependencyInternal(this, T::class)

    inline fun <reified T : Steps> steps(): ReadOnlyProperty<TestElement, T> =
        stepsInternal(this, T::class)

    inline operator fun <T : Steps> T.invoke(run: T.() -> Unit) = run(this)

    // --- region: Internals

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkConfigurePossible()
        configureDependencyProvision(
            testContext[DependenciesTestContext],
            dependencyType = type,
            provider = provider
        )
    }

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>) {
        checkConfigurePossible()
        configureDependencyProvisionAutomatic(
            testContext[DependenciesTestContext],
            dependencyType = type
        )
    }

    @PublishedApi
    internal fun <T : Any> dependencyInternal(
        scope: TestElement,
        type: KClass<T>
    ): ReadOnlyProperty<TestElement, T> =
        getDependencyDelegate(scope.testContext[DependenciesTestContext], type)

    @PublishedApi
    internal fun <T : Steps> stepsInternal(
        scope: TestElement,
        type: KClass<T>
    ): ReadOnlyProperty<TestElement, T> =
        getStepsDelegate(scope.testContext[StepsTestContext], type)

    protected fun checkConfigurePossible() {
        check(!hasWorkflowAlreadyStarted(testContext[WorkflowTestContext])) {
            "Can't perform configuration tasks, workflow has already started!"
        }
    }
}
