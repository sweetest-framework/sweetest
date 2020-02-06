package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.UncompletedCoroutinesError
import org.junit.Test

class AutoCancelTestCoroutinesDisabledTest : BaseJUnitTest(moduleTestingConfiguration()) {

    override fun configure() = super.configure()
        .autoCancelTestCoroutines(false)

    @Test(expected = UncompletedCoroutinesError::class)
    fun `There will be a complaint about jobs still being active`() = testCoroutine {
        BroadcastChannel<Int>(1)
            .asFlow()
            .launchIn(this)
    }
}