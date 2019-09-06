package com.mysugr.sweetest.framework.base2.internal.dependency

import kotlin.reflect.KProperty

class DefaultDependencyDelegate<T>(private val getter: () -> T) : DependencyDelegate<T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = getter()
}