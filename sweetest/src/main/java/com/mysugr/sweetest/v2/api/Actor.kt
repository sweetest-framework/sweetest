package com.mysugr.sweetest.v2.api

/**
 * Allows multiple instance of its own type.
 * Allows consumption of any number of dependencies via a call to [dependency].
 * Allows acting on ONE instance of a production class.
 */
abstract class Actor {

    /**
     * Consumes a dependency which is needed for this actor class to function.
     * In the initializer block you have access to dependency management and can initialize stuff around the dependency
     * in whatever way you want.
     */
    fun <T : Any> dependency(setUpInstance: (instance: T) -> Unit = {}): DependencyPropertyDelegate<T> {
        throw NotImplementedError()
    }

    fun onBefore(block: () -> Unit) {
        throw NotImplementedError()
    }

    fun onBeforeAll(block: () -> Unit) {
        throw NotImplementedError()
    }

    fun onAfter(block: () -> Unit) {
        throw NotImplementedError()
    }

    fun onAfterAll(block: () -> Unit) {
        throw NotImplementedError()
    }
}
