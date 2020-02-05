package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.Steps
import com.mysugr.sweetest.framework.base.TestingAccessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.ContinuationInterceptor

@Suppress("EXPERIMENTAL_API_USAGE")
@Deprecated("Please migrate to `runBlockingSweetest`")
fun BaseJUnitTest.testCoroutine(
    testBody: suspend CoroutineScope.() -> Unit
) {
    check(accessor.testContext.coroutines.coroutineScope !is TestCoroutineScope) {
        "You are using the legacy CoroutineScope in this test, therefore `testCoroutine` can't be used. " +
            "Please use `runBlockingSweetest` instead or add `useLegacyCoroutineScope()` to the test or " +
            "steps configuration"
    }
    accessor.testContext.coroutines.runTest {
        testBody(coroutineScope)
    }
}

/**
 * Wrapper around [kotlinx.coroutines.test.runBlockingTest] that takes the [TestCoroutineScope] provided by sweetest
 */
@Suppress("EXPERIMENTAL_API_USAGE")
fun BaseJUnitTest.runBlockingSweetest(
    testBody: suspend CoroutineScope.() -> Unit
) {
    val scope = accessor.testContext.coroutines.coroutineScope
    check(scope is TestCoroutineScope) {
        "You are not using the legacy CoroutineScope in this test, therefore `testCoroutine` can't be used. " +
            "Please remove `useLegacyCoroutineScope()` from the test or steps configuration."
    }
    accessor.testContext.coroutines.runTest {
        testBody(scope)
    }
}

val TestingAccessor.coroutineScope get() = accessor.testContext.coroutines.coroutineScope

@Suppress("EXPERIMENTAL_API_USAGE")
val TestingAccessor.testCoroutineScope get() = accessor.testContext.coroutines.coroutineScope as TestCoroutineScope

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