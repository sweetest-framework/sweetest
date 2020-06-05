package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.reflect

class DependencyConfiguration<T : Any>(
    val clazz: KClass<T>,
    val defaultRealInitializer: DependencyInitializer<T>? = null,
    val defaultMockInitializer: DependencyInitializer<T>?,
    val defaultDependencyMode: DependencyMode? = null
) {

    // TODO remove
    val instance: T
        get() = TestEnvironment.dependencies.states[this].instance

    override fun toString(): String {
        return clazz.simpleName.toString()
    }
}
