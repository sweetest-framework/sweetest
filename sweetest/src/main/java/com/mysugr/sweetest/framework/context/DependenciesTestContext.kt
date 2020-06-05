package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    @Deprecated("Use \"provide\" instead.")
    fun requireReal(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.REAL
    }

    @Deprecated("Use \"provide\" instead.")
    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = TestEnvironment.dependencies.configurations.getAssignableFrom(clazz)
        TestEnvironment.dependencies.states[dependency].realInitializerUnknown = initializer
    }

    @Deprecated("Use \"provide\" instead.")
    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            realInitializerUnknown = initializer
            mode = DependencyMode.REAL
        }
    }

    @Deprecated("Use \"provide\" instead.")
    fun requireMock(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.MOCK
    }

    @Deprecated("Use \"provide\" instead.")
    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mockInitializerUnknown = initializer
    }

    @Deprecated("Use \"provide\" instead.")
    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            mockInitializerUnknown = initializer
            mode = DependencyMode.MOCK
        }
    }

    @Deprecated("Use \"provide\" instead.")
    fun requireSpy(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.SPY
    }

    private fun getDependencyConfiguration(clazz: KClass<*>) =
        TestEnvironment.dependencies.configurations.getAssignableTo(clazz)

    fun provide(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            providedInitializerUnknown = initializer
            mode = DependencyMode.PROVIDED
        }
    }

    fun provide(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.REAL
    }
}
