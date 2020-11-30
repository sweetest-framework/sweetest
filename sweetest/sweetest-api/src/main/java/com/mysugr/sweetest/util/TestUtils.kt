package com.mysugr.sweetest.util

import org.junit.Assert.fail
import org.mockito.Mockito
import org.mockito.internal.util.MockUtil

private const val OUT_OF_SCOPE_DEPRECATION_MESSAGE =
    "Phased out after v1 as out-of-scope."

private const val MOCKITO_DEPRECATION_MESSAGE =
    "Phased out after v1 as sweetest becomes independent of specific mocking frameworks."

@Deprecated(MOCKITO_DEPRECATION_MESSAGE)
inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

@Deprecated(MOCKITO_DEPRECATION_MESSAGE)
val Any.isMock get() = MockUtil.isMock(this)

@Deprecated(MOCKITO_DEPRECATION_MESSAGE)
val Any.isSpy get() = MockUtil.isSpy(this)

@Deprecated(OUT_OF_SCOPE_DEPRECATION_MESSAGE)
inline fun <reified TException : Exception> expectException(block: () -> Unit) {
    try {
        block()
        fail("Expected exception of type ${TException::class.simpleName}, but no exception was thrown")
    } catch (e: Exception) {
        if (e !is TException) {
            fail(
                "Wrong exception type.\n" +
                    "expected: ${TException::class.simpleName}\n" +
                    "actual: ${e::class.simpleName}"
            )
        }
    }
}
