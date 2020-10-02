package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.base.SweetestException
import com.mysugr.sweetest.framework.environment.DependencyAccessor
import com.mysugr.sweetest.framework.environment.DependencySetupHandler
import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class DependencyManager(setupHandlerReceiver: (DependencySetupHandler) -> Unit) : DependencyAccessor {

    private var _states: DependencyStates? = null
    private val configurationsField = DependencyConfigurations()

    init {
        setupHandlerReceiver(configurationsField)
    }

    override val configurations: DependencyConfigurationConsumer
        get() = configurationsField

    private val initializerContext = object : DependencyInitializerContext() {
        override fun <T : Any> instanceOf(clazz: KClass<T>): T {
            return getDependencyState(clazz).instance
        }
    }

    /**
     * Returns the dependency state for _consumption_ (e.g. `val instance by dependency<T>()`).
     */
    override fun <T : Any> getDependencyState(clazz: KClass<T>): DependencyState<T> {
        if (clazz.isSubclassOf(BaseSteps::class)) {
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
            if (configuration.clazz != clazz) {
                throw SweetestException( // TODO add extra test case for that
                    "There is a dependency \"${configuration.clazz.simpleName}\" configured in the module " +
                        "testing configuration, but you are requesting type \"${clazz.simpleName}\". To avoid " +
                        "ambiguities please specify \"provide<${clazz.simpleName}>...\" explicitly!"
                )
            } else {
                throw SweetestException(
                    "There is a dependency \"${configuration.clazz.simpleName}\" configured in the module " +
                        "testing configuration, but as there is a chance for ambiguities between " +
                        "different types you have to specify \"provide<${clazz.simpleName}>...\" " +
                        "explicitly nonetheless!"
                )
            }
        }
    }

    private fun <T : Any> getDependencyConfigurationLoosely(clazz: KClass<T>): DependencyConfiguration<T> {
        return (configurations.getAssignableTo(clazz)
            ?: throw SweetestException(
                "No configuration for \"${clazz.simpleName}\" found! Please configure the type by using " +
                    "`provide<${clazz.simpleName}>...`.\nLegacy note: Adding the type to the module " +
                    "testing configuration also fixes this problem, but these are deprecated. Please use " +
                    "`provide` in your test and steps classes instead!"
            ))
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
     *  **Reason:** when the user utilizes the "new" precise type matching (`provide<T>`, see [provide]) for configuring
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
            _states = DependencyStates(initializerContext)
            _states!!
        } else {
            _states!!
        }

    class Controller(private val parent: DependencyManager) {
        fun resetState() {
            parent._states = null
        }
    }
}
