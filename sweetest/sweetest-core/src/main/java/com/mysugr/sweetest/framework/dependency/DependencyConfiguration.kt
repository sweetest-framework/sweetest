package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.internal.DependencyProvider
import kotlin.reflect.KClass

class DependencyConfiguration<T : Any>(
    val clazz: KClass<T>,
    val defaultRealInitializer: DependencyProvider<T>? = null,
    val defaultMockInitializer: DependencyProvider<T>?,
    val defaultDependencyMode: DependencyMode? = null
) {

    override fun toString(): String {
        return clazz.simpleName.toString()
    }
}
