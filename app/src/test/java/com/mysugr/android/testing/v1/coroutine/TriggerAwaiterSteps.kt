package com.mysugr.android.testing.v1.coroutine

import com.mysugr.android.testing.example.coroutine.TriggerAwaiter
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.TestContext
import com.mysugr.sweetest.framework.coroutine.throwExceptionIfFailed
import com.mysugr.sweetest.framework.coroutine.verifyOrder
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
