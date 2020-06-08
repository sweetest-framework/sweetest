package com.mysugr.android.testing.util

@Suppress("UNCHECKED_CAST")
val <T> T?.nonNullable : T get() = (this as? T) ?: (null as T)
