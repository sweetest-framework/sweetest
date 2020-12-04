package dev.sweetest.internal

import kotlin.reflect.KClass

abstract class DependencyProviderScope @InternalSweetestApi constructor() {

    // Public API (the inline function should just be a wrapper over the implementation function!)

    inline fun <reified T : Any> instanceOf() = instanceOfInternal(T::class)

    // Internal API

    // Need @PublishedApi as otherwise public to user and invisible to inline function
    @PublishedApi
    internal fun <T : Any> instanceOfInternal(dependencyType: KClass<T>): T = instanceOf(dependencyType)

    // Needed as otherwise internal function can't be overridden
    protected abstract fun <T : Any> instanceOf(dependencyType: KClass<T>): T
}
