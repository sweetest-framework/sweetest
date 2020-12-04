package com.mysugr.sweetest.framework.coroutine

import dev.sweetest.internal.TestContext
import dev.sweetest.internal.TestContextElement
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlin.coroutines.CoroutineContext

@Deprecated(TEST_UTILS_DEPRECATION_MESSAGE)
internal class CoroutinesTestContext : TestContextElement {

    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()

    @Suppress("DEPRECATION")
    val coroutineContext: CoroutineContext
        get() = SweetestCoroutineSupport.coroutineDispatcher + supervisorJob + name

    suspend fun testFinished() {
        supervisorJob.cancelAndJoin()
    }

    // Necessary for defining a TestContextElement:

    override val definition = Companion

    companion object : TestContextElement.Definition<CoroutinesTestContext> {
        private var instanceCounter = 0
        override fun createInstance(testContext: TestContext) = CoroutinesTestContext()
    }
}
