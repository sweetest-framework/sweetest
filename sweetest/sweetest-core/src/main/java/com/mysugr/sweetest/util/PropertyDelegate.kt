package com.mysugr.sweetest.util

import com.mysugr.sweetest.internal.CommonBase
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PropertyDelegate<out T>(private val getter: () -> T) : ReadOnlyProperty<CommonBase, T> {
    override operator fun getValue(thisRef: CommonBase, property: KProperty<*>): T = getter()
}
