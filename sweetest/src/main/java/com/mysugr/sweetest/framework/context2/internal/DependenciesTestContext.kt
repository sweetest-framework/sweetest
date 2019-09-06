package com.mysugr.sweetest.framework.context2.internal

import com.mysugr.sweetest.framework.dependency2.DependencyState
import com.mysugr.sweetest.framework.dependency2.getOrInitializeInstance
import java.lang.IllegalStateException
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class DependenciesTestContext {
    private val dependencyStates = mutableMapOf<KClass<*>, DependencyState>()

    fun hasDependencyStateFor(type: KClass<*>) = dependencyStates.containsKey(type)

    fun <T : Any> getDependencyStateFor(type: KClass<T>): DependencyState {
        return dependencyStates[type] ?: throw IllegalStateException(
            "Dependency state not initialized, please use standard dependency() function to access dependencies!"
        )
    }

    fun assignDependencyStateFor(assignedType: KClass<*>, dependencyState: DependencyState) {
        checkTypeCompatibility(assignedType, dependencyState.type)
        if (dependencyStates.containsKey(assignedType)) {
            throw IllegalArgumentException("State for dependency type \"${assignedType.simpleName}\" is already present.")
        }
        dependencyStates[assignedType] = dependencyState
    }
}



fun <T : Any> DependenciesTestContext.consumeDependency(type: KClass<T>): T =
    getDependencyStateFor(type).getOrInitializeInstance(type)

private fun checkTypeCompatibility(assignedType: KClass<*>, dependencyType: KClass<*>) {
    if (assignedType != dependencyType && !assignedType.isSuperclassOf(dependencyType)) {
        throw IllegalArgumentException(
            "You are trying to assign a dependency of type \"${dependencyType.simpleName}\" to " +
                "\"${assignedType.simpleName}\", which is not possible."
        )
    }
}