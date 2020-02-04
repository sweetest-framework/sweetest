package com.mysugr.sweetest.framework.coroutine

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class LegacyCoroutinesTestContext(
    private val configuration: CoroutinesTestConfigurationData
) : CoroutinesTestContext {

    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()
    private val coroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override val coroutineScope = CoroutineScope(coroutineDispatcher + supervisorJob + name)

    override fun runTest(testBody: suspend () -> Unit) {
        runBlocking {
            withContext(coroutineScope.coroutineContext) {
                testBody()
            }
            if (configuration.autoCancelTestCoroutinesEnabled) {
                supervisorJob.cancelAndJoin()
            }
        }
    }

    companion object {
        private var instanceCounter = 0
    }
}