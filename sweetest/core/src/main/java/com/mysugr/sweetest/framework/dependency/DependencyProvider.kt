package com.mysugr.sweetest.framework.dependency

import com.mysugr.sweetest.internal.DependencyProviderScope

typealias DependencyProvider<T> = DependencyProviderScope.() -> T
