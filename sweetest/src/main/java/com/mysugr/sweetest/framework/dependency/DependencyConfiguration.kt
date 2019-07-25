package com.mysugr.sweetest.framework.dependency

import kotlin.reflect.KClass

class DependencyConfiguration<T : Any>(
    val clazz: KClass<T>,
    val defaultRealInitializer: DependencyInitializer<T>? = null,
    val defaultMockInitializer: DependencyInitializer<T>?,
    val defaultDependencyMode: DependencyMode? = null
) {
    override fun toString(): String {
        return clazz.simpleName.toString()
    }
}
