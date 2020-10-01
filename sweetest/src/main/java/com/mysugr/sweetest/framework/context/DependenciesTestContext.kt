package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.configuration.ModuleTestingConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyManager
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireReal(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean = true) {
        checkInvalidLegacyFunctionCall("requireReal", hasModuleTestingConfiguration)
        val mode = DependencyMode.REAL
        checkDependencyMode(clazz, mode)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerReal(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean = true
    ) {
        checkInvalidLegacyFunctionCall("offerReal", hasModuleTestingConfiguration)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            state.realInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerRealRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean = true
    ) {
        checkInvalidLegacyFunctionCall("offerRealRequired", hasModuleTestingConfiguration)
        val mode = DependencyMode.REAL
        checkDependencyMode(clazz, mode)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
            state.realInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    @JvmOverloads
    fun requireMock(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean = true) {
        checkInvalidLegacyFunctionCall("requireMock", hasModuleTestingConfiguration)
        val mode = DependencyMode.MOCK
        checkDependencyMode(clazz, mode)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMock(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean = true
    ) {
        checkInvalidLegacyFunctionCall("offerMock", hasModuleTestingConfiguration)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            state.mockInitializerUnknown = initializer
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun offerMockRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean = true
    ) {
        checkInvalidLegacyFunctionCall("offerMockRequired", hasModuleTestingConfiguration)
        val mode = DependencyMode.MOCK
        checkDependencyMode(clazz, mode)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mockInitializerUnknown = initializer
            state.mode = mode
        }
    }

    @Deprecated("Use \"provide\" instead.", replaceWith = ReplaceWith("provide"))
    fun requireSpy(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean = true) {
        checkInvalidLegacyFunctionCall("requireSpy", hasModuleTestingConfiguration)
        val mode = DependencyMode.SPY
        checkDependencyMode(clazz, mode)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
        }
    }

    private fun checkInvalidLegacyFunctionCall(functionName: String, hasModuleTestingConfiguration: Boolean) {
        check(hasModuleTestingConfiguration) {
            error(
                "`$functionName` is a legacy function and can't be used " +
                    "when using new API without module testing configuration!"
            )
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
        checkDependencyMode(clazz, mode)
        prepareAndUseDependencyOf(clazz) { state, _ ->
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
        checkDependencyMode(clazz, mode)
        prepareAndUseDependencyOf(clazz) { state, _ ->
            state.mode = mode
        }
    }

    /**
     * Checks if the type [clazz] is already added via the global module configuration.
     * If so, the found [DependencyConfiguration] is added to the [DependencyManager.states].
     * If not, a [DependencyConfiguration] is created dynamically and also added.
     */
    private fun prepareAndUseDependencyOf(
        clazz: KClass<*>,
        block: (DependencyState<out Any>, DependencyMode?) -> Unit
    ) {
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
     * Checks if the configured [DependencyConfiguration.defaultDependencyMode] matches the requested [DependencyMode].
     * If no [ModuleTestingConfiguration] is defined, the check succeeds.
     */
    private fun checkConfiguredMode(clazz: KClass<*>, configurationMode: DependencyMode?, mode: DependencyMode) {
        check(configurationMode == mode || configurationMode == null) {
            "Dependency \"${clazz.simpleName}\" has been forced to be ${configurationMode?.name} in the module " +
                "testing configuration, but you requested it to be ${mode.name} instead. Please loosen up the " +
                "constraint by using \"any\" instead of \"requireMock\" or \"requireReal\" or remove the module " +
                "testing configuration entirely as it's deprecated anyway."
        }
    }

    /**
     * Checks if the [DependencyState.mode] of an already added [DependencyState] is requested to be changed,
     * which is not allowed.
     */
    private fun checkDependencyMode(clazz: KClass<*>, mode: DependencyMode) {
        TestEnvironment.dependencies.states.getByDependencyType(clazz)?.let {
            check(mode == it.mode) {
                "Dependency ${clazz.simpleName} is requested as $mode but is already defined as ${it.mode}. " +
                    "Please check for contradicting definitions."
            }
        }
    }
}
