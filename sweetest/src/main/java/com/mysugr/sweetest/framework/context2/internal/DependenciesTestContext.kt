package com.mysugr.sweetest.framework.context2.internal

import com.mysugr.sweetest.framework.dependency2.DependencyState
import kotlin.reflect.KClass

class DependenciesTestContext {
    private val dependencyStates = mutableMapOf<KClass<*>, DependencyState>()
}