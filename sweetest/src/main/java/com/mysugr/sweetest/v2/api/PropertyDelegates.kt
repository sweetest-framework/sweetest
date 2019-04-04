package com.mysugr.sweetest.v2.api

import kotlin.reflect.KProperty

open class InstancePropertyDelegate<out T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        throw NotImplementedError()
    }
}

class DependencyPropertyDelegate<out T : Any> : InstancePropertyDelegate<T>() {

    /**
     * Puts the object supplied by the delegate to be used in the dependency management also through the specified type
     * [X].
     */
    inline fun <reified X : Any> alsoAs(): DependencyPropertyDelegate<T> {
        throw NotImplementedError()
    }
}
