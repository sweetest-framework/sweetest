package com.mysugr.sweetest.framework.dependency2

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class DependencyInstantiator(val dependenciesProvider: DependenciesProvider) {

    fun <T : Any> instantiate(type: KClass<T>): T {
        val constructor = getConstructor(type)
        val arguments = getArgumentsFor(constructor)
        return constructor.call(*arguments.toTypedArray())
    }

    private fun <T : Any> getConstructor(type: KClass<T>): KFunction<T> {
        val constructors = type.constructors
        return when {
            type.constructors.isEmpty() -> throw IllegalArgumentException("Can't " +
                    "auto-instantiate class with no constructors")
            constructors.size > 1 -> throw IllegalArgumentException("Can't auto-instantiate " +
                    "class with ambiguous constructors (> 1)")
            else -> constructors.first()
        }
    }

    private fun <T : Any> getArgumentsFor(constructor: KFunction<T>): List<Any> {
        return constructor.parameters.map { parameter ->
            val requestedType = parameter.type.classifier as KClass<*>
            dependenciesProvider.getInstanceOf(requestedType)
        }
    }
}