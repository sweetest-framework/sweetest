package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class LegacyCoroutinesTestContext : CoroutinesTestContext {
    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()
    private val coroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override val coroutineScope = CoroutineScope(coroutineDispatcher + supervisorJob + name)

    override fun cleanupCoroutines() = runBlocking {
        supervisorJob.cancelAndJoin()
    }

    companion object {
        private var instanceCounter = 0
    }
}