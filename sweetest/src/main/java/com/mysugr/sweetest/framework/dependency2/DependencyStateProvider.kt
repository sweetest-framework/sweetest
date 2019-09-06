package com.mysugr.sweetest.framework.dependency2

import kotlin.reflect.KClass

interface DependencyStateProvider {
    fun <T : Any> getDependencyStateFor(type: KClass<T>): DependencyState
}