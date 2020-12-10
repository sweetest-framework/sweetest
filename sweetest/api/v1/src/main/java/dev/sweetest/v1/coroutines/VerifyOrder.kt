@file:Suppress("DEPRECATION")

package dev.sweetest.v1.coroutines

import dev.sweetest.v1.COROUTINES_EAGER_EXECUTION_DEPRECATION_MESSAGE
import org.junit.Assert.fail

/**
 * Experimental
 * Verifies a specific call order. If the actual order is different from the expected one, an
 * [AssertionError] is thrown.
 * Successful example:
 * ```
 * verifyOrder {
 *   order(1) // is called first
 *
 *   launch {
 *     order(3) // this is called after yield()
 *   }
 *
 *   order(2) // this is called second
 *   yield()
 *   order(4) // this is called after the launch block
 * }
 * ```
 *
 * Failing example:
 * ```
 * verifyOrder {
 *   order(1)
 *   order(3) // this will throw an [AssertionError], because order(2) was expected here instead
 *   order(2)
 * }
 * ```
 */
@Deprecated(COROUTINES_EAGER_EXECUTION_DEPRECATION_MESSAGE)
suspend fun verifyOrder(block: suspend OrderVerifier.() -> Unit) {
    block(OrderVerifier())
}

/**
 * Helper class for [verifyOrder].
 */
@Deprecated(COROUTINES_EAGER_EXECUTION_DEPRECATION_MESSAGE)
class OrderVerifier {
    private var currentOrderIndex = 0

    fun order(orderIndex: Int) {
        currentOrderIndex++

        if (orderIndex != currentOrderIndex) {
            fail("Expected order($currentOrderIndex), but reached order($orderIndex) instead")
        }
    }
}
