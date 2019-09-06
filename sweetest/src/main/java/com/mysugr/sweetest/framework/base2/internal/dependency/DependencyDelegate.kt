package com.mysugr.sweetest.framework.base2.internal.dependency

import kotlin.reflect.KProperty

interface DependencyDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        TODO()
    }
}