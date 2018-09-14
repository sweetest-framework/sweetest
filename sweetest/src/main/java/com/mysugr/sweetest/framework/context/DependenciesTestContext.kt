package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    fun requireReal(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.REAL
    }

    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = TestEnvironment.dependencies.configurations.getAssignableFrom(clazz)
        TestEnvironment.dependencies.states[dependency].realInitializerUnknown = initializer
    }

    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            realInitializerUnknown = initializer
            mode = DependencyMode.REAL
        }
    }

    fun requireMock(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.MOCK
    }

    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mockInitializerUnknown = initializer
    }

    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            mockInitializerUnknown = initializer
            mode = DependencyMode.MOCK
        }
    }

    fun requireSpy(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.SPY
    }

    private fun getDependencyConfiguration(clazz: KClass<*>) =
        TestEnvironment.dependencies.configurations.getAssignableTo(clazz)
}
