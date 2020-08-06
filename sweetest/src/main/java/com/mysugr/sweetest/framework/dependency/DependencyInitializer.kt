package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.framework.environment.DependencyAccessor

typealias DependencyInitializer<T> = DependencyAccessor.() -> T
