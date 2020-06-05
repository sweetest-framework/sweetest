package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.TestEnvironment
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.reflect

interface DependencyStatesConsumer {
    val all: Collection<DependencyState<*>>
    fun <T : Any> getAllAssignableTo(clazz: KClass<T>): List<DependencyState<T>>
    operator fun <T : Any> get(configuration: DependencyConfiguration<T>): DependencyState<T>
    fun <T : Any> getForConsumptionOf(type: KClass<T>): DependencyState<T>
}

class DependencyStates(private val initializerContext: DependencyInitializerContext) : DependencyStatesConsumer {

    private val statesMap = hashMapOf<DependencyConfiguration<*>, DependencyState<*>>()

    override fun <T : Any> getForConsumptionOf(type: KClass<T>): DependencyState<T> {
        val foundByAlias = statesMap.values.filter { it.aliasType == type }
        return when (foundByAlias.size) {
            0 -> {
                // still uses "unprecise" matching of configured type, but not so bad as global dependency config is going to be deprecated
                val configuration = TestEnvironment.dependencies.configurations.getAssignableTo(type)
                get(configuration)
            }
            1 -> {
                foundByAlias.first() as DependencyState<T>
            }
            else -> error("More than one state found") // TODO tests, edge cases management and better error message
        }
    }

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
