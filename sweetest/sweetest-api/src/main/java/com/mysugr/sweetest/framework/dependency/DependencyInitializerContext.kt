package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.context.DependenciesTestContext
import com.mysugr.sweetest.internal.DependencyInitializerArgument
import kotlin.reflect.KClass

class DependencyInitializerContext(private val dependenciesTestContext: DependenciesTestContext) :
    DependencyInitializerArgument {

    // Public API
    inline fun <reified T : Any> instanceOf() = instanceOf(T::class)

    // Internal API
    @PublishedApi
    internal fun <T : Any> instanceOf(clazz: KClass<T>): T {
        return dependenciesTestContext.getInstanceOf(clazz)
    }
}
