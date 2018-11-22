package com.mysugr.android.testing.example.coroutine

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.coroutine.assertSequence
import com.mysugr.sweetest.framework.coroutine.throwExceptionIfFailed
import kotlinx.coroutines.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import java.lang.AssertionError

class TriggerAwaiterSteps(testContext: TestContext)
    : BaseSteps(testContext, appModuleTestingConfiguration) {

    private val sut = TriggerAwaiter()

    private var triggerCompleted = false

    suspend fun startAwaitTrigger() {
        assertSequence {
            expect(1)

            val awaitJob = async {
                expect(3)
                sut.awaitTrigger()
                triggerCompleted = true
                expect(5)
            }

            expect(2)
            yield() // allow awaitJob to get into awaitTrigger fun
            expect(4)

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