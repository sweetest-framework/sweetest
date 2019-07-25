package com.mysugr.sweetest.framework.dependency

import kotlin.reflect.KClass

interface DependencyStatesConsumer {
    val all: Collection<DependencyState<*>>
    operator fun <T : Any> get(clazz: KClass<T>): DependencyState<T>
}

class DependencyStates(
    private val initializerContext: DependencyInitializerContext,
    private val dependencyConfigurationConsumer: DependencyConfigurationConsumer
) : DependencyStatesConsumer {

    private val statesMap = hashMapOf<Class<*>, DependencyState<*>>()

    override val all get() = statesMap.values

    override operator fun <T : Any> get(clazz: KClass<T>): DependencyState<T> {
        return getAssignableWith(clazz) ?: createFor(clazz)
    }

    private fun <T : Any> getAssignableWith(clazz: KClass<T>): DependencyState<T>? {
        return statesMap.keys.find { clazz.java.isAssignableFrom(it) }
            ?.let { finalClass ->
                statesMap[finalClass] as DependencyState<T>?
            }
    }

    private fun <T : Any> createFor(clazz: KClass<T>): DependencyState<T> {
        val configuration = dependencyConfigurationConsumer.tryGetAssignableWith(clazz)
        val newState = DependencyState(clazz, initializerContext, configuration)
        statesMap[clazz.java] = newState
        return newState
    }
}
