package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    fun requireReal(clazz: KClass<*>) {
        getDependencyStateFor(clazz).mode = DependencyMode.REAL
    }

    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        // TODO fix inconsistence and check effect on tests (should use getDependencyConfiguration()!)
        getDependencyStateFor(clazz).realInitializerUnknown = initializer
    }

    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        getDependencyStateFor(clazz).run {
            realInitializerUnknown = initializer
            mode = DependencyMode.REAL
        }
    }

    fun requireMock(clazz: KClass<*>) {
        getDependencyStateFor(clazz).mode = DependencyMode.MOCK
    }

    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        getDependencyStateFor(clazz).mockInitializerUnknown = initializer
    }

    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        getDependencyStateFor(clazz).run {
            mockInitializerUnknown = initializer
            mode = DependencyMode.MOCK
        }
    }

    fun requireSpy(clazz: KClass<*>) {
        getDependencyStateFor(clazz).mode = DependencyMode.SPY
    }

    private fun getDependencyStateFor(clazz: KClass<*>) =
        TestEnvironment.dependencies.states.getForConsumptionOf(clazz)
}
