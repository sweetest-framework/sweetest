package com.mysugr.sweetest.framework.coroutine.legacy

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.TestingAccessor
import com.mysugr.sweetest.framework.coroutine.delayController
import com.mysugr.sweetest.framework.coroutine.uncaughtExceptionCaptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.yield

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

/**
 * With multiple child jobs, you may want to yield multiple times to ensure each child can finish.
 * This function counts the number of child jobs and calls [yield] the number of times as jobs are present.
 */
@Suppress("SuspendFunctionOnCoroutineScope")
@Deprecated("Not needed for tests with TestCoroutineScope anymore")
suspend fun CoroutineScope.yieldForEachJob() {
    val job =
        this.coroutineContext[Job.Key] ?: error(
            "coroutineContext doesn't have a parent Job. Probably you are mistakenly using a TestCoroutineScope " +
                "(these don't have a `Job` by default) or you don't use sweetest's legacy CoroutineScope. Pleas bear " +
                "in mind that with runBlockingTest/Sweetest and TestCoroutineScope you don't need `yield` usually!"
        )
    kotlin.repeat(countJobs(job)) { yield() }
}

private fun countJobs(job: Job): Int = 1 + job.children.sumBy { countJobs(it) }