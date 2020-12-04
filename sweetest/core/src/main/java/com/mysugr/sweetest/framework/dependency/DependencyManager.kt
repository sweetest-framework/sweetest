package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.internal.Steps
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.environment.DependencyAccessor
import com.mysugr.sweetest.framework.environment.DependencySetupHandler
import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.DependencyProviderScope
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

internal class DependencyManager(
    setupHandlerReceiver: (DependencySetupHandler) -> Unit
) : DependencyAccessor {

    private var _states: DependencyStates? = null
    private val configurationsField = DependencyConfigurations()

    init {
        setupHandlerReceiver(configurationsField)
    }

    override val configurations: DependencyConfigurationConsumer
        get() = configurationsField

    /**
     * Returns the dependency state for _consumption_ (e.g. `val instance by dependency<T>()`).
     */
    override fun <T : Any> getDependencyState(clazz: KClass<T>): DependencyState<T> {
        if (clazz.isSubclassOf(Steps::class)) {
            throw RuntimeException(
                "Steps classes can't be accessed via `dependency<T>`, please " +
                    "use `steps<T>` to access steps classes!"
            )
        }

        return getDependencyStateByClassPrecisely(clazz) ?: getDependencyStateViaConfigurationLoosely(clazz)
    }

    private fun <T : Any> getDependencyStateViaConfigurationLoosely(clazz: KClass<T>): DependencyState<T> {
        val configuration = getDependencyConfigurationLoosely(clazz)

        ensureLooseMatchingIsAllowed(configuration, clazz)

        return states[configuration]
    }

    private fun <T : Any> ensureLooseMatchingIsAllowed(
        configuration: DependencyConfiguration<T>,
        clazz: KClass<T>
    ) {
        if (states.isForcedToPreciseMatching(configuration)) {
            throw SweetestException(
                getNotProvidedErrorMessage(clazz) +
                    "\nLegacy note: you are seeing this error because you use the new `provide` " +
                    "function but there is still an old module testing configuration entry for the type " +
                    "\"${configuration.clazz.simpleName}\". While the old way allowed requesting types that are " +
                    "derived from the configured type (loose matching), the new way doesn't support that anymore " +
                    "(strict matching)."
            )
        }
    }

    private fun <T : Any> getDependencyConfigurationLoosely(clazz: KClass<T>): DependencyConfiguration<T> {
        return (configurations.getAssignableTo(clazz)
            ?: throw SweetestException(
                getNotProvidedErrorMessage(clazz) +
                    "\nLegacy note: adding the type to the module  testing configuration also fixes this problem, " +
                    "but these are deprecated. Please use `provide` instead!"
            ))
    }

    private fun <T : Any> getNotProvidedErrorMessage(clazz: KClass<T>): String {
        return "You requested the dependency \"${clazz.simpleName}\", but it has not been yet provided. Please " +
            "specify how to provide this type by placing a `provide<${clazz.simpleName}>` call at the " +
            "appropriate place."
    }

    private fun <T : Any> getDependencyStateByClassPrecisely(clazz: KClass<T>) = states.getOrNull(clazz)

    /**
     * Returns the dependency state for _configuration_ (e.g. `provide<T>()`).
     */
    override fun getDependencyStateForConfiguration(
        clazz: KClass<*>,
        preciseTypeMatching: Boolean
    ): DependencyState<*> {
        return if (preciseTypeMatching) {
            val dependencyState = TestEnvironment.dependencies.states[clazz]
            forcePreciseTypeMatching(clazz)
            dependencyState
        } else {
            configurations.getAssignableTo(clazz)?.let { configuration ->
                states[configuration]
            } ?: throw SweetestException(
                "Dependency `${clazz.simpleName}` is not configured. Please use `provide` instead of the " +
                    "`require/offer` family of functions."
            )
        }
    }

    /**
     * If there is a configuration that would match with this type: tag it to force precise type matching!
     *
     *  **Reason:** when the user utilizes the "new" precise type matching (`provide<T>`, see provide) for configuring
     *  the dependency here, it makes no sense to use the legacy loose type matching during consumption of the type
     *  later on.
     */
    private fun forcePreciseTypeMatching(clazz: KClass<*>) {
        TestEnvironment.dependencies.configurations.getAssignableTo(clazz)?.let { configuration ->
            TestEnvironment.dependencies.states.setForcedToPreciseMatching(configuration)
        }
    }

    override val states: DependencyStatesConsumer
        get() = if (_states == null) {
            _states = DependencyStates(DependencyProviderScopeInstance)
            _states!!
        } else {
            _states!!
        }

    private object DependencyProviderScopeInstance : DependencyProviderScope() {
        override fun <T : Any> instanceOf(dependencyType: KClass<T>): T {
            return TestEnvironment.dependencies.getDependencyState(dependencyType).instance
        }
    }

    class Controller(private val parent: DependencyManager) {
        fun resetState() {
            parent._states = null
        }
    }
}
