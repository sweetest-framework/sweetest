package com.mysugr.sweetest.framework.dependency2

import kotlin.reflect.KClass

fun DependencyState.checkCorrectTypeOf(instance: Any) {
    if (instance::class != type) {
        throw IllegalStateException(
                "The initializer of dependency \"${type.simpleName}\" returns type ${instance::class.simpleName}, but " +
                        "\"${type.simpleName}\" is expected."
        )
    }
}

fun DependencyState.getInstance(): Any {
    return instance
            ?: throw IllegalStateException("Can't get instance as it hasn't yet been initialized.")
}

fun <T : Any> DependencyState.getInstanceChecked(requestedType: KClass<T>): T {
    if (requestedType != this.type) {
        throw IllegalArgumentException(
                "You're requesting a dependency of type \"${requestedType.simpleName}\", but this state object actually " +
                        "just serves \"${type.simpleName}\"."
        )
    }
    val instance = getInstance()
    checkCorrectTypeOf(instance)
    @Suppress("UNCHECKED_CAST")
    return instance as T
}

val DependencyState.isInitialized: Boolean
    get() = instance != null

