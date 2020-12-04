package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.usecases.configureDependencyProvisionAutomatic
import com.mysugr.sweetest.usecases.configureDependencyMock
import com.mysugr.sweetest.usecases.configureDependencyProvision
import com.mysugr.sweetest.usecases.configureDependencyReal
import com.mysugr.sweetest.usecases.configureDependencySpy
import com.mysugr.sweetest.usecases.notifyStepsRequired
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.dependency.DependencyProvider
import com.mysugr.sweetest.framework.dependency.asCoreDependencyProvider
import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.usecases.subscribeWorkflow
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

    inline fun <reified T : Steps> requireSteps() = apply {
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

    // --- region: Internal API – LEGACY!

    @PublishedApi
    internal fun requireRealInternal(type: KClass<*>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("requireReal")
        configureDependencyReal(
            testContext.dependencies,
            dependencyType = type,
            forceMode = true
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerReal")
        configureDependencyReal(
            testContext.dependencies,
            dependencyType = type,
            offerProvider = provider.asCoreDependencyProvider()
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealRequiredInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerRealRequired")
        configureDependencyReal(
            testContext.dependencies,
            dependencyType = type,
            forceMode = true,
            offerProvider = provider.asCoreDependencyProvider()
        )
    }

    @PublishedApi
    internal fun requireMockInternal(type: KClass<*>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("requireMock")
        configureDependencyMock(
            testContext.dependencies,
            dependencyType = type,
            forceMode = true
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerMock")
        configureDependencyMock(
            testContext.dependencies,
            dependencyType = type,
            offerProvider = provider.asCoreDependencyProvider()
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockRequiredInternal(type: KClass<T>, provider: DependencyProvider<T>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("offerMockRequired")
        configureDependencyMock(
            testContext.dependencies,
            dependencyType = type,
            forceMode = true,
            offerProvider = provider.asCoreDependencyProvider()
        )
    }

    @PublishedApi
    internal fun requireSpyInternal(type: KClass<*>) {
        checkNotYetFrozen()
        checkInvalidLegacyFunctionCall("requireSpy")
        configureDependencySpy(
            testContext.dependencies,
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
        subscribeWorkflow(testContext.workflow, InitializationStep.INITIALIZE_DEPENDENCIES, run)
    }

    fun onBeforeSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, InitializationStep.BEFORE_SET_UP, run)
    }

    fun onSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, InitializationStep.SET_UP, run)
    }

    fun onAfterSetUp(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, InitializationStep.AFTER_SET_UP, run)
    }

    fun onTearDown(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, InitializationStep.TEAR_DOWN, run)
    }

    fun onAfterTearDown(run: () -> Unit) = apply {
        checkNotYetFrozen()
        subscribeWorkflow(testContext.workflow, InitializationStep.AFTER_TEAR_DOWN, run)
    }
}
