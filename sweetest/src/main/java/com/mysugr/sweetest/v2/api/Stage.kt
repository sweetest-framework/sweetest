package com.mysugr.sweetest.v2.api

/**
 * Base class for stages, which by definition can contain:
 * - [Stage]s
 * - [Actor]s
 * Corollary, stages can contain stages, but don't have to.
 */
abstract class Stage {

    fun <T : Stage> stage(): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }

    /**
     * Makes sure the type of actor is initialized
     * If in the actor a type is bound to act on a real or mock instance, it's then defined so
     * The resulting instance can be consumed via the delegated property
     */
    fun <T : Actor> actor(): InstancePropertyDelegate<T> {
        throw NotImplementedError()
    }

    operator fun <T : Any> T.invoke(block: T.() -> Unit) {
        block()
    }

    @JvmName("invokeNullable")
    inline operator fun <T : Any> T?.invoke(block: T.() -> Unit) {
        requireNotNull(this)
        block()
    }

    inline infix fun <T : Actor> T?.maybe(block: T.() -> Unit) {
        this?.block()
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
