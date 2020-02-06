package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.Steps
import com.mysugr.sweetest.framework.base.TestingAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncaughtExceptionCaptor
import kotlinx.coroutines.yield

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

@ExperimentalCoroutinesApi
val TestingAccessor.delayController
    get() = coroutineScope as DelayController

@ExperimentalCoroutinesApi
val TestingAccessor.uncaughtExceptionCaptor
    get() = coroutineScope as UncaughtExceptionCaptor

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

/**
 * With multiple child jobs, you may want to yield multiple times to ensure each child can finish.
 * This function counts the number of child jobs and calls [yield] the number of times as jobs are present.
 */
@Suppress("SuspendFunctionOnCoroutineScope")
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