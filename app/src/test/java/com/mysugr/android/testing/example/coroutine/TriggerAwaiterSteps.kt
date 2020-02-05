package com.mysugr.android.testing.example.coroutine

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.coroutine.throwExceptionIfFailed
import com.mysugr.sweetest.framework.coroutine.verifyOrder
import kotlinx.coroutines.async
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class TriggerAwaiterSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    private val sut = TriggerAwaiter()

    private var triggerCompleted = false

    suspend fun startAwaitTrigger() {
        verifyOrder {
            order(1)

            val awaitJob = async {
                order(2)
                sut.awaitTrigger()
                triggerCompleted = true
                order(4)
            }

            order(3)

            awaitJob.throwExceptionIfFailed()
        }
    }

    fun trigger() {
        sut.trigger()
    }

    fun assertAwaitTriggerCompleted() = assertTrue(triggerCompleted)
    fun assertAwaitTriggerNotCompleted() = assertFalse(triggerCompleted)
}