package com.mysugr.sweetest.framework.environment

import com.mysugr.sweetest.framework.dependency.DependencyConfiguration
import com.mysugr.sweetest.framework.dependency.DependencyConfigurationConsumer
import com.mysugr.sweetest.framework.dependency.DependencyInitializer
import com.mysugr.sweetest.framework.dependency.DependencyManager
import com.mysugr.sweetest.framework.dependency.DependencyMode
import com.mysugr.sweetest.framework.dependency.DependencySetup
import com.mysugr.sweetest.framework.dependency.DependencyState
import com.mysugr.sweetest.framework.dependency.DependencyStatesConsumer
import kotlin.reflect.KClass

object TestEnvironment {

    private lateinit var _dependencies: DependencyManager

    private lateinit var dependenciesController: DependencyManager.Controller

    val dependencies: DependencyAccessor get() = _dependencies

    init {
        setUpDependencyManager()
    }

    private fun setUpDependencyManager() {
        _dependencies = DependencyManager {
            DependencySetup.init(it)
        }
        dependenciesController = DependencyManager.Controller(_dependencies)
    }

    fun fullReset() {
        setUpDependencyManager()
    }

    fun reset() {
        dependenciesController.resetState()
    }
}

interface DependencySetupHandler {

    fun addConfiguration(configuration: DependencyConfiguration<*>)

    @Deprecated("Use addConfiguration(configuration) instead")
    // since it is deprecated, we need no additional codacy warnings on this
    @Suppress("LongParameterList")
    fun <T : Any> addConfiguration(
        clazz: KClass<T>,
        realInitializer: DependencyInitializer<T>? = null,
        mockInitializer: (DependencyAccessor.() -> T)? = null,
        dependencyMode: DependencyMode? = null,
        alias: KClass<*>? = null
    ): DependencyConfiguration<T>
}

abstract class DependencyAccessor {
    abstract val configurations: DependencyConfigurationConsumer
    abstract val states: DependencyStatesConsumer

    @PublishedApi
    internal abstract fun <T : Any> instanceOf(clazz: KClass<T>): T
    abstract fun <T : Any> getDependencyState(clazz: KClass<T>): DependencyState<T>
}

inline fun <reified T : Any> DependencyAccessor.instanceOf() = instanceOf(T::class)
inline fun <reified T : Any> DependencyAccessor.getDependencyState() = getDependencyState(T::class)
