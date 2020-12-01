package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.DependencySetupHandler
import com.mysugr.sweetest.internal.DependencyProvider
import kotlin.reflect.KClass

internal interface DependencyConfigurationConsumer {
    val all: Collection<DependencyConfiguration<*>>
    fun <T : Any> getAssignableTo(clazz: KClass<T>): DependencyConfiguration<T>?
}

internal class DependencyConfigurations : DependencyConfigurationConsumer, DependencySetupHandler {

    private val configurations = linkedMapOf<KClass<*>, DependencyConfiguration<*>>()

    override val all = configurations.values

    override fun addConfiguration(configuration: DependencyConfiguration<*>) {
        if (configurations.containsKey(configuration.clazz)) {
            throw RuntimeException("Configuration for dependency \"${configuration.clazz}\" is already added")
        }
        configurations[configuration.clazz] = configuration
    }

    @Deprecated("Use addConfiguration(config)")
    override fun <T : Any> addConfiguration(
        clazz: KClass<T>,
        realProvider: DependencyProvider<T>?,
        mockProvider: DependencyProvider<T>?,
        dependencyMode: DependencyMode?,
        alias: KClass<*>?
    ): DependencyConfiguration<T> {

        val found = configurations[clazz]
        return if (found == null) {
            val newDependency = DependencyConfiguration(clazz, realProvider, mockProvider, dependencyMode)
            configurations[clazz] = newDependency
            if (alias != null) {
                configurations[alias] = newDependency
            }
            newDependency
        } else {
            throw RuntimeException(
                "Dependency \"$clazz\" already configured! Please make sure " +
                    "you haven't added it to two different Dependencies objects!"
            )
        }
    }

    override fun <T : Any> getAssignableTo(clazz: KClass<T>): DependencyConfiguration<T>? {
        return configurations.values.find { clazz.java.isAssignableFrom(it.clazz.java) }
            as DependencyConfiguration<T>?
    }
}
