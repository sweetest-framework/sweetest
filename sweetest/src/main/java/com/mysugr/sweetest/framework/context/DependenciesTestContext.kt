package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyManager
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireReal(clazz: KClass<*>) {
        val mode = DependencyMode.REAL
        assertDependencyMode(clazz, mode)
        addDependency(clazz) { state, configurationMode ->
            assertConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerReal(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        addDependency(clazz) { state, _ ->
            state.realInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerRealRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val mode = DependencyMode.REAL
        assertDependencyMode(clazz, mode)
        addDependency(clazz) { state, configurationMode ->
            assertConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
            state.realInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireMock(clazz: KClass<*>) {
        val mode = DependencyMode.MOCK
        assertDependencyMode(clazz, mode)
        addDependency(clazz) { state, configurationMode ->
            assertConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        addDependency(clazz) { state, _ ->
            state.mockInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMockRequired(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val mode = DependencyMode.MOCK
        assertDependencyMode(clazz, mode)
        addDependency(clazz) { state, configurationMode ->
            assertConfiguredMode(clazz, configurationMode, mode)
            state.mockInitializerUnknown = initializer
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireSpy(clazz: KClass<*>) {
        val mode = DependencyMode.SPY
        assertDependencyMode(clazz, mode)
        addDependency(clazz) { state, configurationMode ->
            assertConfiguredMode(clazz, configurationMode, mode)
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
        val mode = DependencyMode.PROVIDED
        assertDependencyMode(clazz, mode)
        addDependency(clazz) { state, _ ->
            state.providedInitializerUnknown = initializer
            state.mode = mode
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
        val mode = DependencyMode.REAL
        assertDependencyMode(clazz, mode)
        addDependency(clazz) { state, _ ->
            state.mode = mode
        }
    }

    /**
     * Checks if the type [clazz] is already added via the global module configuration.
     * If so, the found [DependencyConfiguration] is added to the [DependencyManager.states].
     * If not, a [DependencyConfiguration] is created dynamically and also added.
     */
    private fun addDependency(clazz: KClass<*>, block: (DependencyState<out Any>, DependencyMode?) -> Unit) {
        with(TestEnvironment.dependencies) {
            val dependencyState = getDependencyConfiguration(clazz)?.let { configuration ->
                return@let states.getByConfiguration(configuration)
            } ?: run {
                val configuration = DependencyConfiguration(clazz, null, null, null)
                return@run states.getByConfiguration(configuration)
            }
            block(dependencyState, dependencyState.configuration.defaultDependencyMode)
        }
    }

    /**
     * Asserts if the configured mode matches the requested one.
     */
    private fun assertConfiguredMode(clazz: KClass<*>, configurationMode: DependencyMode?, mode: DependencyMode) {
        check(configurationMode == mode || configurationMode == null) {
            "Dependency $clazz has been configured as ${configurationMode?.name} but is requested as ${mode.name}! "
        }
    }

    /**
     * Asserts if the [DependencyState.mode] of an already added [DependencyState] has changed.
     */
    private fun assertDependencyMode(clazz: KClass<*>, mode: DependencyMode) {
        TestEnvironment.dependencies.states.getByDependencyType(clazz)?.let {
            check(mode == it.mode) {
                "Dependency ${clazz.simpleName} is requested as $mode but is already defined as ${it.mode}. " +
                    "Please check for contradicting definitions."
            }
        }
    }
}
