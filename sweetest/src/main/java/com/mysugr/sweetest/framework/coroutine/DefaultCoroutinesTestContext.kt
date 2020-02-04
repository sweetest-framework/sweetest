package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.DelayController
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UncaughtExceptionCaptor
import kotlinx.coroutines.test.UncompletedCoroutinesError
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class DefaultCoroutinesTestContext(
    private val configuration: CoroutinesTestConfigurationData,
    /**
     * An optional context that MAY provide [UncaughtExceptionCaptor] and/or [DelayController] for the
     * TestCoroutineScope
     */
    coroutineContext: CoroutineContext = EmptyCoroutineContext
) : CoroutinesTestContext {

    override val coroutineScope = TestCoroutineScope(coroutineContext.checkArguments())

    override fun runTest(testBody: suspend () -> Unit) {
        val parentJob = checkNotNull(coroutineScope.coroutineContext[Job])
        val dispatcher = checkNotNull(coroutineScope.coroutineContext[ContinuationInterceptor] as? DelayController)

        val startingJobs = parentJob.activeJobs()
        val deferred = coroutineScope.async {
            testBody()
        }
        dispatcher.advanceUntilIdle()
        deferred.getCompletionExceptionOrNull()?.let {
            throw it
        }
        if (configuration.autoCancelTestCoroutinesEnabled) {
            runBlocking {
                parentJob.cancelAndJoin()
            }
        }
        coroutineScope.cleanupTestCoroutines()
        val endingJobs = parentJob.activeJobs()
        if ((endingJobs - startingJobs).isNotEmpty()) {
            throw UncompletedCoroutinesError("Test finished with active jobs: $endingJobs")
        }
    }
}

private fun CoroutineContext.checkArguments(): CoroutineContext {
    val dispatcher = get(ContinuationInterceptor).run {
        this?.let { require(this is DelayController) { "Dispatcher must implement DelayController: $this" } }
        this ?: TestCoroutineDispatcher()
    }

    val exceptionHandler = get(CoroutineExceptionHandler).run {
        this?.let {
            require(this is UncaughtExceptionCaptor) { "coroutineExceptionHandler must implement UncaughtExceptionCaptor: $this" }
        }
        this ?: TestCoroutineExceptionHandler()
    }

    val job = get(Job) ?: SupervisorJob()
    return this + dispatcher + exceptionHandler + job
}

private fun CoroutineContext.activeJobs(): Set<Job> {
    return checkNotNull(this[Job]).children.filter { it.isActive }.toSet()
}