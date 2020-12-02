package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.OUT_OF_SCOPE_DEPRECATION_MESSAGE
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.internal.Steps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

@Deprecated(TEST_UTILS_DEPRECATION_MESSAGE)
fun BaseJUnitTest.testCoroutine(
    testBlock: suspend CoroutineScope.() -> Unit
) {
    runBlocking {
        val coroutinesTestContext = testContext[CoroutinesTestContext]
        withContext(coroutinesTestContext.coroutineContext) {
            testBlock()
        }
        coroutinesTestContext.testFinished()
    }
}

suspend operator fun <T : Steps> T.invoke(run: suspend T.() -> Unit) = run(this)

@Deprecated(OUT_OF_SCOPE_DEPRECATION_MESSAGE)
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
@Deprecated(TEST_UTILS_DEPRECATION_MESSAGE)
suspend fun CoroutineScope.yieldForEachJob() {
    val job = coroutineContext[Job.Key] ?: error("coroutineContext doesn't have a parent Job.")
    repeat(countJobs(job)) { yield() }
}

private fun countJobs(job: Job): Int = 1 + job.children.sumBy { countJobs(it) }
