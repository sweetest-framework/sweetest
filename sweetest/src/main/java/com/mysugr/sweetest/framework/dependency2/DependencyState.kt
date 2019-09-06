package com.mysugr.sweetest.framework.dependency2

import com.mysugr.sweetest.framework.base2.internal.dependency.DependencyInitializer
import kotlin.reflect.KClass

class DependencyState(val type: KClass<*>, val initializer: DependencyInitializer<*>) {
    var instance: Any? = null
}