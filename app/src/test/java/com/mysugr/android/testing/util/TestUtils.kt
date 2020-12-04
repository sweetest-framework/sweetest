/**
 * Kotlin nullability fix utilities for Mockito
 */

package com.mysugr.android.testing.util

import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
val <T> T?.nonNullable: T get() = this ?: (null as T)

fun <T : Any> nonNullableAny() = Mockito.any<T>().nonNullable
