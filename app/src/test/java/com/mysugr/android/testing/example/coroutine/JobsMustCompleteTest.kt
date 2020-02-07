package com.mysugr.android.testing.example.coroutine

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UncompletedCoroutinesError
import org.junit.Assert.fail
import org.junit.Test

@ExperimentalCoroutinesApi
class JobsMustCompleteTest : BaseJUnitTest(appModuleTestingConfiguration) {

    @Test
    fun `testCoroutine throws if active jobs after end of function`() {
        try {
            testCoroutine {
                val neverCompleting = CompletableDeferred<Unit>()

                launch { neverCompleting.await() }
            }

            fail("Error should have been thrown")
        } catch (e: UncompletedCoroutinesError) {
            // expected
        }
    }

    @Test
    fun `running jobs can be cancelled`() = testCoroutine {
        val neverCompleting = CompletableDeferred<Unit>()
        val job = launch { neverCompleting.await() }

        job.cancel()
    }
}
