package com.mysugr.android.testing.example.coroutine

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import com.mysugr.sweetest.framework.coroutine.invoke
import com.mysugr.sweetest.util.expectException
import org.junit.Test

class TriggerAwaiterLegacyTest : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .useLegacyCoroutineScope()

    private val triggerAwaiter by steps<TriggerAwaiterLegacySteps>()

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