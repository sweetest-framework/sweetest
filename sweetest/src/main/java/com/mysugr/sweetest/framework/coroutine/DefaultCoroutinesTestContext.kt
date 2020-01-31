package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncaughtExceptionCaptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class DefaultCoroutinesTestContext(
    /**
     * An optional context that MAY provide [UncaughtExceptionCaptor] and/or [DelayController] for the TestCoroutineScope
     */
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) : CoroutinesTestContext {

    override val coroutineScope = TestCoroutineScope(coroutineContext)

    override fun cleanupCoroutines() {
        // runBlockingTest/Sweetest handles finishing, don't need to do anything here
    }
}