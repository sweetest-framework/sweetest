package com.mysugr.testing.framework.dependency

import com.mysugr.testing.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependencyConfiguration<T : Any>(
    val clazz: KClass<T>,
    val defaultRealInitializer: DependencyInitializer<T>? = null,
    val defaultMockInitializer: DependencyInitializer<T>?,
    val defaultDependencyMode: DependencyMode? = null
) {

    val instance: T
        get() = TestEnvironment.dependencies.states[this].instance

    override fun toString(): String {
        return clazz.simpleName.toString()
    }
}
