package com.mysugr.testing.util

import org.mockito.Mockito
import org.mockito.internal.util.MockUtil

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

val Any.isMock get() = MockUtil.isMock(this)

val Any.isSpy get() = MockUtil.isSpy(this)