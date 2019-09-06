package com.mysugr.sweetest.framework.dependency2

import kotlin.reflect.KClass

interface DependenciesProvider {
    fun <T : Any> getInstanceOf(type: KClass<T>): T
}