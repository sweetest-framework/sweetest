package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    fun requireReal(clazz: KClass<*>) {
        val mode = DependencyMode.REAL
        addDependency(clazz, mode) { state ->
            state.mode = mode
        }
    }

    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        addDependency(clazz) { state ->
            state.realInitializerUnknown = initializer
        }
    }

    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val mode = DependencyMode.REAL
        addDependency(clazz, mode) { state ->
            state.mode = mode
            state.realInitializerUnknown = initializer
        }
    }

    fun requireMock(clazz: KClass<*>) {
        val mode = DependencyMode.MOCK
        addDependency(clazz, mode) { state ->
            state.mode = mode
        }
    }

    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        addDependency(clazz) { state ->
            state.mockInitializerUnknown = initializer
        }
    }

    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val mode = DependencyMode.MOCK
        addDependency(clazz, mode) { state ->
            state.mockInitializerUnknown = initializer
            state.mode = mode
        }
    }

    fun requireSpy(clazz: KClass<*>) {
        val mode = DependencyMode.SPY
        addDependency(clazz, mode) { state ->
            state.mode = mode
        }
    }

    private fun getDependencyConfiguration(clazz: KClass<*>) =
        TestEnvironment.dependencies.configurations.getAssignableTo(clazz)

    private fun addDependency(
        clazz: KClass<*>,
        mode: DependencyMode? = null,
        block: (DependencyState<out Any>) -> Unit
    ) {
        with(TestEnvironment.dependencies) {
            val dependencyState = getDependencyConfiguration(clazz)?.let { configuration ->
                states[configuration].also {
                    check(it.mode == mode) {
                        "Class $clazz has been defined as ${it.mode.name} but is " +
                            "requested as ${mode?.name}!"
                    }
                }
            } ?: run {
                states[DependencyConfiguration(clazz, null, null, null)]
            }

            block(dependencyState)
        }
    }
}
