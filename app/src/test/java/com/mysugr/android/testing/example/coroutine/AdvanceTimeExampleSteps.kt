package com.mysugr.android.testing.example.coroutine

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseSteps
import com.mysugr.sweetest.framework.context.TestContext
import com.mysugr.sweetest.framework.coroutine.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class AdvanceTimeExampleSteps(testContext: TestContext) :
    BaseSteps(testContext, appModuleTestingConfiguration) {

    private lateinit var job: Job

    fun startDelay() {
        job = coroutineScope.launch {
            delay(1000)
        }
    }

    fun advanceTimeStep(delayTimeMillis: Long) {
        coroutineScope.advanceTimeBy(delayTimeMillis)
    }

    fun assertJobActive() {
        assertTrue(job.isActive)
    }

    fun assertJobNotActive() {
        assertFalse(job.isActive)
    }
}