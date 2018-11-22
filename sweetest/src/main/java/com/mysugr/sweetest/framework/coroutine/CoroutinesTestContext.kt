package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.newSingleThreadContext
import kotlin.coroutines.CoroutineContext

class CoroutinesTestContext {
    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()
    val coroutineContext: CoroutineContext
        get() = coroutineDispatcher + supervisorJob + name

    suspend fun testFinished() {
        supervisorJob.cancelAndJoin()
    }

    companion object {
        val coroutineDispatcher by lazy { newSingleThreadContext("testDispatcher") }
        private var instanceCounter = 0
    }
}