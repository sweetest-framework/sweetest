package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment
import com.mysugr.sweetest.internal.DependencyProviderScope
import kotlin.reflect.KClass

internal interface DependencyStatesConsumer {
    val all: Collection<DependencyState<*>>

    /**
     * Returns an existing state assigned to the [configuration] or CREATES (⚠️) one if necessary.
     */
    operator fun <T : Any> get(configuration: DependencyConfiguration<T>): DependencyState<T>

    /**
     * Returns a state assigned to the [clazz] or CREATES (⚠️) one if necessary (in this case the state is _not_
     * assigned to a [DependencyConfiguration]!)
     */
    operator fun <T : Any> get(clazz: KClass<T>): DependencyState<T>

    /**
     * Looks up a [DependencyState] for the exact [clazz] or null if not found.
     */
    fun <T : Any> getOrNull(clazz: KClass<T>): DependencyState<T>?

    /**
     * Looks up a all [DependencyState]s for the [clazz] or its subtypes (loose matching).
     */
    fun <T : Any> getAllAssignableTo(clazz: KClass<T>): List<DependencyState<T>>

    /**
     * Tags a [DependencyConfiguration] as forced to precise type matching.
     */
    fun setForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>)

    /**
     * Returns whether the [dependencyConfiguration] was tagged for precise type matching.
     */
    fun isForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>): Boolean
}

internal class DependencyStates(private val dependencyProviderScope: DependencyProviderScope) : DependencyStatesConsumer {

    private val statesMap = linkedMapOf<DependencyConfiguration<*>, DependencyState<*>>()
    private val configurationsForcedToPreciseMatching = hashSetOf<DependencyConfiguration<*>>()

    override fun <T : Any> getAllAssignableTo(clazz: KClass<T>): List<DependencyState<T>> {
        val result = mutableListOf<DependencyState<T>>()
        TestEnvironment.dependencies.configurations.all.forEach {
            if (clazz.java.isAssignableFrom(it.clazz.java)) {
                result.add(get(it) as DependencyState<T>)
            }
        }
        return result
    }

    override val all get() = statesMap.values

    override fun <T : Any> get(configuration: DependencyConfiguration<T>): DependencyState<T> {
        val found = statesMap[configuration]
        return if (found == null) {
            create(configuration)
        } else {
            found as DependencyState<T>
        }
    }

    override fun <T : Any> get(clazz: KClass<T>): DependencyState<T> {
        val dummyConfiguration = DependencyConfiguration(clazz, defaultMockProvider = null)
        return getOrNull(clazz) ?: create(dummyConfiguration)
    }

    private fun <T : Any> create(configuration: DependencyConfiguration<T>): DependencyState<T> {
        val newState = DependencyState(dependencyProviderScope, configuration)
        statesMap[configuration] = newState
        return newState
    }

    override fun <T : Any> getOrNull(clazz: KClass<T>): DependencyState<T>? {
        val key = statesMap.keys.find { it.clazz == clazz }
        return statesMap[key] as? DependencyState<T>
    }

    override fun setForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>) {
        configurationsForcedToPreciseMatching.add(dependencyConfiguration)
    }

    override fun isForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>): Boolean {
        return configurationsForcedToPreciseMatching.contains(dependencyConfiguration)
    }
}
