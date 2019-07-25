package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    fun requireReal(clazz: KClass<*>) {
        TestEnvironment.dependencies.states[clazz].mode = DependencyMode.REAL
    }

    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        TestEnvironment.dependencies.states[clazz].realInitializerUnknown = initializer
    }

    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        TestEnvironment.dependencies.states[clazz].run {
            realInitializerUnknown = initializer
            mode = DependencyMode.REAL
        }
    }

    fun requireMock(clazz: KClass<*>) {
        TestEnvironment.dependencies.states[clazz].mode = DependencyMode.MOCK
    }

    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        TestEnvironment.dependencies.states[clazz].mockInitializerUnknown = initializer
    }

    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        TestEnvironment.dependencies.states[clazz].run {
            mockInitializerUnknown = initializer
            mode = DependencyMode.MOCK
        }
    }

    fun requireSpy(clazz: KClass<*>) {
        TestEnvironment.dependencies.states[clazz].mode = DependencyMode.SPY
    }
}
