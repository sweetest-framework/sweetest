package com.mysugr.sweetest.framework.build

import com.mysugr.sweetest.framework.accessor.BaseAccessor
import com.mysugr.sweetest.framework.base.Steps
import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.factory.FactoryRunner0
import com.mysugr.sweetest.framework.factory.FactoryRunner1
import com.mysugr.sweetest.framework.factory.FactoryRunner2
import com.mysugr.sweetest.framework.factory.FactoryRunner3
import com.mysugr.sweetest.framework.flow.InitializationStep.INITIALIZE_DEPENDENCIES
import com.mysugr.sweetest.framework.flow.InitializationStep.SET_UP
import kotlin.reflect.KClass

private const val dependencyModeDeprecationMessage = "Dependency modes like \"REAL\" or \"MOCK\" " +
    "as well as \"required...\" are obsolete. Use \"provide\" instead."

abstract class BaseBuilder<TSelf, TResult : BaseAccessor>(
    @PublishedApi internal val testContext: TestContext,
    moduleTestingConfiguration: ModuleTestingConfiguration
) {

    init {
        testContext.configurations.put(moduleTestingConfiguration)
    }

    private var built = false

    @PublishedApi
    internal fun checkNotYetBuilt() {
        if (built) {
            throw IllegalStateException("build() already done")
        }
    }

    @PublishedApi
    internal fun build(): TResult {
        checkNotYetBuilt()
        return buildInternal().also {
            built = true
        }
    }

    protected abstract fun buildInternal(): TResult

    @PublishedApi
    internal fun apply(run: () -> Unit): TSelf {
        run()
        return this as TSelf
    }

    /**
     * Provides an [initializer] for type [T] to sweetest.
     * That [initializer] will be used when an instance of [T] is needed in the test.
     *
     * Note:
     *  Require functions like [requireReal], [requireMock], etc. cannot be used on a type that uses
     *  [provide]. [provide] automatically requires that the [initializer] is used.
     */
    inline fun <reified T : Any> provide(noinline initializer: DependencyInitializer<T>) = apply {
        testContext.dependencies.provide(T::class, initializer)
    }

    /**
     * Provides an instance of [T] to sweetest that is automatically instantiated using the default
     * constructor and the built-in dependency injection.
     *
     * Note:
     *  Require functions like [requireReal], [requireMock], etc. cannot be used on a type that uses
     *  [provide]. [provide] automatically requires that the automatically created instance is used.
     */
    inline fun <reified T : Any> provide() = apply {
        testContext.dependencies.provide(T::class)
    }

    inline fun <reified T : Steps> requireSteps() = apply {
        testContext.steps.setUpAsRequired(T::class as KClass<Steps>)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireReal() = apply {
        testContext.dependencies.requireReal(T::class)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerReal(noinline initializer: DependencyInitializer<T>) = apply {
        testContext.dependencies.offerReal(T::class, initializer)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerRealRequired(noinline initializer: DependencyInitializer<T>) =
        apply {
            testContext.dependencies.offerRealRequired(T::class, initializer)
        }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireMock() = apply {
        testContext.dependencies.requireMock(T::class)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerMock(noinline initializer: DependencyInitializer<T>) = apply {
        testContext.dependencies.offerMock(T::class, initializer)
    }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> offerMockRequired(noinline initializer: DependencyInitializer<T>) =
        apply {
            testContext.dependencies.offerMockRequired(T::class, initializer)
        }

    @Deprecated(dependencyModeDeprecationMessage, replaceWith = ReplaceWith("provide"))
    inline fun <reified T : Any> requireSpy() = apply {
        testContext.dependencies.requireSpy(T::class)
    }

    inline fun <reified R : Any> offerFactory(noinline createObject: () -> R) = apply {
        testContext.factories.configure(FactoryRunner0(R::class.java, createObject))
    }

    inline fun <reified T : Steps, reified R : Any> offerFactory(noinline createObject: (T) -> R) =
        apply {
            testContext.factories.configure(
                FactoryRunner1(
                    R::class.java,
                    T::class.java,
                    createObject
                )
            )
        }

    inline fun <reified T1 : Steps, reified T2 : Steps, reified R : Any> offerFactory(
        noinline createObject: (T1, T2) -> R
    ) = apply {
        testContext.factories.configure(
            FactoryRunner2(
                R::class.java,
                T1::class.java,
                T2::class.java,
                createObject
            )
        )
    }

    inline fun <reified T1 : Steps, reified T2 : Steps, reified T3 : Steps, reified R : Any> offerFactory(
        noinline createObject: (T1, T2, T3) -> R
    ) = apply {
        testContext.factories.configure(
            FactoryRunner3(
                R::class.java, T1::class.java, T2::class.java, T3::class.java,
                createObject
            )
        )
    }

    fun onInitializeDependencies(run: () -> Unit) = apply {
        checkNotYetBuilt()
        testContext.workflow.subscribe(INITIALIZE_DEPENDENCIES, run)
    }

    fun onSetUp(run: () -> Unit) = apply {
        checkNotYetBuilt()
        testContext.workflow.subscribe(SET_UP, run)
    }

    // TODO onTearDown
}
