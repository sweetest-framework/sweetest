package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlin.coroutines.CoroutineContext

@Deprecated(TEST_UTILS_DEPRECATION_MESSAGE)
internal class CoroutinesTestContext {

    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()

    @Suppress("DEPRECATION")
    val coroutineContext: CoroutineContext
        get() = SweetestCoroutineSupport.coroutineDispatcher + supervisorJob + name

    suspend fun testFinished() {
        supervisorJob.cancelAndJoin()
    }

    companion object {
        private var instanceCounter = 0
    }
}
