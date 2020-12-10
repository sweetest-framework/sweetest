package dev.sweetest.v1.util

import dev.sweetest.v1.OUT_OF_SCOPE_DEPRECATION_MESSAGE
import org.junit.Assert.fail

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
