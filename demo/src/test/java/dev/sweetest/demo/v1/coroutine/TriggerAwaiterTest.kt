package dev.sweetest.demo.v1.coroutine

import dev.sweetest.v1.BaseJUnitTest
import dev.sweetest.v1.coroutines.invoke
import dev.sweetest.v1.coroutines.testCoroutine
import dev.sweetest.v1.steps
import dev.sweetest.v1.util.expectException
import org.junit.Test

class TriggerAwaiterTest : BaseJUnitTest() {

    private val triggerAwaiter by steps<TriggerAwaiterSteps>()

    @Test
    fun `awaitTrigger completes when triggered`() = testCoroutine {
        triggerAwaiter {
            startAwaitTrigger()
            trigger()
            assertAwaitTriggerCompleted()
        }
    }

    @Test
    fun `awaitTrigger does not complete when triggered before`() = testCoroutine {
        triggerAwaiter {
            trigger()
            startAwaitTrigger()
            assertAwaitTriggerNotCompleted()
        }
    }

    @Test
    fun `awaitTrigger two times throws exception`() = testCoroutine {
        triggerAwaiter {
            startAwaitTrigger()
            expectException<IllegalStateException> {
                startAwaitTrigger()
            }
        }
    }
}
