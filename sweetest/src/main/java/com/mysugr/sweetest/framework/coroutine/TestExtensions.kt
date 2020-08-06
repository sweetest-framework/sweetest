package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.Steps
import com.mysugr.sweetest.framework.base.TestingAccessor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.ContinuationInterceptor

val TestingAccessor.testCoroutineScope: TestCoroutineScope
    get() = this.accessor.testContext.coroutines.testCoroutineScope

val TestingAccessor.coroutineDispatcher: CoroutineDispatcher
    get() = this.accessor.testContext.coroutines.testCoroutineScope
        .coroutineContext[ContinuationInterceptor] as CoroutineDispatcher

@Deprecated(
    "Legacy support only",
    replaceWith = ReplaceWith("testCoroutineScope.runBlockingTest", "kotlinx.coroutines.test", "")
)
fun BaseJUnitTest.testCoroutine(
    testBlock: suspend CoroutineScope.() -> Unit
) {
    runBlocking {
        val coroutinesTestContext = accessor.testContext.legacyCoroutines
        withContext(coroutinesTestContext.coroutineContext) {
            testBlock()
        }
        coroutinesTestContext.testFinished()
    }
}

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
suspend fun CoroutineScope.yieldForEachJob() {
    val job =
        coroutineContext[Job.Key] ?: kotlin.error("coroutineContext doesn't have a parent Job.")
    kotlin.repeat(countJobs(job)) { yield() }
}

private fun countJobs(job: Job): Int = 1 + job.children.sumBy { countJobs(it) }