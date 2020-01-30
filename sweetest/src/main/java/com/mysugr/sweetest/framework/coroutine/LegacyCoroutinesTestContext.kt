package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class LegacyCoroutinesTestContext : CoroutinesTestContext {
    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()
    override val coroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    override val coroutineContext = coroutineDispatcher + supervisorJob + name

    override suspend fun finish() = runBlocking {
        supervisorJob.cancelAndJoin()
    }

    companion object {
        private var instanceCounter = 0
    }
}