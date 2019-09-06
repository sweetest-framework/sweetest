package com.mysugr.sweetest.framework.dependency2

import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class DependencyStateStore : DependencyStateProvider {

    private val dependencyStates = mutableMapOf<KClass<*>, DependencyState>()

    fun hasDependencyStateFor(type: KClass<*>) = dependencyStates.containsKey(type)

    override fun <T : Any> getDependencyStateFor(type: KClass<T>): DependencyState {
        return dependencyStates[type] ?: throw IllegalStateException(
                "Dependency state not initialized, please use standard dependency() function to " +
                        " access dependencies!"
        )
    }

    fun assignDependencyStateFor(assignedType: KClass<*>, dependencyState: DependencyState) {
        checkTypeCompatibility(assignedType, dependencyState.type)
        if (dependencyStates.containsKey(assignedType)) {
            throw IllegalArgumentException("State for dependency type " +
                    "\"${assignedType.simpleName}\" is already present.")
        }
        dependencyStates[assignedType] = dependencyState
    }
}

private fun checkTypeCompatibility(assignedType: KClass<*>, dependencyType: KClass<*>) {
    if (assignedType != dependencyType && !assignedType.isSuperclassOf(dependencyType)) {
        throw IllegalArgumentException(
                "You are trying to assign a dependency of type \"${dependencyType.simpleName}\" " +
                        "to \"${assignedType.simpleName}\", which is not possible."
        )
    }
}