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
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyInitializerContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.usecases.subscribeWorkflow
import kotlin.reflect.KClass

private const val dependencyModeDeprecationMessage = "Dependency modes like \"REAL\" or \"MOCK\" " +
    "as well as \"required...\" are obsolete. Use \"provide\" instead."

abstract class BaseBuilder<TSelf>(
    internal val testContext: TestContext,
    internal val moduleTestingConfiguration: ModuleTestingConfiguration?
) {

    private var isDone = false

    /**
     * Finalizes the configuration
     */
    internal fun setDone() {
        // Just makes sure the setDone() function is called just once
        checkNotYetDone()
        isDone = true
    }

    protected fun checkNotYetDone() {
        if (isDone) {
            throw IllegalStateException("build() already done")
        }
    }

    // This function is inlined so there won't be any external binary dependencies to it and it can be changed freely
    @PublishedApi
    internal inline fun apply(run: () -> Unit): TSelf {
        run()
        return this as TSelf
    }

    // --- region: Public API (inline functions should just be a wrapper over implementation functions!)

    /**
     * Provides an [initializer] for type [T] to sweetest.
     *
     * That [initializer] will be used when an instance of [T] is needed in the test.
     *
     * Can only by called once per type!
     *
     * **Legacy note:** [provide] doesn't know about constraints configured with [requireReal], [requireMock] etc.
     * (configuration in test and steps classes) or `realOnly` and `mockOnly` (module testing configuration) and thus
     * overrides these constraints.
     */
    inline fun <reified T : Any> provide(noinline initializer: DependencyInitializer<T>) = apply {
        provideInternal(T::class, initializer)
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

    // --- region: Public API – LEGACY! (inline functions should just be a wrapper over implementation functions!)

    inline fun <reified T : Steps> requireSteps() = apply {
        requireStepsInternal(T::class)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireReal() = apply {
        requireRealInternal(T::class)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerReal(noinline initializer: DependencyInitializer<T>) = apply {
        offerRealInternal(T::class, initializer)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerRealRequired(noinline initializer: DependencyInitializer<T>) = apply {
        offerRealRequiredInternal(T::class, initializer)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireMock() = apply {
        requireMockInternal(T::class)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerMock(noinline initializer: DependencyInitializer<T>) = apply {
        offerMockInternal(T::class, initializer)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerMockRequired(noinline initializer: DependencyInitializer<T>) = apply {
        offerMockRequiredInternal(T::class, initializer)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireSpy() = apply {
        requireSpyInternal(T::class)
    }

    // --- region: Internal API

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>, initializer: DependencyInitializer<T>) =
        configureDependencyProvision(
            testContext.dependencies,
            type
        ) { initializer(it as DependencyInitializerContext) }

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>) {
        configureDependencyProvisionAutomatic(
            testContext.dependencies,
            type
        )
    }

    @PublishedApi
    internal fun <T : Steps> requireStepsInternal(type: KClass<T>) {
        notifyStepsRequired(testContext.steps, type)
    }

    // --- region: Internal API – LEGACY!

    @PublishedApi
    internal fun requireRealInternal(type: KClass<*>) {
        checkInvalidLegacyFunctionCall("requireReal")
        configureDependencyReal(
            testContext.dependencies,
            type = type,
            forceMode = true
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerReal")
        configureDependencyReal(
            testContext.dependencies,
            type = type,
            forceMode = false,
            offerInitializer = { argument -> initializer(argument as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealRequiredInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerRealRequired")
        configureDependencyReal(
            testContext.dependencies,
            type = type,
            forceMode = true,
            offerInitializer = { argument -> initializer(argument as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun requireMockInternal(type: KClass<*>) {
        checkInvalidLegacyFunctionCall("requireMock")
        configureDependencyMock(
            testContext.dependencies,
            type = type,
            forceMode = true
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerMock")
        configureDependencyMock(
            testContext.dependencies,
            type = type,
            forceMode = false,
            offerInitializer = { argument -> initializer(argument as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockRequiredInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerMockRequired")
        configureDependencyMock(
            testContext.dependencies,
            type = type,
            forceMode = true,
            offerInitializer = { argument -> initializer(argument as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun requireSpyInternal(type: KClass<*>) {
        checkInvalidLegacyFunctionCall("requireSpy")
        configureDependencySpy(
            testContext.dependencies,
            type = type
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
        checkNotYetDone()
        subscribeWorkflow(testContext.workflow, InitializationStep.INITIALIZE_DEPENDENCIES, run)
    }

    fun onBeforeSetUp(run: () -> Unit) = apply {
        checkNotYetDone()
        subscribeWorkflow(testContext.workflow, InitializationStep.BEFORE_SET_UP, run)
    }

    fun onSetUp(run: () -> Unit) = apply {
        checkNotYetDone()
        subscribeWorkflow(testContext.workflow, InitializationStep.SET_UP, run)
    }

    fun onAfterSetUp(run: () -> Unit) = apply {
        checkNotYetDone()
        subscribeWorkflow(testContext.workflow, InitializationStep.AFTER_SET_UP, run)
    }

    fun onTearDown(run: () -> Unit) = apply {
        checkNotYetDone()
        subscribeWorkflow(testContext.workflow, InitializationStep.TEAR_DOWN, run)
    }

    fun onAfterTearDown(run: () -> Unit) = apply {
        checkNotYetDone()
        subscribeWorkflow(testContext.workflow, InitializationStep.AFTER_TEAR_DOWN, run)
    }
}
