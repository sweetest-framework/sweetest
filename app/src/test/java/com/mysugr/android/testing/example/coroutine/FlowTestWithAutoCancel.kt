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
import org.junit.Assert.assertEquals

@ExperimentalCoroutinesApi
@FlowPreview
class FlowTestWithAutoCancel : BaseJUnitTest(appModuleTestingConfiguration) {

    override fun configure() = super.configure()
        .autoCancelTestCoroutines(true) // <-- !

    fun `In contrast to FlowTest without auto cancel, here no exception is expected`() = runBlockingSweetest {
        val channel = BroadcastChannel<Int>(1)
        val emissions = mutableListOf<Int>()

        channel.asFlow()
            .onEach { emissions.add(it) }
            .launchIn(testCoroutineScope)

        channel.offer(1)
        assertEquals(1, emissions.size)

        // After the test sweetest automatically cancels the job created by `launchIn`
    }
}