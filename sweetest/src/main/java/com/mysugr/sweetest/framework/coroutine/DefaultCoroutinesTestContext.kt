package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncompletedCoroutinesError
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

internal class DefaultCoroutinesTestContext(private val configuration: CoroutinesTestConfigurationData) : CoroutinesTestContext {

    val job = SupervisorJob()
    override val coroutineScope = TestCoroutineScope(job)

    override fun runTest(testBody: suspend () -> Unit) {
        val dispatcher = checkNotNull(coroutineScope.coroutineContext[ContinuationInterceptor] as? DelayController)

        val startingJobs = job.activeJobs()
        val deferred = coroutineScope.async {
            testBody()
        }
        dispatcher.advanceUntilIdle()
        deferred.getCompletionExceptionOrNull()?.let {
            throw it
        }
        if (configuration.autoCancelTestCoroutinesEnabled) {
            runBlocking {
                job.cancelAndJoin()
            }
        }
        coroutineScope.cleanupTestCoroutines()
        val endingJobs = job.activeJobs()
        if ((endingJobs - startingJobs).isNotEmpty()) {
            throw UncompletedCoroutinesError("Test finished with active jobs: $endingJobs")
        }
    }
}

private fun CoroutineContext.activeJobs(): Set<Job> {
    return checkNotNull(this[Job]).children.filter { it.isActive }.toSet()
}