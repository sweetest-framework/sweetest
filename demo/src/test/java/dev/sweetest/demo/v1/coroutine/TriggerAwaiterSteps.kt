package dev.sweetest.demo.v1.coroutine

import dev.sweetest.demo.coroutine.TriggerAwaiter
import dev.sweetest.v1.BaseSteps
import dev.sweetest.internal.TestContext
import dev.sweetest.v1.coroutines.throwExceptionIfFailed
import dev.sweetest.v1.coroutines.verifyOrder
import kotlinx.coroutines.async
import kotlinx.coroutines.yield
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class TriggerAwaiterSteps(testContext: TestContext) : BaseSteps(testContext) {

    private val sut = TriggerAwaiter()

    private var triggerCompleted = false

    suspend fun startAwaitTrigger() {
        verifyOrder {
            order(1)

            val awaitJob = async {
                order(3)
                sut.awaitTrigger()
                triggerCompleted = true
                order(5)
            }

            order(2)
            yield() // allow awaitJob to get into awaitTrigger fun
            order(4)

            awaitJob.throwExceptionIfFailed()
        }
    }

    suspend fun trigger() {
        sut.trigger()
        yield() // allow awaitJob to complete
    }

    fun assertAwaitTriggerCompleted() = assertTrue(triggerCompleted)
    fun assertAwaitTriggerNotCompleted() = assertFalse(triggerCompleted)
}
