package com.mysugr.sweetest.framework.coroutine

suspend fun verifyOrder(block: suspend OrderVerifier.() -> Unit) {
    block(OrderVerifier())
}

class OrderVerifier {
    private var currentOrderIndex = 0

    fun order(orderIndex: Int) {
        currentOrderIndex++

        if (orderIndex != currentOrderIndex) {
            throw AssertionError("Expected order($currentOrderIndex), but reached order($orderIndex) instead")
        }
    }
}