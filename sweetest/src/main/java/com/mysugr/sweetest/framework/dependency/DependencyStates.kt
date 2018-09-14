package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass

interface DependencyStatesConsumer {
    val all: Collection<DependencyState<*>>
    fun <T : Any> getAllAssignableTo(clazz: KClass<T>): List<DependencyState<T>>
    operator fun <T : Any> get(configuration: DependencyConfiguration<T>): DependencyState<T>
}

class DependencyStates(private val initializerContext: DependencyInitializerContext) : DependencyStatesConsumer {

    private val statesMap = hashMapOf<DependencyConfiguration<*>, DependencyState<*>>()

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

    override operator fun <T : Any> get(configuration: DependencyConfiguration<T>):
        DependencyState<T> {
        val found = statesMap[configuration]
        return if (found == null) {
            val newState = DependencyState(initializerContext, configuration)
            statesMap[configuration] = newState
            newState
        } else {
            found as DependencyState<T>
        }
    }
}
