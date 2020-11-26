package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.DependencyInitializer
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
