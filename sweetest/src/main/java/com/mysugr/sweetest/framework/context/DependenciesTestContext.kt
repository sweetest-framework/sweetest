package com.mysugr.sweetest.framework.context

import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyManager
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

class DependenciesTestContext {

    fun provide(clazz: KClass<*>, initializer: DependencyInitializer<*>) {
        val mode = DependencyMode.PROVIDED
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = true) { state, _ ->
            state.mode = mode
            state.providedInitializerUnknown = initializer
        }
    }

    fun provide(clazz: KClass<*>) {
        val mode = DependencyMode.REAL
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = true) { state, _ ->
            state.mode = mode
        }
    }

    fun requireReal(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean) {
        checkInvalidLegacyFunctionCall("requireReal", hasModuleTestingConfiguration)
        val mode = DependencyMode.REAL
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = false) { state, _ ->
            state.mode = mode
        }
    }

    fun offerReal(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerReal", hasModuleTestingConfiguration)
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = false) { state, _ ->
            state.realInitializerUnknown = initializer
        }
    }

    fun offerRealRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerRealRequired", hasModuleTestingConfiguration)
        val mode = DependencyMode.REAL
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = false) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
            state.realInitializerUnknown = initializer
        }
    }

    fun requireMock(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean) {
        checkInvalidLegacyFunctionCall("requireMock", hasModuleTestingConfiguration)
        val mode = DependencyMode.MOCK
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = false) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
        }
    }

    fun offerMock(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerMock", hasModuleTestingConfiguration)
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = false) { state, _ ->
            state.mockInitializerUnknown = initializer
        }
    }

    fun offerMockRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>,
        hasModuleTestingConfiguration: Boolean
    ) {
        checkInvalidLegacyFunctionCall("offerMockRequired", hasModuleTestingConfiguration)
        val mode = DependencyMode.MOCK
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = false) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mockInitializerUnknown = initializer
            state.mode = mode
        }
    }

    fun requireSpy(clazz: KClass<*>, hasModuleTestingConfiguration: Boolean) {
        checkInvalidLegacyFunctionCall("requireSpy", hasModuleTestingConfiguration)
        val mode = DependencyMode.SPY
        prepareAndUseDependencyOf(clazz = clazz, preciseTypeMatching = false) { state, _ ->
            // The assertion is omitted to keep compatibility to older versions of this library.
            // checkConfiguredMode(clazz, configurationMode, mode)
            state.mode = mode
        }
    }

    private fun checkInvalidLegacyFunctionCall(functionName: String, hasModuleTestingConfiguration: Boolean) {
        if (!hasModuleTestingConfiguration) {
            throw SweetestException(
                "`$functionName` is a legacy function and can't be used " +
                    "when using new API without module testing configuration!"
            )
        }
    }

    private fun getDependencyConfiguration(clazz: KClass<*>) =
        TestEnvironment.dependencies.configurations.getAssignableTo(clazz)

    /**
     * Checks if the type [clazz] is already added via the global module configuration.
     * If so, the found [DependencyConfiguration] is added to the [DependencyManager.states].
     * If not, a [DependencyConfiguration] is created dynamically and also added.
     */
    private fun prepareAndUseDependencyOf(
        clazz: KClass<*>,
        preciseTypeMatching: Boolean,
        block: (DependencyState<out Any>, DependencyMode?) -> Unit
    ) {
        with(TestEnvironment.dependencies) {
            val dependencyState = if (preciseTypeMatching) {
                val dependencyState = states.getOrNull(clazz) ?: run {
                    val configuration = DependencyConfiguration(clazz, null, null, DependencyMode.PROVIDED)
                    states[configuration]
                }

                val configuration = configurations.getAssignableTo(clazz)
                if (configuration != null) {
                    states.setForcedToPreciseMatching(configuration)
                }

                dependencyState
            } else {
                getDependencyConfiguration(clazz)?.let { configuration ->
                    states[configuration]
                } ?: throw SweetestException(
                    "Dependency `${clazz.simpleName}` is not configured. Please use `provide` instead of the " +
                        "`require/offer` family of functions."
                )
            }

            block(dependencyState, dependencyState.configuration.defaultDependencyMode)
        }
    }

    // --- region: legacy binary compatibility API:

    fun requireReal(clazz: KClass<*>) = requireReal(clazz, hasModuleTestingConfiguration = true)

    fun offerReal(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) = offerReal(clazz, initializer, hasModuleTestingConfiguration = true)

    fun offerRealRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) = offerRealRequired(clazz, initializer, hasModuleTestingConfiguration = true)

    fun requireMock(clazz: KClass<*>) = requireMock(clazz, hasModuleTestingConfiguration = true)

    fun offerMock(clazz: KClass<*>, initializer: DependencyInitializer<*>) =
        offerMock(clazz, initializer, hasModuleTestingConfiguration = true)

    fun offerMockRequired(
        clazz: KClass<*>,
        initializer: DependencyInitializer<*>
    ) = offerMockRequired(clazz, initializer, hasModuleTestingConfiguration = true)

    fun requireSpy(clazz: KClass<*>) = requireSpy(clazz, hasModuleTestingConfiguration = true)

}
