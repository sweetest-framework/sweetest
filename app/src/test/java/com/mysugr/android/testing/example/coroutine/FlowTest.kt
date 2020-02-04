package com.mysugr.android.testing.example.coroutine

import com.mysugr.android.testing.example.appModuleTestingConfiguration
import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.coroutine.runBlockingSweetest
import com.mysugr.sweetest.framework.coroutine.testCoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UncompletedCoroutinesError
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class FlowTest : BaseJUnitTest(appModuleTestingConfiguration) {

    @Test
    fun `Hot Flow with TestCoroutineScope is successful`() {
        val channel = BroadcastChannel<Int>(1)
        val emissions = mutableListOf<Int>()

        channel.asFlow()
            .onEach { emissions.add(it) }
            .launchIn(testCoroutineScope)
        channel.offer(1)

        assertEquals(1, emissions.size)
    }

    @Test(expected = UncompletedCoroutinesError::class)
    fun `Hot Flow produces error when not finished`() = runBlockingSweetest {
        val channel = BroadcastChannel<Int>(1)
        val emissions = mutableListOf<Int>()

        channel.asFlow()
            .onEach { emissions.add(it) }
            .launchIn(testCoroutineScope)

        channel.offer(1)
        assertEquals(1, emissions.size)
    }
}