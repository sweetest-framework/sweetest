package com.mysugr.sweetest.coroutine

import com.mysugr.sweetest.framework.base.BaseJUnitTest
import com.mysugr.sweetest.framework.configuration.moduleTestingConfiguration
import com.mysugr.sweetest.framework.coroutine.testCoroutine
import kotlinx.coroutines.Dispatchers
import org.junit.Test

class AutoSetMainDispatcherEnabledTest : BaseJUnitTest(moduleTestingConfiguration()) {

    override fun configure() = super.configure()
        .autoSetMainCoroutineDispatcher(true)

    @Test
    fun `Main dispatcher is set`() = testCoroutine {
        Dispatchers.Main // no exception thrown
    }
}