package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

interface DependencyStatesConsumer {
    val all: Collection<DependencyState<*>>

    /**
     * Returns an existing state assigned to the [configuration] or CREATES (⚠️) one if necessary.
     */
    operator fun <T : Any> get(configuration: DependencyConfiguration<T>): DependencyState<T>

    /**
     * Looks up a [DependencyState] for the exact [clazz] (precise matching) or null if not found.
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

class DependencyStates(private val initializerContext: DependencyInitializerContext) : DependencyStatesConsumer {

    private val statesMap = hashMapOf<DependencyConfiguration<*>, DependencyState<*>>()
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
            val newState = DependencyState(initializerContext, configuration)
            statesMap[configuration] = newState
            newState
        } else {
            found as DependencyState<T>
        }
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
