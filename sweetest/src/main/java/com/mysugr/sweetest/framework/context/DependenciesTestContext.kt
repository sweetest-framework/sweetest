package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireReal(clazz: KClass<*>) {
        val mode = DependencyMode.REAL
        addDependency(clazz, mode) { state ->
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        addDependency(clazz) { state ->
            state.realInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val mode = DependencyMode.REAL
        addDependency(clazz, mode) { state ->
            state.mode = mode
            state.realInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireMock(clazz: KClass<*>) {
        val mode = DependencyMode.MOCK
        addDependency(clazz, mode) { state ->
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        addDependency(clazz) { state ->
            state.mockInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val mode = DependencyMode.MOCK
        addDependency(clazz, mode) { state ->
            state.mockInitializerUnknown = initializer
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireSpy(clazz: KClass<*>) {
        val mode = DependencyMode.SPY
        addDependency(clazz, mode) { state ->
            state.mode = mode
        }
    }

    private fun getDependencyConfiguration(clazz: KClass<*>) =
        TestEnvironment.dependencies.configurations.getAssignableTo(clazz)

    /**
     * Provides an [initializer] for type [clazz] to sweetest.
     * That [initializer] will be used when an instance of [clazz] is needed in the test.
     *
     * Note:
     *  Require functions like [requireReal], [requireMock], etc. cannot be used on a type that uses
     *  [provide]. [provide] automatically requires that the [initializer] is used.
     */
    fun provide(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].run {
            providedInitializerUnknown = initializer
            mode = DependencyMode.PROVIDED
        }
    }

    /**
     * Provides an instance of [clazz] to sweetest that is automatically instantiated using the default
     * constructor and the built-in dependency injection.
     *
     * Note:
     *  Require functions like [requireReal], [requireMock], etc. cannot be used on a type that uses
     *  [provide]. [provide] automatically requires that the automatically created instance is used.
     */
    fun provide(clazz: KClass<*>) {
        val dependency = getDependencyConfiguration(clazz)
        TestEnvironment.dependencies.states[dependency].mode = DependencyMode.REAL
    }

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
