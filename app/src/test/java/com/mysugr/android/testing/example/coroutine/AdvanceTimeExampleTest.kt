package com.mysugr.android.testing.example.coroutine

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.base.invoke
import com.mysugr.sweetest.framework.base.steps
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalCoroutinesApi
class AdvanceTimeExampleTest : BaseJUnitTest(appModuleTestingConfiguration) {

    private val advanceTimeSteps by steps<AdvanceTimeExampleSteps>()

    @Test
    fun `Advance time in test`() = testCoroutine {
        val job = launch {
            delay(1000)
        }

        advanceTimeBy(999)
        assertTrue(job.isActive)

        advanceTimeBy(1)
        assertFalse(job.isActive)
    }

    @Test
    fun `Advance time in steps`() = testCoroutine {
        val localJob = launch {
            delay(2000)
        }

        advanceTimeSteps {
            startDelay()

            advanceTimeStep(999)
            assertJobActive()

            advanceTimeStep(1)
            assertJobNotActive()
        }

        assertTrue(localJob.isActive)

        advanceTimeBy(1000)
        assertFalse(localJob.isActive)
    }
}
