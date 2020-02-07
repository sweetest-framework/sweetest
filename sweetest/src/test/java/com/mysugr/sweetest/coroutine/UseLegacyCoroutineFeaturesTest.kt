package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.coroutineScope
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import com.mysugr.sweetest.framework.coroutine.legacy.coroutineScope as legacyCoroutineScope
import com.mysugr.sweetest.framework.coroutine.legacy.testCoroutine as legacyTestCoroutine

class UseLegacyCoroutineFeaturesTest : BaseJUnitTest(moduleTestingConfiguration()) {

    override fun configure() = super.configure()
        .useLegacyCoroutineScope(true)

    @Test
    fun `Uses legacy CoroutineScope`() = legacyTestCoroutine {
        assert(this !is TestCoroutineScope)
        assert(legacyCoroutineScope !is TestCoroutineScope)
    }

    @Test(expected = IllegalStateException::class)
    fun `Can't access original testCoroutine`() = testCoroutine {}

    @Test(expected = IllegalStateException::class)
    fun `Can't access original coroutineScope`() = legacyTestCoroutine {
        coroutineScope
    }
}