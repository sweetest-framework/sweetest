package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.coroutineScope
import com.mysugr.sweetest.framework.coroutine.legacy.testCoroutine
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import com.mysugr.sweetest.framework.coroutine.legacy.coroutineScope as legacyCoroutineScope
import com.mysugr.sweetest.framework.coroutine.legacy.testCoroutine as legacyTestCoroutine

class UseCoroutineFeaturesTest : BaseJUnitTest(moduleTestingConfiguration()) {

    override fun configure() = super.configure()
        .useLegacyCoroutineScope(false)

    @Test
    fun `Uses default TestCoroutineScope`() = testCoroutine {
        assert(this is TestCoroutineScope)
        assert(coroutineScope is TestCoroutineScope)
    }

    @Test
    fun `Can access legacy testCoroutine`() = legacyTestCoroutine {}

    @Test
    fun `Can access legacy coroutineScope`() = legacyTestCoroutine {
        legacyCoroutineScope
    }
}