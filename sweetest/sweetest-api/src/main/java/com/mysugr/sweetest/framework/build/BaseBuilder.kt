package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.api.configureAutomaticDependencyProvision
import com.mysugr.sweetest.api.configureDependencyProvision
import com.mysugr.sweetest.api.notifyStepsRequired
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyInitializerContext
import com.mysugr.sweetest.framework.flow.InitializationStep
import com.mysugr.sweetest.internal.Steps
import kotlin.reflect.KClass

private const val dependencyModeDeprecationMessage = "Dependency modes like \"REAL\" or \"MOCK\" " +
    "as well as \"required...\" are obsolete. Use \"provide\" instead."

abstract class BaseBuilder<TSelf>(
    @PublishedApi internal val testContext: TestContext,
    @PublishedApi internal val moduleTestingConfiguration: ModuleTestingConfiguration?
) {

    init {
        moduleTestingConfiguration?.let { testContext.configurations.put(it) }
    }

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

    @PublishedApi
    internal fun apply(run: () -> Unit): TSelf {
        run()
        return this as TSelf
    }

    // --- region: Published API (kept as small as possible)

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

    // --- region: Published API – LEGACY! (kept as small as possible)

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
        configureDependencyProvision(testContext.dependencies, type) { initializer(it as DependencyInitializerContext) }

    @PublishedApi
    internal fun <T : Any> provideInternal(type: KClass<T>) {
        configureAutomaticDependencyProvision(testContext.dependencies, type)
    }

    @PublishedApi
    internal fun <T : Steps> requireStepsInternal(type: KClass<T>) {
        notifyStepsRequired(testContext.steps, type)
    }

    // --- region: Internal API – LEGACY!

    @PublishedApi
    internal fun requireRealInternal(type: KClass<*>) {
        checkInvalidLegacyFunctionCall("requireReal")
        testContext.dependencies.requireReal(
            clazz = type
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerReal")
        testContext.dependencies.offerReal(
            clazz = type,
            initializer = { argument -> initializer(argument as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun <T : Any> offerRealRequiredInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerRealRequired")
        testContext.dependencies.offerRealRequired(
            clazz = type,
            initializer = { argument -> initializer(argument as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun requireMockInternal(type: KClass<*>) {
        checkInvalidLegacyFunctionCall("requireMock")
        testContext.dependencies.requireMock(
            clazz = type
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerMock")
        testContext.dependencies.offerMock(
            clazz = type,
            initializer = { argument -> initializer(argument as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun <T : Any> offerMockRequiredInternal(type: KClass<T>, initializer: DependencyInitializer<T>) {
        checkInvalidLegacyFunctionCall("offerMockRequired")
        testContext.dependencies.offerMockRequired(
            clazz = type,
            initializer = { initializer(it as DependencyInitializerContext) }
        )
    }

    @PublishedApi
    internal fun requireSpyInternal(type: KClass<*>) {
        checkInvalidLegacyFunctionCall("requireSpy")
        testContext.dependencies.requireSpy(
            clazz = type
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
        testContext.workflowProvider.subscribe(InitializationStep.INITIALIZE_DEPENDENCIES, run)
    }

    fun onBeforeSetUp(run: () -> Unit) = apply {
        checkNotYetDone()
        testContext.workflowProvider.subscribe(InitializationStep.BEFORE_SET_UP, run)
    }

    fun onSetUp(run: () -> Unit) = apply {
        checkNotYetDone()
        testContext.workflowProvider.subscribe(InitializationStep.SET_UP, run)
    }

    fun onAfterSetUp(run: () -> Unit) = apply {
        checkNotYetDone()
        testContext.workflowProvider.subscribe(InitializationStep.AFTER_SET_UP, run)
    }

    fun onTearDown(run: () -> Unit) = apply {
        checkNotYetDone()
        testContext.workflowProvider.subscribe(InitializationStep.TEAR_DOWN, run)
    }

    fun onAfterTearDown(run: () -> Unit) = apply {
        checkNotYetDone()
        testContext.workflowProvider.subscribe(InitializationStep.AFTER_TEAR_DOWN, run)
    }
}
