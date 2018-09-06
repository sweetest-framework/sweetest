package com.mysugr.testing.framework.build

import com.mysugr.testing.framework.accessor.BaseAccessor
import com.mysugr.testing.framework.base.Steps
import com.mysugr.testing.framework.configuration.ModuleTestingConfiguration
import com.mysugr.testing.framework.context.TestContext
import com.mysugr.testing.framework.dependency.DependencyInitializer
import com.mysugr.testing.framework.factory.FactoryRunner0
import com.mysugr.testing.framework.factory.FactoryRunner1
import com.mysugr.testing.framework.factory.FactoryRunner2
import com.mysugr.testing.framework.factory.FactoryRunner3
import com.mysugr.testing.framework.flow.InitializationStep.INITIALIZE_DEPENDENCIES
import com.mysugr.testing.framework.flow.InitializationStep.SET_UP
import kotlin.reflect.KClass

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

    inline fun <reified T : Steps> requireSteps() = apply {
        testContext.steps.setUpAsRequired(T::class as KClass<Steps>)
    }

    inline fun <reified T : Any> requireReal() = apply {
        testContext.dependencies.requireReal(T::class)
    }

    inline fun <reified T : Any> offerReal(noinline initializer: DependencyInitializer<T>) = apply {
        testContext.dependencies.offerReal(T::class, initializer)
    }

    inline fun <reified T : Any> offerRealRequired(noinline initializer: DependencyInitializer<T>) = apply {
        testContext.dependencies.offerRealRequired(T::class, initializer)
    }

    inline fun <reified T : Any> requireMock() = apply {
        testContext.dependencies.requireMock(T::class)
    }

    inline fun <reified T : Any> offerMock(noinline initializer: DependencyInitializer<T>) = apply {
        testContext.dependencies.offerMock(T::class, initializer)
    }

    inline fun <reified T : Any> offerMockRequired(noinline initializer: DependencyInitializer<T>) = apply {
        testContext.dependencies.offerMockRequired(T::class, initializer)
    }

    inline fun <reified T : Any> requireSpy() = apply {
        testContext.dependencies.requireSpy(T::class)
    }

    inline fun <reified R : Any> offerFactory(noinline createObject: () -> R) = apply {
        testContext.factories.configure(FactoryRunner0(R::class.java, createObject))
    }

    inline fun <reified T : Steps, reified R : Any> offerFactory(noinline createObject: (T) -> R) = apply {
        testContext.factories.configure(FactoryRunner1(R::class.java, T::class.java, createObject))
    }

    inline fun <reified T1 : Steps, reified T2 : Steps, reified R : Any> offerFactory(
        noinline createObject: (T1, T2) -> R
    ) = apply {
        testContext.factories.configure(FactoryRunner2(R::class.java, T1::class.java, T2::class.java, createObject))
    }

    inline fun <reified T1 : Steps, reified T2 : Steps, reified T3 : Steps, reified R : Any> offerFactory(
        noinline createObject: (T1, T2, T3) -> R
    ) = apply {
        testContext.factories.configure(FactoryRunner3(R::class.java, T1::class.java, T2::class.java, T3::class.java,
            createObject))
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
