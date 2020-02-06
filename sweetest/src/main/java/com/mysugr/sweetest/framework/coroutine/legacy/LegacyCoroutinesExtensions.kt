package com.mysugr.sweetest.framework.coroutine.legacy

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.TestingAccessor
import com.mysugr.sweetest.framework.coroutine.delayController
import com.mysugr.sweetest.framework.coroutine.uncaughtExceptionCaptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScope

/**
 * Imitates [kotlinx.coroutines.test.runBlockingTest] and uses the [TestCoroutineScope] provided by sweetest. You can
 * access [TestCoroutineScope] features via [coroutineScope], [delayController] and [uncaughtExceptionCaptor]
 *
 * There is a legacy mode that works with the previous sweetest solution (can be enabled by calling
 * `useLegacyCoroutineScope` on the configuration).
 */
@Suppress("EXPERIMENTAL_API_USAGE")
fun BaseJUnitTest.testCoroutine(
    testBody: suspend CoroutineScope.() -> Unit
) {
    accessor.testContext.coroutines.runTest {
        testBody(coroutineScope)
    }
}

val TestingAccessor.coroutineScope get() = accessor.testContext.coroutines.coroutineScope