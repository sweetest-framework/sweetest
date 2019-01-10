package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * Experimental
 */
class CoroutinesTestContext {
    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()
    val coroutineContext: CoroutineContext
        get() = coroutineDispatcher + supervisorJob + name

    suspend fun testFinished() {
        supervisorJob.cancelAndJoin()
    }

    companion object {
        val coroutineDispatcher: CoroutineDispatcher by lazy {
            Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        }
        private var instanceCounter = 0
    }
}