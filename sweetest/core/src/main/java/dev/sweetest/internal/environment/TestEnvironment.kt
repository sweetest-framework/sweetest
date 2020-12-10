package dev.sweetest.internal.environment

import dev.sweetest.internal.dependency.DependencyConfiguration
import dev.sweetest.internal.dependency.DependencyConfigurationConsumer
import dev.sweetest.internal.dependency.DependencyManager
import dev.sweetest.internal.dependency.DependencyMode
import dev.sweetest.internal.dependency.DependencyProvider
import dev.sweetest.internal.dependency.DependencySetup
import dev.sweetest.internal.dependency.DependencyState
import dev.sweetest.internal.dependency.DependencyStatesConsumer
import kotlin.reflect.KClass

internal object TestEnvironment {

    private lateinit var _dependencies: DependencyManager
    private lateinit var dependenciesController: DependencyManager.Controller

    internal val dependencies: DependencyAccessor get() = _dependencies

    init {
        setUpDependencyManager()
    }

    private fun setUpDependencyManager() {
        _dependencies = DependencyManager(
            setupHandlerReceiver = { DependencySetup.init(it) }
        )
        dependenciesController = DependencyManager.Controller(_dependencies)
    }

    internal fun fullReset() {
        setUpDependencyManager()
    }

    internal fun reset() {
        dependenciesController.resetState()
    }
}

interface DependencySetupHandler {

    fun addConfiguration(configuration: DependencyConfiguration<*>)

    @Deprecated("Use addConfiguration(configuration) instead")
    @Suppress("LongParameterList") // since it is deprecated, we need no additional codacy warnings on this
    fun <T : Any> addConfiguration(
        clazz: KClass<T>,
        realProvider: DependencyProvider<T>? = null,
        mockProvider: DependencyProvider<T>? = null,
        dependencyMode: DependencyMode? = null,
        alias: KClass<*>? = null
    ): DependencyConfiguration<T>
}

internal interface DependencyAccessor {
    val configurations: DependencyConfigurationConsumer
    val states: DependencyStatesConsumer

    /**
     * Returns the dependency state for _consumption_ (e.g. `val instance by dependency<T>()`).
     */
    fun <T : Any> getDependencyState(clazz: KClass<T>): DependencyState<T>

    /**
     * Returns the dependency state for _configuration_ (e.g. `provide<T>()`).
     */
    fun getDependencyStateForConfiguration(clazz: KClass<*>, preciseTypeMatching: Boolean): DependencyState<*>
}
