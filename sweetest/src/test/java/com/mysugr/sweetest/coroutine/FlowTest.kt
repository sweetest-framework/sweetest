package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.coroutineScope
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UncompletedCoroutinesError
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class FlowTest : BaseJUnitTest(moduleTestingConfiguration()) {

    @Test
    fun `Hot Flow with TestCoroutineScope is successful`() {
        val channel = BroadcastChannel<Int>(1)
        val emissions = mutableListOf<Int>()

        channel.asFlow()
            .onEach { emissions.add(it) }
            .launchIn(coroutineScope)
        channel.offer(1)

        assertEquals(1, emissions.size)
    }

    @Test(expected = UncompletedCoroutinesError::class)
    fun `Hot Flow produces error when not finished`() = testCoroutine {
        val channel = BroadcastChannel<Int>(1)
        val emissions = mutableListOf<Int>()

        channel.asFlow()
            .onEach { emissions.add(it) }
            .launchIn(coroutineScope)

        channel.offer(1)
        assertEquals(1, emissions.size)
    }
}