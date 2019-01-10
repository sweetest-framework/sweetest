package com.mysugr.sweetest.util

import org.junit.Assert.fail
import org.mockito.Mockito
import org.mockito.internal.util.MockUtil

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

val Any.isMock get() = MockUtil.isMock(this)

val Any.isSpy get() = MockUtil.isSpy(this)

inline fun <reified TException : Exception> expectException(block: () -> Unit) {
    try {
        block()
        fail("Expected exception of type ${TException::class.simpleName}, but no exception was thrown")
    } catch (e: Exception) {
        if (e !is TException) {
            fail("Wrong exception type.\n" +
                    "expected: ${TException::class.simpleName}\n" +
                    "actual: ${e::class.simpleName}")
        }
    }
}