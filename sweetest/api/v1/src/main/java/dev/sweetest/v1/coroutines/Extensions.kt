@file:Suppress("DEPRECATION")

package dev.sweetest.v1.coroutines

import dev.sweetest.v1.OUT_OF_SCOPE_DEPRECATION_MESSAGE
import dev.sweetest.v1.BaseJUnitTest
import dev.sweetest.internal.InternalBaseSteps
import dev.sweetest.v1.internal.coroutines.CoroutinesTestContext
import dev.sweetest.v1.COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

@Deprecated(COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE)
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

/**
 * Convenience function to allow DSL-like calls on steps class instances, e.g.:
 *
 * ```kotlin
 * val steps by steps<MyStepsClass>()
 *
 * @Test
 * fun test() {
 *     steps {
 *         callSomethingInTheStepsClass()
 *     }
 *
 *     // does the same
 *     steps.callSomethingInTheStepsClass()
 * }
 * ```
 */
@Deprecated(
    "You can now use the standard extension which works for both conventional and suspending functions",
    replaceWith = ReplaceWith("invoke", imports = ["dev.sweetest.v1.invoke"])
)
suspend operator fun <T : InternalBaseSteps> T.invoke(run: suspend T.() -> Unit) = run(this)

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
@Deprecated(COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE)
suspend fun CoroutineScope.yieldForEachJob() {
    val job = coroutineContext[Job.Key] ?: error("coroutineContext doesn't have a parent Job.")
    repeat(countJobs(job)) { yield() }
}

private fun countJobs(job: Job): Int = 1 + job.children.sumBy {
    countJobs(
        it
    )
}
