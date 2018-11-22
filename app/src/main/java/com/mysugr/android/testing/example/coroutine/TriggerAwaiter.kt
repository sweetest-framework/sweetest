package com.mysugr.android.testing.example.coroutine

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * [awaitTrigger] suspends until [trigger] is called.
 * [awaitTrigger] must only be called once.
 */
class TriggerAwaiter {
    private var currentContinuation: Continuation<Unit>? = null

    fun trigger() {
        currentContinuation?.resume(Unit)
    }

    suspend fun awaitTrigger() = suspendCancellableCoroutine<Unit> { continuation ->
        if (currentContinuation != null) {
            throw IllegalStateException("Sorry, only one awaitTrigger at a time")
        }

        currentContinuation = continuation
    }
}