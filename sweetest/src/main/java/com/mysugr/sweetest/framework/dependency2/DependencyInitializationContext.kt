package com.mysugr.sweetest.framework.dependency2

import com.mysugr.sweetest.framework.base2.internal.dependency.DependencyInitializerReceiver

interface DependencyInitializationContext {
    val initializerReceiver: DependencyInitializerReceiver<*>
}