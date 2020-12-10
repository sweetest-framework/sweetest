@file:Suppress("DEPRECATION")

package dev.sweetest.v1.internal.builder

import dev.sweetest.internal.SweetestException
import dev.sweetest.internal.InternalBaseSteps
import dev.sweetest.internal.TestContext
import dev.sweetest.internal.dependency.DependenciesTestContext
import dev.sweetest.internal.dependency.DependencyProvider
import dev.sweetest.internal.dependency.configureDependencyMock
import dev.sweetest.internal.dependency.configureDependencyProvision
import dev.sweetest.internal.dependency.configureDependencyProvisionAutomatic
import dev.sweetest.internal.dependency.configureDependencyReal
import dev.sweetest.internal.dependency.configureDependencySpy
import dev.sweetest.internal.steps.StepsTestContext
import dev.sweetest.internal.steps.notifyStepsRequired
import dev.sweetest.internal.workflow.WorkflowStep
import dev.sweetest.internal.workflow.WorkflowTestContext
import dev.sweetest.internal.workflow.subscribeWorkflow
import dev.sweetest.v1.ModuleTestingConfiguration
import kotlin.reflect.KClass

private const val DEPENDENCY_MODE_DEPRECATION_MESSAGE = "Dependency modes like \"REAL\" or \"MOCK\" " +
    "as well as \"required...\" are obsolete. Use \"provide\" instead."

abstract class BaseBuilder<TSelf>(
    internal val testContext: TestContext,
    internal val moduleTestingConfiguration: ModuleTestingConfiguration?
) {

    private var isFrozen = false

    internal fun freeze() {
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
        @Suppress("UNCHECKED_CAST")
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

    // --- region: Public API – LEGACY! (the following inline functions should just be wrappers over implementation functions!)

    inline fun <reified T : InternalBaseSteps> requireSteps() = apply {
        requireStepsInternal(T::class)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireReal() = apply {
        requireRealInternal(T::class)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerReal(noinline provider: DependencyProvider<T>) = apply {
        offerRealInternal(T::class, provider)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerRealRequired(noinline provider: DependencyProvider<T>) = apply {
        offerRealRequiredInternal(T::class, provider)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireMock() = apply {
        requireMockInternal(T::class)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerMock(noinline provider: DependencyProvider<T>) = apply {
        offerMockInternal(T::class, provider)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerMockRequired(noinline provider: DependencyProvider<T>) = apply {
        offerMockRequiredInternal(T::class, provider)
    }

    @Deprecated(DEPENDENCY_MODE_DEPRECATION_MESSAGE, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireSpy() = apply {
        requireSpyInternal(T::class)
    }

    // --- region: Internal API

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        configureDependencyProvision(
            testContext[DependenciesTestContext],
            dependencyType = type,
            provider = provider
        )
    }

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>) {
        checkNotYetFrozen()
        configureDependencyProvisionAutomatic(
            testContext[DependenciesTestContext],
            dependencyType = type
        )
    }

    @PublishedApi
    internal fun <T : InternalBaseSteps> requireStepsInternal(stepsType: KClass<T>) {
        checkNotYetFrozen()
        notifyStepsRequired(testContext[StepsTestContext], stepsType)
    }

    // --- region: Internal API – LEGACY!

    @PublishedApi
    internal fun requireRealInternal(type: KClass<*>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("requireReal")
        configureDependencyReal(
            testContext[DependenciesTestContext],
            dependencyType = type,
            forceMode = true
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerReal")
        configureDependencyReal(
            testContext[DependenciesTestContext],
            dependencyType = type,
            offerProvider = provider
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealRequiredInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerRealRequired")
        configureDependencyReal(
            testContext[DependenciesTestContext],
            dependencyType = type,
            forceMode = true,
            offerProvider = provider
        )
    }

    @PublishedApi
    internal fun requireMockInternal(type: KClass<*>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("requireMock")
        configureDependencyMock(
            testContext[DependenciesTestContext],
            dependencyType = type,
            forceMode = true
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerMock")
        configureDependencyMock(
            testContext[DependenciesTestContext],
            dependencyType = type,
            offerProvider = provider
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockRequiredInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerMockRequired")
        configureDependencyMock(
            testContext[DependenciesTestContext],
            dependencyType = type,
            forceMode = true,
            offerProvider = provider
        )
    }

    @PublishedApi
    internal fun requireSpyInternal(type: KClass<*>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("requireSpy")
        configureDependencySpy(
            testContext[DependenciesTestContext],
            dependencyType = type
        )
    }

    private fun checkInvalidLegacyFunctionCall(functionName: String) {
        if (this.moduleTestingConfiguration == null) {
            throw SweetestException(
                "`$functionName` is a legacy function and can't be used " +
                    "when using new API without module testing configuration!"
            )
        }
    }

    // --- region: Callbacks

    fun onInitializeDependencies(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.INITIALIZE_DEPENDENCIES, run)
    }

    fun onBeforeSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.BEFORE_SET_UP, run)
    }

    fun onSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.SET_UP, run)
    }

    fun onAfterSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.AFTER_SET_UP, run)
    }

    fun onTearDown(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.TEAR_DOWN, run)
    }

    fun onAfterTearDown(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext[WorkflowTestContext], WorkflowStep.AFTER_TEAR_DOWN, run)
    }
}