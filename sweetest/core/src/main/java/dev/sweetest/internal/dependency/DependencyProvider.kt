package dev.sweetest.internal.dependency

import com.mysugr.sweetest.internal.DependencyProviderScope

typealias DependencyProvider<T> = DependencyProviderScope.() -> T
