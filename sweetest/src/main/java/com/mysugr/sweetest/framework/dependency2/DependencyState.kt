package com.mysugr.sweetest.framework.dependency2

import kotlin.reflect.KClass

class DependencyState(val type: KClass<*>, val initializer: () -> Any) {
    var instance: Any? = null
}