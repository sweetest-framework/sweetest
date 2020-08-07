package com.mysugr.sweetest.framework.dependency

import kotlin.reflect.KClass

abstract class DependencyInitializerScope {

    @PublishedApi
    internal abstract fun <T : Any> instanceOf(clazz: KClass<T>): T

    inline fun <reified T : Any> instanceOf() = instanceOf(T::class)
}