package com.mysugr.sweetest.framework.base2.internal.dependency

import com.mysugr.sweetest.framework.dependency2.DependenciesProvider

typealias DependencyInitializer<T> = DependencyInitializerReceiver<T>.() -> T

class DependencyInitializerReceiver<T : Any>(
        @PublishedApi internal val dependenciesSource: DependenciesProvider) {
    inline fun <reified T : Any> instanceOf() = dependenciesSource.getInstanceOf(T::class)
}