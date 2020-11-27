package com.mysugr.sweetest.internal

internal typealias DependencyProvider<T> = (DependencyProviderArgument) -> T

/**
 * Intermediary solution until global state ([TestEnvironment]) is removed and [DependencyProviderArgument] is not
 * late-initialized anymore.
 */
internal typealias DependencyProviderArgumentProvider = () -> DependencyProviderArgument
