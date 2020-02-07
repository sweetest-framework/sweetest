package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.Steps
import com.mysugr.sweetest.framework.base.TestingAccessor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.test.TestCoroutineScope

/**
 * Imitates [kotlinx.coroutines.test.runBlockingTest] and uses the [TestCoroutineScope] provided by sweetest. You can
 * access [TestCoroutineScope] features via [coroutineScope], [delayController] and [uncaughtExceptionCaptor]
 *
 * There is a legacy mode that works with the previous sweetest solution (can be enabled by calling
 * `useLegacyCoroutineScope` on the configuration). You have to use `testCoroutine` from the `legacy` sub-package in
 * oder for this to work.
 */
@Suppress("EXPERIMENTAL_API_USAGE")
fun BaseJUnitTest.testCoroutine(
    testBody: suspend TestCoroutineScope.() -> Unit
) {
    accessor.testContext.coroutines.runTest {
        testBody(coroutineScope)
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
val TestingAccessor.coroutineScope: TestCoroutineScope
    get() = accessor.testContext.coroutines.coroutineScope as? TestCoroutineScope
        ?: error(
            "Legacy CoroutineScope is enabled, so please use the `legacy` sub-package for accessing " +
                "`coroutineScope` or `testCoroutine`."
        )

suspend operator fun <T : Steps> T.invoke(run: suspend T.() -> Unit) = run(this)

suspend fun Deferred<*>.throwExceptionIfFailed() {
    if (isCompleted) {
        await() // throws exception, if Deferred failed. Does nothing otherwise
    }
}