package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.context.DependenciesTestContext
import com.mysugr.sweetest.internal.DependencyProviderArgument
import com.mysugr.sweetest.usecases.getDependencyInstance
import kotlin.reflect.KClass
import com.mysugr.sweetest.internal.DependencyProvider as CoreDependencyProvider

typealias DependencyProvider<T> = DependencyProviderScope.() -> T

class DependencyProviderScope(private val dependenciesTestContext: DependenciesTestContext) :
    DependencyProviderArgument {

    // Public API (the following inline functions should just be wrappers over implementation functions!)

    inline fun <reified T : Any> instanceOf() = instanceOf(T::class)

    // Internal API

    @PublishedApi
    internal fun <T : Any> instanceOf(dependencyType: KClass<T>): T {
        return getDependencyInstance(
            dependenciesTestContext,
            dependencyType = dependencyType
        )
    }
}

fun <T> DependencyProvider<T>.asCoreDependencyProvider(): CoreDependencyProvider<T> =
    { argument -> this(argument as DependencyProviderScope) }
