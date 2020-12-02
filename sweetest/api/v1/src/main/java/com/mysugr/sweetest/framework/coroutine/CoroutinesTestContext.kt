package com.mysugr.sweetest.framework.coroutine

import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.TestContextElement
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlin.coroutines.CoroutineContext

@Deprecated(TEST_UTILS_DEPRECATION_MESSAGE)
internal class CoroutinesTestContext : TestContextElement {

    private val name = CoroutineName("testCoroutine${InstanceCounter.instanceCounter++}")
    private val supervisorJob = SupervisorJob()

    @Suppress("DEPRECATION")
    val coroutineContext: CoroutineContext
        get() = SweetestCoroutineSupport.coroutineDispatcher + supervisorJob + name

    suspend fun testFinished() {
        supervisorJob.cancelAndJoin()
    }

    private object InstanceCounter {
        var instanceCounter = 0
    }

    // Necessary for defining a TestContextElement:
    override val key = Key
    companion object Key : TestContextElement.Key<CoroutinesTestContext> {
        override fun createInstance(testContext: TestContext) = CoroutinesTestContext()
    }
}
