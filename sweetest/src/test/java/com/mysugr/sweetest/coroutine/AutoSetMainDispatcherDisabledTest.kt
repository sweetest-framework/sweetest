package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.Test

class AutoSetMainDispatcherDisabledTest : BaseJUnitTest(moduleTestingConfiguration()) {

    override fun configure() = super.configure()
        .autoSetMainCoroutineDispatcher(false)

    @Test(expected = IllegalStateException::class)
    fun `Main dispatcher is not set`() = testCoroutine {
        withContext(Dispatchers.Main) { }
    }
}