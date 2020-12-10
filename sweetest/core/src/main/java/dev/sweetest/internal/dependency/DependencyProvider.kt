package dev.sweetest.internal.dependency

import dev.sweetest.internal.DependencyProviderScope

typealias DependencyProvider<T> = DependencyProviderScope.() -> T
