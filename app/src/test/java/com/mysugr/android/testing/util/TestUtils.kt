package com.mysugr.android.testing.util

import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
val <T> T?.nonNullable: T get() = (this as? T) ?: (null as T)

fun <T : Any> nonNullableAny() = Mockito.any<T>().nonNullable
