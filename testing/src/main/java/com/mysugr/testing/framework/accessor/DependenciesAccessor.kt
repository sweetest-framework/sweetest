package com.mysugr.testing.framework.accessor

import com.mysugr.testing.framework.dependency.DependencyInitializer

class DependenciesAccessor(@PublishedApi internal val parent: BaseAccessor) {

    inline fun <reified T : Any> requireReal() {
        parent.testContext.dependencies.requireReal(T::class)
    }

    inline fun <reified T : Any> offerReal(noinline initializer: DependencyInitializer<T>) {
        parent.testContext.dependencies.offerReal(T::class, initializer)
    }

    inline fun <reified T : Any> offerRealRequired(noinline initializer: DependencyInitializer<T>) {
        parent.testContext.dependencies.offerRealRequired(T::class, initializer)
    }

    inline fun <reified T : Any> requireMock() {
        parent.testContext.dependencies.requireMock(T::class)
    }

    inline fun <reified T : Any> offerMock(noinline initializer: DependencyInitializer<T>) {
        parent.testContext.dependencies.offerMock(T::class, initializer)
    }

    inline fun <reified T : Any> offerMockRequired(noinline initializer: DependencyInitializer<T>) {
        parent.testContext.dependencies.offerMockRequired(T::class, initializer)
    }
}
