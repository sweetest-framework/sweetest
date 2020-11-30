package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * Experimental
 */
internal class CoroutinesTestContext {
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
