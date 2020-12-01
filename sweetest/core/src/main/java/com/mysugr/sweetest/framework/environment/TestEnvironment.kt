package com.mysugr.sweetest.framework.environment

import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyConfigurationConsumer
import com.mysugr.sweetest.internal.DependencyProvider
import com.mysugr.sweetest.framework.dependency.DependencyManager
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencySetup
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.dependency.DependencyStatesConsumer
import com.mysugr.sweetest.internal.DependencyProviderArgument
import kotlin.reflect.KClass

internal object TestEnvironment {

    private lateinit var _dependencies: DependencyManager
    private lateinit var dependenciesController: DependencyManager.Controller
    private lateinit var dependencyProviderArgument: DependencyProviderArgument

    internal val dependencies: DependencyAccessor get() = _dependencies

    init {
        setUpDependencyManager()
    }

    internal fun initializeDependencies(dependencyProviderArgument: DependencyProviderArgument) {
        this.dependencyProviderArgument = dependencyProviderArgument
    }

    private fun setUpDependencyManager() {
        _dependencies = DependencyManager(
            setupHandlerReceiver = { DependencySetup.init(it) },
            dependencyProviderArgumentProvider = { dependencyProviderArgument }
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
