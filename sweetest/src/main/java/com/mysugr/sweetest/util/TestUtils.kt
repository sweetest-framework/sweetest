package com.mysugr.sweetest.util

import org.junit.Assert.fail
import org.mockito.Mockito
import org.mockito.internal.util.MockUtil

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

val Any.isMock get() = MockUtil.isMock(this)

val Any.isSpy get() = MockUtil.isSpy(this)

// Internal for now as there is no decision yet whether to keep these assertions as public tools
internal inline fun <reified TThrowable : Throwable> assertThrown(block: () -> Unit) =
    expectException<TThrowable>(block)

inline fun <reified TThrowable : Throwable> expectException(block: () -> Unit) {
    try {
        block()
        fail("Expected throwable of type ${TThrowable::class.simpleName}, but not thrown.")
    } catch (e: Exception) {
        if (e !is TThrowable) {
            fail(
                "Unexpected throwable type.\n" +
                    "    Expected: ${TThrowable::class.simpleName}\n" +
                    "    Actual: ${e::class.simpleName}"
            )
        }
    }
}