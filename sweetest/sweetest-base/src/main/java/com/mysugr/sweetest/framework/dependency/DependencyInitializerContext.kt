package com.mysugr.sweetest.framework.dependency

import kotlin.reflect.KClass

// TODO move to api module
abstract class DependencyInitializerContext {

    // TODO Should not be visible publicly
    abstract fun <T : Any> instanceOf(clazz: KClass<T>): T

    inline fun <reified T : Any> instanceOf() = instanceOf(T::class)
}
