@file:Suppress("DEPRECATION")

package dev.sweetest.v1.internal.coroutines

import dev.sweetest.internal.TestContext
import dev.sweetest.internal.TestContextElement
import dev.sweetest.v1.COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE
import dev.sweetest.v1.coroutines.SweetestCoroutineSupport
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlin.coroutines.CoroutineContext

@Deprecated(COROUTINES_TEST_UTILS_DEPRECATION_MESSAGE)
internal class CoroutinesTestContext : TestContextElement {

    private val name = CoroutineName("testCoroutine${instanceCounter++}")
    private val supervisorJob = SupervisorJob()

    val coroutineContext: CoroutineContext
        get() = SweetestCoroutineSupport.coroutineDispatcher + supervisorJob + name

    suspend fun testFinished() {
        supervisorJob.cancelAndJoin()
    }

    // Necessary for defining a TestContextElement:

    override val definition = Companion

    companion object : TestContextElement.Definition<CoroutinesTestContext> {
        private var instanceCounter = 0
        override fun createInstance(testContext: TestContext) =
            CoroutinesTestContext()
    }
}
