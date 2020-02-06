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
import org.junit.Assert.assertEquals

@ExperimentalCoroutinesApi
@FlowPreview
class FlowTestWithAutoCancel : BaseJUnitTest(moduleTestingConfiguration()) {

    override fun configure() = super.configure()
        .autoCancelTestCoroutines(true) // <-- !

    fun `In contrast to FlowTest without auto cancel, here no exception is expected`() = testCoroutine {
        val channel = BroadcastChannel<Int>(1)
        val emissions = mutableListOf<Int>()

        channel.asFlow()
            .onEach { emissions.add(it) }
            .launchIn(coroutineScope)

        channel.offer(1)
        assertEquals(1, emissions.size)

        // After the test sweetest automatically cancels the job created by `launchIn`
    }
}