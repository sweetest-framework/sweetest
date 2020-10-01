package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

interface DependencyStatesConsumer {
    val all: Collection<DependencyState<*>>

    @Deprecated("Operator access is deprecated", ReplaceWith("getByConfiguration"))
    operator fun <T : Any> get(configuration: DependencyConfiguration<T>): DependencyState<T>
    fun <T : Any> getAllAssignableTo(clazz: KClass<T>): List<DependencyState<T>>
    fun <T : Any> getByConfiguration(configuration: DependencyConfiguration<T>): DependencyState<T>
    fun <T : Any> getByDependencyType(clazz: KClass<T>): DependencyState<T>?
    fun setForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>)
    fun isForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>): Boolean
}

class DependencyStates(private val initializerContext: DependencyInitializerContext) : DependencyStatesConsumer {

    private val statesMap = hashMapOf<DependencyConfiguration<*>, DependencyState<*>>()
    private val suppressedConfigurations = hashSetOf<DependencyConfiguration<*>>()

    override fun <T : Any> getAllAssignableTo(clazz: KClass<T>): List<DependencyState<T>> {
        val result = mutableListOf<DependencyState<T>>()
        TestEnvironment.dependencies.configurations.all.forEach {
            if (clazz.java.isAssignableFrom(it.clazz.java)) {
                result.add(getByConfiguration(it) as DependencyState<T>)
            }
        }
        return result
    }

    override val all get() = statesMap.values

    override fun <T : Any> get(configuration: DependencyConfiguration<T>): DependencyState<T> {
        return getByConfiguration(configuration)
    }

    override fun <T : Any> getByConfiguration(configuration: DependencyConfiguration<T>): DependencyState<T> {
        val found = statesMap[configuration]
        return if (found == null) {
            val newState = DependencyState(initializerContext, configuration)
            statesMap[configuration] = newState
            newState
        } else {
            found as DependencyState<T>
        }
    }

    override fun <T : Any> getByDependencyType(clazz: KClass<T>): DependencyState<T>? {
        val key = statesMap.keys.find { it.clazz == clazz }
        return statesMap[key] as? DependencyState<T>
    }

    override fun setForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>) {
        suppressedConfigurations.add(dependencyConfiguration)
    }

    override fun isForcedToPreciseMatching(dependencyConfiguration: DependencyConfiguration<*>): Boolean {
        return suppressedConfigurations.contains(dependencyConfiguration)
    }
}
